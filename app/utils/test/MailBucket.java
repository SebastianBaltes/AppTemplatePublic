package utils.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;


@SuppressWarnings("serial")
public class MailBucket extends ArrayList<MailBucket.Mail> implements MailListener {

	public static class Mail {

		public String from;
		public String to;
		public String body;

		public Mail(String _from, String _to, String _body) {
			from = _from;
			to = _to;
			body = _body;
		}
		
		public MimeMessage parse() {
			try {
				Session s = Session.getDefaultInstance(new Properties());
				InputStream is = new ByteArrayInputStream(body.getBytes());
				return new MimeMessage(s, is);
			}
			catch(javax.mail.MessagingException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public void mailReceived(String _mailFrom, String _mailTo, String _mail) {
		add(new Mail(_mailFrom, _mailTo, _mail));
	}

}
