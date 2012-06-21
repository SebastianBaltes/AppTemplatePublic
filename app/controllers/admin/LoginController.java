package controllers.admin;

import static controllers.forms.FlashScope.WARN;
import models.Role;
import models.User;

import controllers.DefaultLoginController;
import controllers.forms.FlashScope;
import controllers.forms.LoginForm;
import play.Logger;
import play.mvc.Result;
import play.api.templates.Html;
import play.data.Form;

public class LoginController extends DefaultLoginController {

	public static Result index() {
		return ok(views.html.admin.login.render(loginForm));
	}

	public static Result authenticate() {
		final LoginLogicCallback c = new LoginLogicCallback() {

			@Override
			public Result perform(final User loginUser, final Form<LoginForm> _form) {
				if (loginUser.getRole() != Role.admin) {
					Logger.info("admin.LoginController:: login denied for non-admin user=" + loginUser);
					flash(WARN, "Benutzer oder Passwort falsch!");
					return badRequest(getErrorHtml(_form));
				}
				return null;
			}

			@Override
			public Html getErrorHtml(Form<LoginForm> _form) {
				return views.html.admin.login.render(_form);
			}
		};
		return authenticate(c);
	}
	
	public static Result signOut() {
		session().clear();
		flash().put(FlashScope.SUCCESS, "Sie wurden ausgeloggt.");
		return redirect(routes.Application.index());
	}

}
