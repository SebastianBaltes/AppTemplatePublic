package functional.page;

import java.util.HashMap;

import play.mvc.Result;
import testutil.TestHelper;
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
	
	public static Result getChangeForm(final String email, final String uuid) {
		return Page.get("/recover/" + email + "/" + uuid);
	}
	
	public static Result doChangePassword(final String email, final String password1, final String password2, final String uuid) {
		return Page.post("/recover/change/" + uuid, (HashMap<String, String>)TestHelper.forMap("email", email, "password1",
				password1, "password2", password2));
	}
	
	
	public Result doRecover(final String email) {
		final Form form = form(0);
		form.set("email", email);
		return form.submitName("Passwort wiederherstellen");
	}

}
