package controllers.admin;

import authenticate.admin.AdminSecured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.*;


@Security.Authenticated(AdminSecured.class)
public class Application extends Controller {
  
  public static Result index() {
    return ok(index.render());
  }
  
  public static Result login() {
	    return ok(login.render());
  }
  
}