package utils.test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public interface SmtpMock {
	
	public enum Command {
		EHLO("EHLO"),
		HELO("HELO"),
		MAIL_FROM("MAIL FROM:"),
		RCPT_TO("RCPT TO:"),
		DATA("DATA"),
		RSET("RSET"),
		HELP("HELP"),
		NOOP("NOOP"),
		QUIT("QUIT"), 
		VRFY("VRFY"),
		EXPN("EXPN");
		
		private String printValue;
		private static Pattern cmdPattern = Pattern.compile("^(" + join() + ")");
		
		private Command(final String printValue) {
			this.printValue = printValue;
		}
		
		public String print() {
			return printValue; 
		}

		public static Command getValidCommand(final String row) {
			final Matcher m = cmdPattern.matcher(row);
			if (! m.find()) return null;
			return Command.find(m.group(1));
		}
		
		private static Command find(final String value) {
			for (Command c : values()) {
				if (c.printValue.equals(value)) return c; 
			}
			return null;
		}
		
		private static String join() {
			final StringBuilder s = new StringBuilder();
			for (int i=0;i<values().length; i++) {
				if (i != 0) s.append("|");
				s.append(values()[i].print());
			}
			return s.toString();
		}
	}
	
	// Codes taken from RFC-2821
	@SuppressWarnings("serial")
	public static final Map<Integer, String> SMTP_CODES = new HashMap<Integer, String>() {{
		put(211, "211 System status, or system help reply\r\n");
		put(214, "214 Help message\r\n");
		put(220, "220 SmtpMock Service ready\r\n");
		put(221, "221 SmtpMock Service closing transmission channel\r\n");
		put(250, "250 Requested mail action okay, completed\r\n");
		put(251, "251 User not local; will forward to <forward-path>\r\n");
		put(252, "252 Cannot VRFY user, but will accept message and attempt delivery\r\n");
		put(354, "354 Start mail input; end with <CRLF>.<CRLF>\r\n");
		put(421, "421 $domain Service not available, closing transmission channel\r\n");
		put(450, "450 Requested mail action not taken: mailbox unavailable\r\n");
		put(451, "451 Requested action aborted: local error in processing\r\n");
		put(452, "452 Requested action not taken: insufficient system storage\r\n");
		put(500, "500 Syntax error, command unrecognized\r\n");
		put(501, "501 Syntax error in parameters or arguments\r\n");
		put(502, "502 Command not implemented\r\n");
		put(503, "503 Bad sequence of commands\r\n");
		put(504, "504 Command parameter not implemented\r\n");
		put(550, "550 Requested action not taken: mailbox unavailable\r\n");
		put(551, "551 User not local; please try <forward-path>\r\n");
		put(552, "552 Requested mail action aborted: exceeded storage allocation\r\n");
		put(553, "553 Requested action not taken: mailbox name not allowed\r\n");
		put(554, "554 Transaction failed\r\n");
	}};

	void addMailListener(MailListener listener);
	void removeMailListener(MailListener listener);
}
