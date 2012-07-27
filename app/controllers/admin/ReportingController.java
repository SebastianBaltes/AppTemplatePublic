package controllers.admin;

import models.ReportQuery;
import models.ReportQueryParameter;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import authenticate.admin.AdminSecured;

@Security.Authenticated(AdminSecured.class)
public class ReportingController extends Controller {

	private static final Form<ReportQuery> form = form(ReportQuery.class);
	
	public static Result index() {
		return ok(views.html.admin.createReports.render(null, form));
	}
	
	public static Result info(final Long id) {
		final ReportQuery q = ReportQuery.find.byId(id);
		if (q == null) return badRequest("invalid report");

		final ObjectNode node = Json.newObject();
		node.put("id", q.getId());
		node.put("description", q.getDescription());
		node.put("query", q.getQuery());
		
		final ArrayNode paraListNode = Json.newObject().arrayNode();
		node.put("parameter_list", paraListNode);
		
		for (final ReportQueryParameter p : q.getQueryParameters()) {
			final ObjectNode paraNode = Json.newObject();
			paraNode.put("name", p.getName());
			paraNode.put("description", p.getDescription());
			paraListNode.add(paraNode);
		}
		
		return ok(node);
	}

	public static Result report() {
		return ok("report");
	}

}
