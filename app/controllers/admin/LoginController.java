package controllers.admin;

import play.mvc.Controller;
import play.mvc.Result;


public class LoginController extends Controller {

	public static Result index() {
		//FIXME: remember target url and perform redirect after authentification
		return  ok(views.html.admin.login.render());
	}
	
	public static Result authenticate() {
		return ok("authenticate");
	}
	
	
}
