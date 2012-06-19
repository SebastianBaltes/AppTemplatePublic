package controllers.admin;

import static controllers.forms.FlashScope.ERROR;
import static controllers.forms.FlashScope.WARN;
import authenticate.Authenticated;
import models.User;

import controllers.forms.LoginForm;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;


public class LoginController extends Controller {

	private static Form<LoginForm> loginForm = form(LoginForm.class); 
	
	public static Result index() {
		return  ok(views.html.admin.login.render(loginForm));
	}
	
	public static Result authenticate() {
		
		final Form<LoginForm> bindForm = loginForm.bindFromRequest();
		Logger.info("LoginController::authenticate " + bindForm);
		
		if (bindForm.hasErrors()) {
			flash(ERROR, "Fehler beim Ausfüllen des Formulars!");
			return redirect(routes.LoginController.index());
		}
		
		final LoginForm myForm = bindForm.get();
		Logger.info("LoginController: got valid form binding=" + myForm);
		
		// find user by email. 
		final User loginUser = User.findByEmail(myForm.getUserName());
		if (loginUser == null) {
			Logger.info("LoginController:: user not found for loginName=" + myForm.getUserName());
			flash(WARN, "Benutzer oder Passwort falsch!");
			return redirect(routes.LoginController.index());
		}
		
		// password matches ? 
		if ( ! Authenticated.isCorrectPasswordForLogin(loginUser, myForm.getPassword())) {
			Logger.info("LoginController:: invalid password for user=" + loginUser);
			flash(WARN, "Benutzer oder Passwort falsch!");
			return redirect(routes.LoginController.index());
		}

		if (! loginUser.getRole().isAdminRole()) {
			Logger.info("LoginController:: login denied for non-admin user=" + loginUser);
			flash(WARN, "Benutzer oder Passwort falsch!");
			return redirect(routes.LoginController.index());
		}
		
		Authenticated.loginUser(loginUser);
		
		final String newPath = Authenticated.getOrgRequestPathOnUnauthorized() != null ?
				Authenticated.getOrgRequestPathOnUnauthorized() : "/admin";
				
		return redirect(newPath);
	}
	

	
	
}
