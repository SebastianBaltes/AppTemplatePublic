package controllers.site;

import javax.swing.JOptionPane;

import global.AppConfigResolver;
import models.Role;
import models.User;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
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

		//send success message even in this case and notify the existing
		// user by mail about this incident.
		final User existingUser = User.findByEmail(myForm.getMandatory().getEmail());
		if (existingUser != null) {
			try {
				sendDoubleRegistrationNotificationMail(existingUser);
				flash().put(FlashScope.SUCCESS, "Registrierung erflogreich ! Bitte überprüfen Sie Ihr EMailpostfach.");
			} catch (final EmailException e) {
				Logger.error("Could not send DoubleRegistrationNotificationMail confirmation mail due to =" + e, e);
				flash().put(FlashScope.ERROR,
						"Es ist ein Fehler bei der Registrierung aufgetreten, bitte versuchen Sie es in wenigen Augenblicken erneut!");
			}
			return ok(views.html.site.registration.render(requestForm));
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
	
	private static void sendDoubleRegistrationNotificationMail(final User user) throws EmailException{
		final Email mail = MailHelper.createSimpleMail();
		mail.addTo(user.getEmail());
		mail.setSubject("Doppelte Anmeldung");
		mail.setMsg(views.html.email.doubleRegistrationNotificationMail_text.render(user).toString());
		
		Logger.info("about to send DoubleRegistrationNotificationMail=" + ReflectionToStringBuilder.toString(mail));
		mail.send();
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
		return Authenticated.createHash(_email
				+ AppConfigResolver.getPlain(AppConfigResolver.ACTIVATE_ACCOUNT_SECRET_SALT));
	}

}
