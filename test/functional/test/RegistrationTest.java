package functional.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;
import static play.test.Helpers.contentAsString;
import play.mvc.Result;

import testutil.TestHelper;
import static testutil.TestHelper.assertPasswordHash;
import static testutil.TestHelper.log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import models.Role;
import models.User;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import authenticate.Authenticated;


import utils.CountryHelper;
import utils.test.MailBucket;
import utils.test.SimpleSmtpMock;

import functional.page.RegistrationPage;
import funcy.FunctionalTest;
import funcy.Page;
import global.AppConfigResolver;

public class RegistrationTest extends FunctionalTest {

	private RegistrationPage page;
	private int currentRowCount;
	private long now; 
	
	private static SimpleSmtpMock smtpMock;
	private static MailBucket mailBucket;
	
	
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
		page = new RegistrationPage(Page.get("/register"));
		currentRowCount = TestHelper.getRowCount(User.class);
		now = System.currentTimeMillis();
	}
	
	@After
	public void shutdownTest() {
		Page.clearSession();
		mailBucket.clear();
	}
	
	@Test
	public void testPositiveMinimalData() {
		log("testPositiveMinimalData");

		final Result r = page.doRegisterMinimal("test1@test.test", "xxxx", "xxxx");
		log("result=" + r);

		page = new RegistrationPage(r);
		TestHelper.assertResultOk(r);
		TestHelper.assertFlashSuccess(r, "Registrierung erfolgreich");

		assertIncrementedUserAccountBy(1);

		final User u = TestHelper.findLast(User.class);
		log("found user=" + ReflectionToStringBuilder.toString(u));
		
		assertNewUser(u);
		assertEquals("test1@test.test", u.getEmail());
		assertPasswordHash("xxxx", u);
		assertRegistrationMail(u.getEmail());
	}
	
	@Test
	public void testDuplicateRegistration() {
		log("testDuplicateRegistration");
		
		final Result r = page.doRegisterMinimal("test1@test.test", "xxxx", "xxxx");
		log("result=" + r);
		
		page = new RegistrationPage(r);
		TestHelper.assertResultOk(r);
		TestHelper.assertFlashSuccess(r, "Registrierung erfolgreich");
		
		assertIncrementedUserAccountBy(0);
		assertDuplicateRegisterMail("test1@test.test");
	}	

	@Test
	public void testPositiveMaximalData() {
		log("testPositiveMaximalData");

		final Result r = page.doRegisterOptional("test2@test.test", "yyyy", "yyyy", "Hans", "Wurst", "address", "2",
				"9999", "Dortmund");

		log("result=" + r);
		page = new RegistrationPage(r);
		
		TestHelper.assertResultOk(r);
		TestHelper.assertFlashSuccess(r, "Registrierung erfolgreich");

		assertIncrementedUserAccountBy(1);

		final User u = TestHelper.findLast(User.class);
		log("found user=" + ReflectionToStringBuilder.toString(u));
		
		assertNewUser(u);
		assertEquals("test2@test.test", u.getEmail());
		assertPasswordHash("yyyy", u);
		assertEquals("Hans", u.getFirstname());
		assertEquals("Wurst", u.getSurname());
		assertEquals("address", u.getAddress());
		assertEquals("9999", u.getZipCode());
		assertEquals("Dortmund", u.getCity());
		assertEquals(CountryHelper.countryList.get(2), u.getCountry());
		
		assertRegistrationMail(u.getEmail());		
	}

	@Test
	public void testInvalidEmailAddress() {
		log("testInvalidEmailAdress");
		testInvalids("InvalidEmail", "xxxx", "xxxx", "Fehler beim Ausfüllen");
	}
	
	@Test
	public void testNoEmailAddress() {
		log("testNoEmailAddress");
		testInvalids("", "xxxx", "xxxx", "Fehler beim Ausfüllen");
	}
	
	@Test
	public void testUnequalPasswords() {
		log("testUnequalPasswords");
		testInvalids("yomama@test.test", "xxxx", "YYYY", "sind nicht gleich");
	}
	
	@Test
	public void testPasswordsTooShort() {
		log("testPasswordsTooShort");
		testInvalids("yomama@test.test", "A", "A", "Fehler beim Ausfüllen");
	}
	
	@Test
	public void testActivationInvalidUser() {
		log("testActivationInvalidUser");
		for (final String user : Arrays.asList("UNKNOWN_STRING", "UNKNOWN_EMAIL@test.test")) {
			final Result r = RegistrationPage.doActivate(user, "this_is_an_invalid_hash_code");
			assertActivationInvalid(r);
			assertNull(User.find.byEmail(user));
		}
	}
	
	@Test
	public void testActivationInvalidCode() {
		log("testActivationInvalidCode");
		final User u = User.find.byEmail("test1@test.test");
		assertNotNull(u);
		assertFalse(u.isValidated());
		
		final Result r = RegistrationPage.doActivate("test1@test.test", "this_is_an_invalid_hash_code");
		assertActivationInvalid(r);
		assertUserValidated(u, false);
	}
	
	@Test
	public void testActivationSuccess() {
		log("testActivationSuccess");
		final User u = User.find.byEmail("test1@test.test");
		assertNotNull(u);
		assertFalse(u.isValidated());

		TestHelper.sleep(10);
		final Result r = callValidActivation(u);
		TestHelper.assertResultOk(r);
		
		final User reloadUser = assertUserValidated(u, true);
		assertTrue(reloadUser.getLastUpdate().getTime() > u.getLastUpdate().getTime());
	}
	
	@Test
	public void testActivationAlreadyDone() {
		log("testActivationAlreadyDone");
		final User u = User.find.byEmail("test1@test.test");
		assertNotNull(u);
		assertTrue(u.isValidated());
		
		TestHelper.sleep(10);
		final Result r = callValidActivation(u);
		assertActivationInvalid(r);
		
		final User reloadUser = assertUserValidated(u, true);
		EqualsBuilder.reflectionEquals(reloadUser, u);
	}
	
	private Result callValidActivation(final User u) {
		return RegistrationPage.doActivate(
				u.getEmail(),
				Authenticated.createHash(u.getEmail()
						+ AppConfigResolver.getPlain(AppConfigResolver.ACTIVATE_ACCOUNT_SECRET_SALT)));
	}
	
	private User assertUserValidated(final User orgUser, final boolean shouldBeValidated) {
		assertNotNull(orgUser);
		final User u = User.find.byId(orgUser.getId());
		assertNotNull(u);
		assertNotSame(orgUser, u);
		assertEquals(shouldBeValidated, u.isValidated());
		return u; 
	}
	
	private void assertActivationInvalid(final Result r) {
		assertNotNull(r);
		assertEquals(BAD_REQUEST, status(r));
		assertEquals("text/plain", contentType(r));
		assertEquals("invalid link", contentAsString(r));
	}
	
	private void assertNewUser(final User u) {
		assertNotNull(u);
		assertEquals(false, u.isValidated());
		assertEquals(Role.user, u.getRole());
		assertEquals("Europe/Berlin", u.getTimezone());
		
		assertNotNull(u.getLastUpdate());
		assertTrue(u.getLastUpdate().getTime() >= now);
	}	
	
	private void testInvalids(String email, String pw1, String pw2, String errMsg) {
		final Result r = page.doRegisterMinimal(email, pw1, pw2);
		log("result=" + r);
		new RegistrationPage(r);
		assertInvalid(r, errMsg);
	}
	
	private void assertInvalid(final Result r, final String containsMessage) {
		TestHelper.assertResultBad(r);
		TestHelper.assertFlashError(r, containsMessage);
		assertIncrementedUserAccountBy(0);
		assertEquals(0, mailBucket.size());
	}
	
	private void assertIncrementedUserAccountBy(final int count) {
		assertEquals(currentRowCount + count, TestHelper.getRowCount(User.class));
	}
	
	private void assertRegistrationMail(final String userEmail) {
		assertEquals(1, mailBucket.size());
		final MailBucket.Mail mail = mailBucket.get(0);
		
		final List<String> groups = TestHelper.matchGroups(mail.body, "http://(.*?)/register/activate/(.*?)/[a-z0-9]*");
		assertNotNull(groups);
		assertEquals(3, groups.size());
		assertEquals(userEmail, groups.get(2));
	}	
	
	private void assertDuplicateRegisterMail(final String userEmail) {
		assertEquals(1, mailBucket.size());
		final MailBucket.Mail mail = mailBucket.get(0);
		
		final List<String> groups = TestHelper.matchGroups(mail.body, "http://(.*?)/recover/(.*?)/");
		assertNotNull(groups);
		assertEquals(3, groups.size());
		assertEquals(userEmail, groups.get(2));
	}

}
