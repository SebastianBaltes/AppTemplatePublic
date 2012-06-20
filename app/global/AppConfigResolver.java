package global;

import play.Configuration;

public class AppConfigResolver {

	public static final String PARA_MY_PROFILE_NAME = "my.profile.name";
	
	public static final String SMTP_HOST = "smtp.host";
	public static final String SMTP_PORT = "smtp.port"; 
	public static final String SMTP_USER = "smtp.user"; 
	public static final String SMTP_PASS = "smtp.pass"; 
	
	public static String get(final String key) {
		return get(key, Configuration.root().getString(PARA_MY_PROFILE_NAME));
	}

	public static String get(final String key, final String scope) {
		final StringBuilder b = new StringBuilder(32);
		b.append("my.").append(scope).append(".").append(key);
		return Configuration.root().getString(b.toString());
	}

}
