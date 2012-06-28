package functional.page;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import play.mvc.Result;
import funcy.Form;
import funcy.Page;

public class RegistrationPage extends Page {

	public RegistrationPage(Result _result) {
		super(_result, "/register");
	}

	public Result doRegisterMinimal(final String email, final String password1, final String password2) {
		final Form form = form(0);
		form.set("mandatory.email", email);
		form.set("mandatory.password1", password1);
		form.set("mandatory.password2", password2);
		form.set("optionalUserProfileForm.countryListIndex", "");

		return form.submitName("Registrieren");
	}

	public Result doRegisterOptional(final String email, final String password1, final String password2,
		final String firstname, final String surname, final String address, final String countryListIndex,
		final String zipCode, final String city) {

		final Form form = form(0);
		form.set("mandatory.email", email);
		form.set("mandatory.password1", password1);
		form.set("mandatory.password2", password2);

		form.set("optionalUserProfileForm.firstname", firstname);
		form.set("optionalUserProfileForm.surname", surname);
		form.set("optionalUserProfileForm.address", address);
		form.set("optionalUserProfileForm.countryListIndex", countryListIndex);
		form.set("optionalUserProfileForm.zipCode", zipCode);
		form.set("optionalUserProfileForm.city", city);

		return form.submitName("Registrieren");
	}

	public static Result doActivate(final String email, final String hash) {
		try {
			return Page.get("/register/activate/" + URLEncoder.encode(email, "UTF-8") + "/"
					+ URLEncoder.encode(hash, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
