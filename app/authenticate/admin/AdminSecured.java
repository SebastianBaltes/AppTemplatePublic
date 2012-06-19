package authenticate.admin;

import controllers.admin.routes;
import play.mvc.Result;
import play.mvc.Http.Context;
import authenticate.AbstractSecured;


public class AdminSecured extends AbstractSecured {
	
	@Override
	public Result onUnauthorized(final Context _ctx) {
		super.onUnauthorized(_ctx);
		return redirect(routes.LoginController.index());
	}
	
}
