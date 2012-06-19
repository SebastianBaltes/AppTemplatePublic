package controllers.admin;

import static controllers.forms.FlashScope.ERROR;
import static controllers.forms.FlashScope.WARNING;;
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
		return  ok(views.html.admin.login.render());
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
			flash(WARNING, "Benutzer oder Passwort falsch!");
			return redirect(routes.LoginController.index());
		}
		
		// FIXME: implement me !! 
		// password matches ? 
		if ( ! myForm.getPassword().equals("123456")) {
			Logger.info("LoginController:: invalid password=" + myForm.getPassword()+ " for user=" + loginUser);
			flash(WARNING, "Benutzer oder Passwort falsch!");
			return redirect(routes.LoginController.index());
		}
		
		// find original request path. 
		final String newPath = Authenticated.getOrgRequestPathOnUnauthorized() != null ?
				Authenticated.getOrgRequestPathOnUnauthorized() : "/admin";
				
		return redirect(newPath);
	}
	
	
}
