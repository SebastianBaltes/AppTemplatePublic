package controllers.site;

import java.util.ArrayList;
import java.util.List;

import models.ReportQuery;
import models.ReportQueryParameter;
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