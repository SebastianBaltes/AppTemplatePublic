package utils.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSmtpMock implements SmtpMock, Runnable {

	private List<MailListener> listeners = new ArrayList<MailListener>();
	private Thread thread; 
	private ServerSocket serverSock;
	
	private boolean isDataMode;
	private boolean wasDataMode;
	private Response response;
	private StringBuilder mail;
	
	private String mailFrom;
	private String mailTo; 
	
	private Map<Command, CmdConfig> cmds = new HashMap<Command, CmdConfig>();
	
	private static class CmdConfig {
		Integer answerCode; 
		CommandCallback callback;
		
		public static CmdConfig get(Integer _answerCode, CommandCallback _callback) {
			final CmdConfig c = new CmdConfig();
			c.answerCode = _answerCode;
			c.callback = _callback;
			return c; 
		}
	}
	
	public SimpleSmtpMock() throws UnknownHostException, IOException {
		serverSock = new ServerSocket(2525, 50, InetAddress.getByName("localhost"));
		
		cmds.put(Command.EHLO,		CmdConfig.get(500, null));
		cmds.put(Command.HELO,		CmdConfig.get(250, null));
		cmds.put(Command.HELP, 		CmdConfig.get(502, null));
		cmds.put(Command.NOOP, 		CmdConfig.get(250, null));

		cmds.put(Command.MAIL_FROM,	CmdConfig.get(250, new CommandCallback() {
			public void run(String row) {
				mailFrom = row.substring(row.indexOf(":"));
			}
		}));
		cmds.put(Command.RCPT_TO, 	CmdConfig.get(250, new CommandCallback() {
			public void run(String row) {
				mailTo = row.substring(row.indexOf(":"));
			}
		}));		
		cmds.put(Command.DATA, CmdConfig.get(354, new CommandCallback() {
			public void run(String row) {
				mail = new StringBuilder(256);
				isDataMode = true; 
				wasDataMode = true; 
			}
		}));
		cmds.put(Command.RSET, CmdConfig.get(250, new CommandCallback() {
			public void run(String row) {
				isDataMode = false;
			}
		}));
		cmds.put(Command.QUIT, CmdConfig.get(221, new CommandCallback() {
			public void run(String row) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
	}
	
	private static interface CommandCallback {
		 void run(String row);
	}

	public void start() {
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	public void stop() throws IOException {
		thread.interrupt();
		serverSock.close();
	}
	
	private static class Response {
		Socket socket; 
		OutputStream out;
		BufferedReader in;

		Response(final Socket s) throws IOException {
			socket = s; 
			out = s.getOutputStream();
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		
		void put(final String msg) throws IOException {
			out.write(msg.getBytes());
		}
		
		void close() throws IOException {
			in.close();
			out.close();
			socket.close();
		}
	}
	
	@Override
	public void run() {
		while (! thread.isInterrupted()) {
			try {
				response = new Response(serverSock.accept());
				isDataMode = false;
				wasDataMode = false;
				
				// hello world ;-)
				response.put(SMTP_CODES.get(220));
				
				String row; 
				while((row = response.in.readLine()) != null) {
					if (! isDataMode) {
						final Command cmd = Command.getValidCommand(row.toUpperCase());
						if (cmd == null) {
							response.put(SMTP_CODES.get(500));
							continue;
						}
						final CmdConfig cmdConfig = cmds.get(cmd);
						response.put(SMTP_CODES.get(cmdConfig.answerCode));
						
						if (cmdConfig.callback != null) {
							cmdConfig.callback.run(row);
						}
					}
					else {
						if (row.equals(".")) {
							isDataMode = false;
							response.put(SMTP_CODES.get(250));
							fireMailReceived(mail.toString());
						}
						else {
							mail.append(row);
							mail.append("\n");
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	@Override
	public void addMailListener(MailListener _listener) {
		listeners.add(_listener);
	}

	@Override
	public void removeMailListener(MailListener _listener) {
		listeners.remove(_listener);
	}

	protected void fireMailReceived(final String mail) {
		for (final MailListener l : listeners) {
			l.mailReceived(mailFrom, mailTo, mail);
		}
	}

	public static void main(String[] args) {
		try {
			SimpleSmtpMock s = new SimpleSmtpMock();
			s.addMailListener(new MailListener() {
				@Override
				public void mailReceived(String mailFrom, String mailTo, String _mail) {
					System.err.println("****************************************************************");
					System.err.println("mailFrom=" + mailFrom);
					System.err.println("mailTo=" + mailTo);
					System.err.println(_mail);
					System.err.println("****************************************************************");
				}
			});
			s.start();
			
			System.out.println("PRESS PLAY ON TAPE");
			System.in.read();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
