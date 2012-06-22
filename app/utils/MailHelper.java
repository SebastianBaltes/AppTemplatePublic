package utils;

import global.AppConfigResolver;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class MailHelper {

	public static final Email createSimpleMail() throws org.apache.commons.mail.EmailException {
		final SimpleEmail mail = new SimpleEmail();
		
		mail.setHostName(AppConfigResolver.get(AppConfigResolver.SMTP_HOST).toString());
		mail.setSmtpPort(AppConfigResolver.get(AppConfigResolver.SMTP_PORT).asInt());
		mail.setFrom(AppConfigResolver.get(AppConfigResolver.SMTP_FROM_ADDRESS).toString());
		mail.setCharset("UTF-8");
		return mail;
	}
}
