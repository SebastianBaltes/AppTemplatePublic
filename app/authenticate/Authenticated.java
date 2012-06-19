package authenticate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import play.Configuration;
import play.Logger;
import play.cache.Cache;
import play.mvc.Http;
import play.mvc.Http.Session;
import models.User;

public class Authenticated {

	public static final String CACHE_USER = "_User";
	public static final String SESSION_KEY_UUID = "uuid";
	public static final String SESSION_KEY_REDIRECT_AFTER_AUTHENTICATION = "redirOnAuth";

	public static void loginUser(final User user) {
		Logger.info("loggin in user=" + user);
		Http.Context.current().session().put(SESSION_KEY_UUID, String.valueOf(user.getId()));
	}
	
	public static User getAuthenticatedUser() {
		final Session session = Http.Context.current().session();
		final String uuid = session.get(SESSION_KEY_UUID);
		if (uuid == null) return null;

		final String uuidKey = uuid + CACHE_USER;
		User auth_user = (User) Cache.get(uuidKey);

		if (auth_user == null) {
			auth_user = User.findById(Long.valueOf(uuid));
			if (auth_user == null) return null;

			Cache.set(uuidKey, auth_user);
			Logger.debug("reloaded User entity from persistence due to inexistance in cache for user=" + auth_user);
		}
		return auth_user;
	}

	public static boolean isCorrectPasswordForLogin(final User user, final String password) {
		final String cipher = Configuration.root().getString("my.passwords.hash.cipher");
		final String salt = Configuration.root().getString("my.password.hash.salt");

		try {
			final MessageDigest digest = MessageDigest.getInstance(cipher);
			digest.update(salt.getBytes());
			digest.update(password.getBytes());
			digest.update(salt.getBytes()); // paranoia mode ;-)

			final StringBuilder b = new StringBuilder(64);
			final byte[] hashBytes = digest.digest();

			for (int i = 0; i < hashBytes.length; i++) {
				b.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return b.toString().equals(user.getPasswordHash());

		} catch (NoSuchAlgorithmException e) {
			Logger.error("Authenticated:: did not find " + cipher + " message digest=" + e, e);
			return false;
		}
	}

	public static void setOrgRequestPathOnUnauthorized(final String url, final Session session) {
		session.put(SESSION_KEY_REDIRECT_AFTER_AUTHENTICATION, url);
	}

	public static String getOrgRequestPathOnUnauthorized() {
		return Http.Context.current().session().get(SESSION_KEY_REDIRECT_AFTER_AUTHENTICATION);
	}
}
