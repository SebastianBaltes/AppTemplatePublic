package controllers.site;

import java.sql.Timestamp;

import global.AppConfigResolver;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.mail.SimpleEmail;

import authenticate.Authenticated;

import models.User;
import play.Logger;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.forms.EMailForm;
import controllers.forms.FlashScope;
import controllers.forms.PasswordChangeForm;

/*
 * FIXME: WHAT ABOUT HTTPS ? 
 */
public class PasswordRecoverController extends Controller {

	private static Form<EMailForm> emailForm = form(EMailForm.class);
	private static Form<PasswordChangeForm> passwordChangeForm = form(PasswordChangeForm.class);

	public static Result indexDefault() {
		return ok(views.html.site.pwrecover.render(emailForm));
	}

	public static Result index(final String email) {
		return ok(views.html.site.pwrecover.render(emailForm.fill(new EMailForm(email))));
	}

	public static Result recover() {
		final Form<EMailForm> requestForm = emailForm.bindFromRequest();
		if (requestForm.hasErrors()) {
			flash().put(FlashScope.ERROR, "Fehler beim Ausfüllen des Formulars!");
			return badRequest(views.html.site.pwrecover.render(requestForm));
		}

		final EMailForm myForm = requestForm.get();
		final User user = User.findByEmail(myForm.getEmail());

		if (user == null) {
			Logger.info("PasswordRecoverController: recovery attempt for unknown email address=" + myForm);
			flash().put(FlashScope.SUCCESS, "Bitte überprüfen Sie Ihr Emailpostfach !");
			return redirect(routes.PasswordRecoverController.index(myForm.getEmail()));
		}

		Logger.info("starting recover process for user=" + user);

		final String randUUID = RandomStringUtils.random(
				AppConfigResolver.getPlain(AppConfigResolver.PASSWORD_RECOVER_UUID_LENGTH).asInt(), true, true);

		user.setRandomPasswordRecoveryString(randUUID);
		user.setRandomPasswordRecoveryTriggerDate(new Timestamp(System.currentTimeMillis()));
		user.save();

		final Html mailTemplate = views.html.email.recoverPassword_text.render(user);

		// FIXME: where to configure stuff ?
		try {
			final SimpleEmail mail = new SimpleEmail();
			mail.setHostName(AppConfigResolver.get(AppConfigResolver.SMTP_HOST).toString());
			mail.setSmtpPort(AppConfigResolver.get(AppConfigResolver.SMTP_PORT).asInt());

			mail.setFrom("someuser@test.test");
			mail.addTo(user.getEmail());
			mail.setSubject("Password wiederhestellen");
			mail.setMsg(mailTemplate.toString());
			mail.setCharset("UTF-8");

			Logger.info("About to send mail=" + ReflectionToStringBuilder.toString(mail));
			mail.send();

		} catch (final org.apache.commons.mail.EmailException e) {
			Logger.error("PasswordRecoverController:: could not send password recovery mail due to =" + e, e);
			flash().put(FlashScope.ERROR,
					"Es ist ein Fehler beim Mailversand aufgetreten, bitte versuchen Sie es später erneut !");
			return redirect(routes.PasswordRecoverController.index(user.getEmail()));
		}

		flash().put(FlashScope.SUCCESS,
				"Eine Bestätigungsmail wurde an Sie versandt, bitte überprüfen Sie Ihr Postfach !");
		return redirect(routes.PasswordRecoverController.index(user.getEmail()));
	}

	public static Result showChangePassword(final String _email, final String _randomPasswordRecoveryString) {
		final User user = isValidRequest(_email, _randomPasswordRecoveryString);
		if (user == null) {
			//FIXME: 
			return badRequest("invalid link");  
		}
		return ok(views.html.site.pwchange.render(passwordChangeForm.fill(new PasswordChangeForm(_email)),
				_randomPasswordRecoveryString));
	}
	
	//FIXME: watch TX !! 
	public static Result performChangePassword(final String _randomPasswordRecoveryString) {
		final Form<PasswordChangeForm> requestForm = passwordChangeForm.bindFromRequest();
		if (requestForm.hasErrors()) {
			flash().put(FlashScope.ERROR, "Fehler beim Ausfüllen des Formulars!");
			return badRequest(views.html.site.pwchange.render(requestForm, _randomPasswordRecoveryString));
		}

		final PasswordChangeForm pwForm = requestForm.get();
		final User user = isValidRequest(pwForm.getEmail(), _randomPasswordRecoveryString);
		if (user == null) {
			//FIXME: 
			return badRequest("invalid link");  
		}
		
		if (! pwForm.isPasswordMatching()) {
			flash().put(FlashScope.ERROR, "Die beiden Passwörter sind nicht gleich!");
			return badRequest(views.html.site.pwchange.render(requestForm, _randomPasswordRecoveryString));
		}
		
		user.setPasswordHash(Authenticated.createHash(pwForm.getPassword1()));
		user.setRandomPasswordRecoveryTriggerDate(null);
		user.setRandomPasswordRecoveryString(null);
		user.save();
		
		//FIXME: notify user by mail about pw change !! best practice. 
		
		flash().put(FlashScope.SUCCESS,"Passwort erfolgreich geändert! Sie können sich nun wieder einloggen");
		return ok(views.html.site.pwchange.render(passwordChangeForm, _randomPasswordRecoveryString));
	}

	private static User isValidRequest(final String _email, final String _randomPasswordRecoveryString) {
		final User user = User.findByEmail(_email);
		if (user == null) {
			Logger.info("PasswordRecoverController:invalid user for mail=" + _email);
			return null;
		}
		if (!user.getRandomPasswordRecoveryString().equals(_randomPasswordRecoveryString)) {
			Logger.info("PasswordRecoverController: invalid uuid for user=" + user);
			return null;
		}
		if (user.getRandomPasswordRecoveryTriggerDate().getTime()
				+ (AppConfigResolver.getPlain(AppConfigResolver.PASSWORD_RECOVER_LINK_VALID_MILLIS).asLong()) < System
				.currentTimeMillis()) {

			Logger.info("PasswordRecoverController: recovery link is expired for user=" + user);
			return null;
		}
		return user; 
	}

}
