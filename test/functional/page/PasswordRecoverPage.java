package functional.page;

import play.mvc.Result;
import funcy.Form;
import funcy.Page;

public class PasswordRecoverPage extends Page {

	public PasswordRecoverPage(Result _result) {
		super(_result, "/recover");
	}

	public PasswordRecoverPage(Result _result, final String email) {
		super(_result, "/recover/" + email);
	}

	public static Result getIndexWithEmail(final String email) {
		return Page.get("/recover/" + email);
	}

	public static Result getIndex() {
		return Page.get("/recover");
	}

	public Result doRecover(final String email) {
		final Form form = form(0);
		form.set("email", email);
		return form.submitName("Passwort wiederherstellen");
	}

}
