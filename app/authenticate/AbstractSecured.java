package authenticate;
	
import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;	

public abstract class AbstractSecured extends Security.Authenticator {

	@Override
	public String getUsername(final Context ctx) {
		return ctx.session().get(Authenticated.SESSION_KEY_UUID);
	}
	
	/**
	 * implements remebering the original request path for later redirection after successful authentication
	 * @return returns always null. 
	 * {@inheritDoc}
	 */
	@Override
	public Result onUnauthorized(final Context _ctx) {
		final String path = _ctx.request().path();
		Logger.info("AbstractSecured: unauthorized request path=" + path);
		
		Authenticated.setOrgRequestPathOnUnauthorized(path, _ctx.session());
		return null;
	}

}
