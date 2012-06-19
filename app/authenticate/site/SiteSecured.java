package authenticate.site;

import play.mvc.Result;
import play.mvc.Http.Context;
import controllers.site.routes;
import authenticate.AbstractSecured;

public class SiteSecured extends AbstractSecured {

	@Override
	public Result onUnauthorized(final Context _ctx) {
		super.onUnauthorized(_ctx);
		return redirect(routes.LoginController.index());
	}
}
