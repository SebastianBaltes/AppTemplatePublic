package controllers.admin;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.*;


public class Application extends Controller {
  
  public static Result index() {
    return ok(index.render());
  }
  
}