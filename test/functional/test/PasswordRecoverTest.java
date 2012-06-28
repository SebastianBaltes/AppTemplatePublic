package functional.test;

//import static org.junit.Assert.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

import java.io.IOException;

import models.User;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

public class PasswordRecoverTest extends FunctionalTest {

	private static SimpleSmtpMock smtpMock;
	private static MailBucket mailBucket;

	public static String EMAIL = "pille@palle.test";
	
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
	}

	@Test
	public void testIndexWithEmail() {
		final Result r = PasswordRecoverPage.getIndexWithEmail("pille@palle.test");
		TestHelper.assertResultOk(r);
		TestHelper.assertFlashEmpty(r);

		final PasswordRecoverPage page = new PasswordRecoverPage(r, "pille@palle.test");
		final Tag inputTag = page.form(0).formTag.getElementsByTag("input").get(0);
		assertEquals("pille@palle.test", inputTag.attr("value"));
	}

	@Test
	public void testRecoverFromInvalidEmail() {
		final Result r = new PasswordRecoverPage(PasswordRecoverPage.getIndex()).doRecover("invalid_email");
		TestHelper.assertResultBad(r);
		TestHelper.assertFlashError(r, "Fehler beim Ausfüllen des Formulars!");
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
		final User u = User.find.byEmail("test@test.test"); 
		assertNotNull(u);
		
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
	}
	
	@Test
	public void testRecoverFromRegisteredUser() {
		final User u = User.find.byEmail("test@test.test"); 
		assertNotNull(u);

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
		final User reloadUser = User.find.byEmail("test@test.test");
		
		assertNotSame(reloadUser, u);
		assertNotNull(reloadUser.getRandomPasswordRecoveryTriggerDate());
		assertTrue(reloadUser.getRandomPasswordRecoveryTriggerDate().getTime() >= now);
		assertNotNull(reloadUser.getRandomPasswordRecoveryString());
		//FIXME: assert mail content and link 
	}
	
	
}
