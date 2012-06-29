package functional.test;

//import static org.junit.Assert.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;
import static testutil.TestHelper.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import models.User;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import authenticate.Authenticated;

import play.mvc.Result;
import testutil.TestHelper;
import testutil.TestHelper.RedirCallback;
import utils.test.MailBucket;
import utils.test.MailBucket.Mail;
import utils.test.SimpleSmtpMock;
import functional.page.PasswordRecoverPage;
import funcy.FunctionalTest;
import funcy.Page;
import funcy.Tag;
import global.AppConfigResolver;

public class PasswordRecoverTest extends FunctionalTest {

	private static SimpleSmtpMock smtpMock;
	private static MailBucket mailBucket;

	public static final String EMAIL = "pille@palle.test";
	
	public long now; 

	@BeforeClass
	public static void setupSmtpMock() throws Exception {
		mailBucket = new MailBucket();
		smtpMock = new SimpleSmtpMock();

		smtpMock.addMailListener(mailBucket);
		smtpMock.start();
	}

	@AfterClass
	public static void shutdownSmtpMock() throws IOException {
		smtpMock.stop();
	}

	@Before
	public void setupTest() {
		now = System.currentTimeMillis();
	}

	@After
	public void shutdownTest() {
		Page.clearSession();
		mailBucket.clear();
	}

	@Test
	public void testIndex() {
		final Result r = PasswordRecoverPage.getIndex();
		TestHelper.assertResultOk(r);
		TestHelper.assertFlashEmpty(r);
		assertEquals(0, mailBucket.size());
	}

	@Test
	public void testIndexWithEmail() {
		final Result r = PasswordRecoverPage.getIndexWithEmail(EMAIL);
		TestHelper.assertResultOk(r);
		TestHelper.assertFlashEmpty(r);

		final PasswordRecoverPage page = new PasswordRecoverPage(r, EMAIL);
		final Tag inputTag = page.form(0).formTag.getElementsByTag("input").get(0);
		assertEquals(EMAIL, inputTag.attr("value"));
		assertEquals(0, mailBucket.size());
	}

	@Test
	public void testRecoverFromInvalidEmail() {
		final Result r = new PasswordRecoverPage(PasswordRecoverPage.getIndex()).doRecover("invalid_email");
		TestHelper.assertResultBad(r);
		TestHelper.assertFlashError(r, "Fehler beim Ausfüllen des Formulars!");
		assertEquals(0, mailBucket.size());
	}

	@Test
	public void testRecoverFromUnregisteredUser() {
		assertNull(User.find.byEmail("invalid_email@test.test"));

		// flash context is lost otherwise:
		Result r = routeAndCall(fakeRequest(POST, "/recover").withFormUrlEncodedBody(
				TestHelper.forMap("email", "invalid_email@test.test")));

		r = TestHelper.assertResultRedirecting(r, new RedirCallback() {
			@Override
			public void call(Result _r) {
				TestHelper.assertFlashSuccess(_r, "Eine Bestätigungsmail wurde an Sie versandt, bitte überprüfen Sie Ihr Postfach !");
			}
		});
		TestHelper.assertResultOk(r);
		assertEquals(0, mailBucket.size());
	}

	@Test
	public void testRecoverFromRegisteredUserWithMailingException() {
		final User u = ensureUserExists("test@test.test"); 
		smtpMock.setMockError(true);
		
		Result r = routeAndCall(fakeRequest(POST, "/recover").withFormUrlEncodedBody(
				TestHelper.forMap("email", "test@test.test")));

		r = TestHelper.assertResultRedirecting(r, new RedirCallback() {
			@Override
			public void call(Result _r) {
				TestHelper.assertFlashError(_r, "Es ist ein Fehler beim Mailversand aufgetreten");
			}
		});
		TestHelper.assertResultOk(r);
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}
	
	@Test
	public void testRecoverFromRegisteredUser() throws IOException, MessagingException {
		final User u = ensureUserExists("test@test.test"); 

		Result r = routeAndCall(fakeRequest(POST, "/recover").withFormUrlEncodedBody(
				TestHelper.forMap("email", "test@test.test")));

		r = TestHelper.assertResultRedirecting(r, new RedirCallback() {
			@Override
			public void call(Result _r) {
				TestHelper.assertFlashSuccess(_r, "Eine Bestätigungsmail wurde an Sie versandt, bitte überprüfen Sie Ihr Postfach !");
			}
		});

		TestHelper.assertResultOk(r);
		assertEquals(1, mailBucket.size());
		final Mail mail = mailBucket.get(0);
		final User reloadUser = ensureUserExists("test@test.test");
		
		assertNotSame(reloadUser, u);
		assertNotNull(reloadUser.getRandomPasswordRecoveryTriggerDate());
		assertTrue(reloadUser.getRandomPasswordRecoveryTriggerDate().getTime() >= now);
		assertNotNull(reloadUser.getRandomPasswordRecoveryString());
		
		final MimeMessage mime = mail.parse();
		assertNotNull(mime);
		
		final List<String> groups = TestHelper.matchGroups(mime.getContent().toString(), "http://(.*?)/recover/" +u.getEmail() +"/(.*)");
		assertNotNull(groups);
		assertEquals(3, groups.size());
		assertEquals(reloadUser.getRandomPasswordRecoveryString(), groups.get(2));
	}

	@Test
	public void testShowFormInvalidUser() {
		assertNull(User.find.byEmail("invalid_email@test.test"));
		final Result r = PasswordRecoverPage.getChangeForm("invalid_email@test.test", "invalid_uuid_value");
		assertBadResult(r);
		assertEquals(0, mailBucket.size());
	}	
	
	@Test
	public void testShowFormInvalidHash() {
		final User u = recoverRequestExistingUser("test@test.test");
		final Result r = PasswordRecoverPage.getChangeForm(u.getEmail(), u.getRandomPasswordRecoveryString()
				+ "_INVALID");
		
		assertBadResult(r);
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}
	
	@Test
	public void testShowFormLinkExpired() {
		final Long expireTime = AppConfigResolver.getPlain(AppConfigResolver.PASSWORD_RECOVER_LINK_VALID_MILLIS).asLong();
		final User u = recoverRequestExistingUser("test@test.test");
		u.setRandomPasswordRecoveryTriggerDate(new Timestamp(System.currentTimeMillis() - expireTime - 1 ));
		u.save();
		
		final Result r = PasswordRecoverPage.getChangeForm(u.getEmail(), u.getRandomPasswordRecoveryString());
		assertBadResult(r);
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}
	
	@Test
	public void testValidLink() {
		final User u = recoverRequestExistingUser("test@test.test");
		final Result r = PasswordRecoverPage.getChangeForm(u.getEmail(), u.getRandomPasswordRecoveryString());
		assertResultOk(r);
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}
	
	@Test
	public void testChangePasswordInvalidUser() {
		assertNull(User.find.byEmail("invalid_email@test.test"));
		final Result r= PasswordRecoverPage.doChangePassword("invalid_email@test.test", "xxxx", "xxxx", "uuid");
		assertBadResult(r);
		assertEquals(0, mailBucket.size());
	}
	
	@Test
	public void testChangePasswordInvalidHash() {
		final User u = recoverRequestExistingUser("test@test.test");
		final Result r = PasswordRecoverPage.doChangePassword(u.getEmail(), "xxxx", "xxxx", "uuid");
		assertBadResult(r);
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}	
	
	@Test
	public void testChangePasswordLinkExpired() {
		final User u = recoverRequestExistingUser("test@test.test");
		final Long expireTime = AppConfigResolver.getPlain(AppConfigResolver.PASSWORD_RECOVER_LINK_VALID_MILLIS).asLong();
		
		u.setRandomPasswordRecoveryTriggerDate(new Timestamp(System.currentTimeMillis() - expireTime - 1 ));
		u.save();
		
		final Result r = PasswordRecoverPage.doChangePassword(u.getEmail(), "xxxx", "xxxx", u.getRandomPasswordRecoveryString());
		assertBadResult(r);
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}		
	
	@Test
	public void testChangePasswordUnequalsPasswords() {
		final User u = recoverRequestExistingUser("test@test.test");
		final Result r = PasswordRecoverPage.doChangePassword(u.getEmail(), "xxxx", "yyyy", u.getRandomPasswordRecoveryString());
		assertResultBad(r);
		assertFlashError(r, "sind nicht gleich");
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}
	
	@Test
	public void testChangePasswordTooShortPasswords() {
		final User u = recoverRequestExistingUser("test@test.test");
		final Result r = PasswordRecoverPage.doChangePassword(u.getEmail(), "x", "x", u.getRandomPasswordRecoveryString());
		assertResultBad(r);
		assertFlashError(r, "Fehler beim Ausfüllen des Formulars!");
		assertEquals(0, mailBucket.size());
		assertUnchangedUser(u);
	}		
	
	@Test
	public void testChangePasswordSuccess() {
		final String newpw = "NEW_PASSWORD";
		final User u = recoverRequestExistingUser("test@test.test");
		final Result r = PasswordRecoverPage.doChangePassword(u.getEmail(), newpw, newpw, u.getRandomPasswordRecoveryString());
		
		//FIXME: switch to "get after post" in controller. 
		assertResultOk(r);
		assertFlashSuccess(r, "Passwort erfolgreich geändert! Sie können sich nun wieder einloggen");
		
		final User reloadUser = User.find.byEmail(u.getEmail());
		assertNotNull(reloadUser);
		assertFalse(u.getPasswordHash().equals(reloadUser.getPasswordHash()));
		assertEquals(Authenticated.createHash(newpw), reloadUser.getPasswordHash());
		assertNull(reloadUser.getRandomPasswordRecoveryString());
		assertNull(reloadUser.getRandomPasswordRecoveryTriggerDate());
	}	
	
	private User ensureUserExists(final String email) {
		final User u = User.find.byEmail(email);
		assertNotNull(u);
		return u;
	}
	
	private User recoverRequestExistingUser(final String email) {
		ensureUserExists(email);
		routeAndCall(fakeRequest(POST, "/recover").withFormUrlEncodedBody(TestHelper.forMap("email", email)));

		final User u = User.find.byEmail(email);
		assertNotNull(u.getRandomPasswordRecoveryString());
		assertEquals(1, mailBucket.size());
		mailBucket.clear();
		return u;
	}
	
	private static void assertBadResult(final Result r) {
		assertNotNull(r);
		assertEquals("text/plain", contentType(r));
		assertEquals("invalid link", contentAsString(r));
	}
	
	private void assertUnchangedUser(final User u1) {
		assertTrue(u1.equalsUser(User.find.byId(u1.getId())));
	}
	
}
