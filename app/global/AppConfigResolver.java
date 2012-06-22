package global;

import play.Configuration;

public class AppConfigResolver {

	public static final String PARA_MY_SCOPE = "my";
	public static final String PARA_MY_PROFILE_NAME = PARA_MY_SCOPE + ".profile.name";
	
	// scoped keys (devel,test,prod)
	public static final String SMTP_HOST 			= "smtp.host";
	public static final String SMTP_PORT 			= "smtp.port"; 
	public static final String SMTP_USER 			= "smtp.user"; 
	public static final String SMTP_PASS 			= "smtp.pass"; 
	public static final String SMTP_FROM_ADDRESS 	= "smtp.from_address";
	
	// plain keys 
	public static final String PASSWORD_RECOVER_UUID_LENGTH = "my.passwords.recover.uuid.length"; 
	public static final String PASSWORD_RECOVER_LINK_VALID_MILLIS = "my.controller.PasswordRecoverController.recoverLinkValidMillis";
	public static final String ACTIVATE_ACCOUNT_SECRET_SALT = "my.controller.RegistrationController.secret_activate_salt";
	
	public static Element getPlain(final String key) {
		return new Element(Configuration.root().getString(key));
	}
	
	public static Element get(final String key) {
		return get(key, Configuration.root().getString(PARA_MY_PROFILE_NAME));
	}

	public static Element get(final String key, final String scope) {
		final StringBuilder b = new StringBuilder(32);
		b.append(PARA_MY_SCOPE).append(".").append(scope).append(".").append(key);
		return new Element(Configuration.root().getString(b.toString()));
	}
	
	public static class Element {
		private String value;
		
		public Element(final String _value) {
			value = _value;
		}
		
		public Integer asInt() {
			if (value == null) return null;
			return Integer.valueOf(value);
		}
		
		public Long asLong() {
			if (value == null) return null;
			return Long.valueOf(value);
		}
		
		public Boolean asBool() {
			return Boolean.valueOf(value);
		}
		
		@Override
		public String toString() {
			return value;
		}
		
	}
	

}
