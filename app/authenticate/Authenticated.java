package authenticate;

import play.Logger;
import play.cache.Cache;
import play.mvc.Http;
import play.mvc.Http.Session;
import models.User;

public class Authenticated {

	public static final String CACHE_USER = "_User";
	public static final String KEY_UUID = "uuid";

	public static User getAuthenticatedUser() {
		final Session session = Http.Context.current().session();
		final String uuid = session.get(KEY_UUID);
		if (uuid == null) return null;

		final String uuidKey = uuid + CACHE_USER;
		User auth_user = (User) Cache.get(uuidKey);

		if (auth_user == null) {
			auth_user = User.findByEmail(session.get("email"));
			Cache.set(uuidKey, auth_user);
			Logger.debug("reloaded User entity from persistence due to inexistance in cache for user=" + auth_user);
		}
		return auth_user;
	}

}