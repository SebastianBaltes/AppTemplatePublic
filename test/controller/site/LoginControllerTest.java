package controller.site;

import static play.test.Helpers.*;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import controllers.forms.LoginForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.test.FakeRequest;

public class LoginControllerTest {

	private static final String ERR_FORM_BIND = "Fehler beim Ausfüllen des Formulars!";
	
//	@Test
	public void testAuthWithInvalidFormBinding() {
		
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				final Map<String, String> postData = new HashMap<String, String>();
				final FakeRequest fakeRequest = fakeRequest(POST, "/login");

				// no post data at all
				Result result = routeAndCall(fakeRequest.withFormUrlEncodedBody(postData));
				assertBadRequest(result, ERR_FORM_BIND);
				
				postData.put("blahh", "blubb");
				result = routeAndCall(fakeRequest.withFormUrlEncodedBody(postData));
				assertBadRequest(result, ERR_FORM_BIND);

				postData.put("email", "test");
				postData.put("password", "X");
				result = routeAndCall(fakeRequest.withFormUrlEncodedBody(postData));
				assertBadRequest(result, ERR_FORM_BIND);
				
				postData.put("email", "blubb");
				
			}
		});
	}
	
//	@Test
	public void testBinding() {
		 Form<LoginForm> f = Controller.form(LoginForm.class);
	}
	
	private void assertBadRequest(final Result r, final String flashMsg) {
		log("************ R=" + r);
		assertNotNull(r);
		assertEquals(BAD_REQUEST, status(r));
		
		if (flashMsg != null) {
			final String flash = flash(r).get("error");
			log("*************** FLASH=" + flash);
			assertTrue(flash.contains(flashMsg));
		}
	}

	private void log(final String msg) {
		System.err.println(getClass().getSimpleName() + ":" + msg);
	}

}
