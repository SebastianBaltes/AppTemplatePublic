package functional.test;

import static testutil.TestHelper.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

import java.util.HashMap;

import models.Role;
import models.User;
import org.junit.After;
import org.junit.Test;
import authenticate.Authenticated;
import com.avaje.ebean.Ebean;

import play.mvc.Result;
import play.test.FakeRequest;
import funcy.FunctionalTest;
import funcy.Page;

public abstract class AbstractLoginControllerTest extends FunctionalTest {
	
	public static final String BIND_ERR_MSG = "Fehler beim Ausfüllen des Formulars!";
	public static final String DEFAULT_ERR_MSG = "Benutzer oder Passwort falsch!"; 
	
	protected String mockOriginalRequestPath = null;
	
	public abstract String getControllerPath();
	public abstract String getRedirPathOnLogout();
	
	@After
	public void shutdownTest() {
		Page.clearSession();
	}
	
	@Test
	public void testInvalidBinding() {
		assertInvalidBinding("", "");
		assertInvalidBinding("email", "");
		assertInvalidBinding("", "secret");
		assertInvalidBinding("test@test.test", "");
		assertInvalidBinding("test@test.test", "X");
	}
	
	@Test
	public void testUnknownUser() {
		assertNull(User.find.byEmail("unknown@test.test"));
		assertInvalidPost("unknown@test.test", "test");
	}
	
	@Test
	public void testInvalidPassword() {
		assertNotNull(User.find.byEmail("test@test.test"));
		assertInvalidPost("test@test.test", "INVALID_PASS");
	}
	
	@Test
	public void testUserNotActivated() {
		Ebean.beginTransaction();
		
		final User u = User.find.byEmail("test@test.test");
		assertNotNull(u);
		u.setValidated(false);
		assertInvalidPost("test@test.test", "test");

		Ebean.endTransaction();
	}
	
	/**
	 * admins can login in site as well
	 */
	@Test
	public void testAdminLogin() {
		final User u = User.find.byEmail("admin@test.test");
		assertNotNull(u);
		assertEquals(Role.admin, u.getRole());
		testValidLogin(u, "admin");
	}

	@Test
	public void testRedirectLocation() {
		final String saveDir = mockOriginalRequestPath;
		try {
			mockOriginalRequestPath = "THIS_IS_A_WEIRD_LOCATION_ISNT_IT";
			testAdminLogin();
		}
		finally {
			mockOriginalRequestPath = saveDir;
		}
	}
	
	@Test
	public void testLogout() {
		final Result r = routeAndCall(fakeRequest(GET, getControllerPath() + "signout").withSession("dummy",	"data"));
		assertResultRedirect(r);
		assertTrue(session(r).size() == 0);
		
		final String redir = redirectLocation(r);
		assertNotNull(redir);
		assertEquals(getRedirPathOnLogout(), redir);
	}
	
	protected void testValidLogin(final User u, final String clearPassword) {
		assertNotNull(u);
		assertNotNull(clearPassword);
		
		final FakeRequest fakeRequest = fakeRequest(POST, getControllerPath() + "login").withFormUrlEncodedBody(
				(HashMap<String, String>) forMap("email", u.getEmail(), "password", clearPassword)).withSession("blah",
				"blubb");
		
		if (mockOriginalRequestPath != null) {
			fakeRequest.withSession(Authenticated.SESSION_KEY_REDIRECT_AFTER_AUTHENTICATION, mockOriginalRequestPath);
		}
		
		final Result r = routeAndCall(fakeRequest);
		
		assertResultRedirect(r);
		assertFlashEmpty(r);
		
		final String location = redirectLocation(r);
		assertNotNull(location);
		assertEquals(mockOriginalRequestPath == null ? "/" : mockOriginalRequestPath, location);
		
		final User authenticatedUser = Authenticated.getAuthenticatedUser(session(r));
		assertNotNull(authenticatedUser);
		assertTrue(authenticatedUser.getId().equals(u.getId()));
	}	
	
	protected Result assertInvalidPost(final String email, final String password) {
		Result r;
		r = Page.post(getControllerPath() + "login", (HashMap<String, String>) forMap("email", email, "password", password));
		assertResultBad(r);
		assertFlashWarning(r, DEFAULT_ERR_MSG);
		return r; 
	}
	
	protected Result assertInvalidBinding(final String email, final String password) {
		Result r;
		r = Page.post(getControllerPath() + "login", (HashMap<String, String>) forMap("email", email, "password", password));
		assertResultBad(r);
		assertFlashError(r, BIND_ERR_MSG);
		return r;
	}
	
}
