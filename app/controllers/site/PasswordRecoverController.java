package controllers.site;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import global.AppConfigResolver;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.mail.SimpleEmail;

import models.User;
import play.Logger;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.forms.EMailForm;
import controllers.forms.FlashScope;

public class PasswordRecoverController extends Controller {

	private static Form<EMailForm> emailForm = form(EMailForm.class);

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
		
		//FIXME: where to configure stuff ? 
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
			Logger.error("PasswordRecoverController:: could not send password recovery mail due to =" +e, e);
			flash().put(FlashScope.ERROR, "Es ist ein Fehler beim Mailversand aufgetreten, bitte versuchen Sie es später erneut !");
			return redirect(routes.PasswordRecoverController.index(user.getEmail()));
		}
		
		flash().put(FlashScope.SUCCESS, "Eine Bestätigungsmail wurde an Sie versandt, bitte überprüfen Sie Ihr Postfach !");
		return redirect(routes.PasswordRecoverController.index(user.getEmail()));
	}
	
	public static Result changePassword(final String _email, final String _randomPasswordRecoveryString) {
		return ok("changePassword for mail=" + _email + _randomPasswordRecoveryString);
	}

}
