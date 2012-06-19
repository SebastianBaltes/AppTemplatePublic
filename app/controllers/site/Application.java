package controllers.site;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.site.*;

public class Application extends Controller {
  
  public static Result index() {
    return ok(index.render());
  }
  
}