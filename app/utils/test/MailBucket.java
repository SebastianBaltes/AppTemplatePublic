package utils.test;

import java.util.ArrayList;

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
	}

	@Override
	public void mailReceived(String _mailFrom, String _mailTo, String _mail) {
		add(new Mail(_mailFrom, _mailTo, _mail));
	}

}
