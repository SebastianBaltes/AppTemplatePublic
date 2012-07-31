package controllers.admin;

import java.util.Map;

import models.ReportQuery;
import models.ReportQueryParameter;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;

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
		Long reportId = null;
		String[] paras = null;
		try {
			final Map<String, String[]> paraMap = request().queryString();
			reportId = Long.valueOf(paraMap.get("rid")[0]);
			paras = paraMap.get("para");
		} catch (final NumberFormatException e) {
			return badRequest("bad request");
		}

		final ReportQuery q = ReportQuery.find.byId(reportId);
		if (q == null) return badRequest("bad request");

		if (q.getQueryParameters() != null && paras.length != q.getQueryParameters().size()) {
			return badRequest("bad request");
		}

		final SqlQuery sqlQuery = Ebean.createSqlQuery(q.getQuery());
		if (paras != null) {
			for (int i = 0; i < paras.length; i++) {
				sqlQuery.setParameter(i, paras[i]);
			}
		}
		

		return ok("report=" + sqlQuery.findList());
	}

}
