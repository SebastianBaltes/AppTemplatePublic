package controllers.site;

import authenticate.site.SiteSecured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.site.*;


public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}
	
	@Security.Authenticated(SiteSecured.class)
	public static Result dummy() {
		return ok("SITE::DUMMY");
	}

}