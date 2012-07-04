package functional.test;

import static testutil.TestHelper.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

import java.util.HashMap;

import javax.swing.JOptionPane;

import models.User;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.After;
import org.junit.Test;

import authenticate.Authenticated;

import com.avaje.ebean.Ebean;

import controllers.forms.FlashScope;

import play.Application;
import play.api.test.Helpers;
import play.mvc.Result;
import play.mvc.Http.Flash;
import funcy.FunctionalTest;
import funcy.Page;

/**
 * 
 * FIXME: assert none authenticated 
 * 
 */
public class SiteLoginControllerTest extends FunctionalTest {
	
	public static final String BIND_ERR_MSG = "Fehler beim Ausfüllen des Formulars!";
	public static final String DEFAULT_ERR_MSG = "Benutzer oder Passwort falsch!"; 
		
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
	
	@Test
	public void testLogin() {
		Result r;
		final User u = User.find.byEmail("test@test.test");
		assertNotNull(u);
		
		r = Page.post("/login", (HashMap<String, String>) forMap("email", "test@test.test", "password", "test"));
		assertResultOk(r);
		assertFlashEmpty(r);
		
		//FIXME: does not work this way
//		final User authenticatedUser = Authenticated.getAuthenticatedUser();
//		assertNotNull(authenticatedUser);
//		assertTrue(authenticatedUser.getId().equals(u.getId()));
	}
	
	private Result assertInvalidPost(final String email, final String password) {
		Result r;
		r = Page.post("/login", (HashMap<String, String>) forMap("email", email, "password", password));
		assertResultBad(r);
		assertFlashWarning(r, DEFAULT_ERR_MSG);
		return r; 
	}
	
	private Result assertInvalidBinding(final String email, final String password) {
		Result r;
		r = Page.post("/login", (HashMap<String, String>) forMap("email", email, "password", password));
		assertResultBad(r);
		assertFlashError(r, BIND_ERR_MSG);
		return r;
	}
	
}
