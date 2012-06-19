package authenticate.admin;


import models.User;
import controllers.admin.routes;
import play.mvc.Result;
import play.mvc.Http.Context;
import authenticate.AbstractSecured;
import authenticate.Authenticated;


public class AdminSecured extends AbstractSecured {
	
	@Override
	public String getUsername(final Context ctx) {
		final String userId = super.getUsername(ctx); 
		if (userId == null) return null;
		final User loggedinUser = Authenticated.getAuthenticatedUser(ctx.session());
		if (loggedinUser != null && loggedinUser.getRole().isAdminRole()) return userId;
		return null;
	}	
	
	@Override
	public Result onUnauthorized(final Context _ctx) {
		super.onUnauthorized(_ctx);
		return redirect(routes.LoginController.index());
	}
	
}
