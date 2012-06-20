package controllers.site;

import controllers.forms.EMailForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;


public class PasswordRecoverController extends Controller {

	private static Form<EMailForm> emailForm = form(EMailForm.class);
	
	public static Result indexDefault() {
		return ok(views.html.site.pwrecover.render(emailForm));
	}	
	
	public static Result index(final String email) {
		return ok(views.html.site.pwrecover.render(emailForm.fill(new EMailForm(email))));
	}
	
	public static Result recover() {
		return ok("");
	}
	
}
