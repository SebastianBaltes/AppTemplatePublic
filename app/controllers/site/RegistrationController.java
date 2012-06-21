package controllers.site;

import models.Role;
import models.User;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import play.Logger;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.MailHelper;

import authenticate.Authenticated;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;

import controllers.forms.FlashScope;
import controllers.forms.RegistrationForm;

public class RegistrationController extends Controller {

	//FIXME: move to application.conf
	private static final String SECRET_ACTIVATE_SALT = "1b062eefc47cfe1daa1d5b2a0ee478d5ecf6eb5456eac9f49601cc127873a1b6";
	private static Form<RegistrationForm> registrationForm = form(RegistrationForm.class);

	public static Result index() {
		return ok(views.html.site.registration.render(registrationForm));
	}

	public static Result register() {
		final Form<RegistrationForm> requestForm = registrationForm.bindFromRequest();
		if (requestForm.hasErrors()) {
			flash().put(FlashScope.ERROR, "Fehler beim Ausfüllen des Formulars!");
			return badRequest(views.html.site.registration.render(requestForm));
		}

		final RegistrationForm myForm = requestForm.get();
		Logger.debug("RegistrationController: got form=" + myForm);

		// FIXME: send success message even in this case and notify the existing
		// user
		// by mail about this incident.
		final User existingUser = User.findByEmail(myForm.getMandatory().getEmail());
		if (existingUser != null) {
			flash().put(FlashScope.ERROR, "Die Email-Adresse ist schon vergeben!");
			return badRequest(views.html.site.registration.render(requestForm));
//			flash().put(FlashScope.SUCCESS, "Registrierung erflogreich ! Bitte überprüfen Sie Ihr EMailpostfach.");
//			return ok(views.html.site.registration.render(requestForm));
		}

		if (!myForm.getMandatory().isPasswordMatching()) {
			flash().put(FlashScope.ERROR, "Die beiden Passwörter sind nicht gleich!");
			return badRequest(views.html.site.registration.render(requestForm));
		}

		final String activationHash = generateActivationHash(myForm.getMandatory().getEmail());
		Logger.debug("activationHash=" + activationHash + " for user=" + myForm.getMandatory().getEmail());

		final Html mailTemplate = views.html.email.confirmRegistration_text.render(myForm, activationHash);

		try {
			Ebean.execute(new TxRunnable() {

				@Override
				public void run() {
					final User newUser = myForm.buildUser();
					newUser.setPasswordHash(Authenticated.createHash(myForm.getMandatory().getPassword1()));
					newUser.setRole(Role.user);
					newUser.save();

					// FIXME: configure where ?
					try {
						final Email mail = MailHelper.createSimpleMail();
						mail.addTo(myForm.getMandatory().getEmail());
						mail.setSubject("Ihre Anmeldung");
						mail.setMsg(mailTemplate.toString());
						mail.send();
					} catch (final EmailException e) {
						throw new RuntimeException("Could not send registration confirmation mail due to =" + e, e);
					}
				}
			});
		} catch (final RuntimeException e) {
			Logger.error("Error while registering new user due to " + e, e);
			flash().put(FlashScope.ERROR, "Es ist ein Fehler bei der Registrierung aufgetreten, bitte versuchen Sie es in wenigen Augenblicken erneut!");
			return ok(views.html.site.registration.render(requestForm));
		}
		
		flash().put(FlashScope.SUCCESS, "Registrierung erflogreich ! Bitte überprüfen Sie Ihr EMailpostfach.");
		return ok(views.html.site.registration.render(requestForm));
	}
	
	public static Result activate(final String _email, final String _hash) {
		final User user = User.findByEmail(_email);
		if (user == null) {
			return badRequest("invalid link");
		}
		
		if (user.isValidated()) {
			return badRequest("invalid link");
		}
		
		// check hash ! 
		final String newHash = generateActivationHash(_email);
		if ( ! newHash.equals(_hash)) {
			return badRequest("invalid link");
		}
		
		user.setValidated(true);
		user.update();
		
		flash().put(FlashScope.SUCCESS, "Ihr Account wurde erfolgreich aktiviert, Sie können sich nun einloggen");
		return redirect(routes.LoginController.index());
	}
	
	private static String generateActivationHash(final String _email) {
		return Authenticated.createHash(_email + SECRET_ACTIVATE_SALT);
	}

}
