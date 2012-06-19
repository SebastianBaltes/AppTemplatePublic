package authenticate;

import play.Logger;
import play.cache.Cache;
import play.mvc.Http;
import play.mvc.Http.Session;
import models.User;

public class Authenticated {

	public static final String CACHE_USER = "_User";
	public static final String SESSION_KEY_UUID = "uuid";
	public static final String SESSION_KEY_REDIRECT_AFTER_AUTHENTICATION = "redirOnAuth";
	
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
	
	public static void setOrgRequestPathOnUnauthorized(final String url, final Session session) {
		session.put(SESSION_KEY_REDIRECT_AFTER_AUTHENTICATION, url);
	}
	
	public static String getOrgRequestPathOnUnauthorized() {
		return Http.Context.current().session().get(SESSION_KEY_REDIRECT_AFTER_AUTHENTICATION);
	}
}
