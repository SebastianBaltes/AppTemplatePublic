package authenticate;

import play.mvc.Http.Context;
import play.mvc.Security;

public abstract class AbstractSecured extends Security.Authenticator {

	@Override
	public String getUsername(final Context ctx) {
		return ctx.session().get(Authenticated.SESSION_KEY_UUID);
	}

}
