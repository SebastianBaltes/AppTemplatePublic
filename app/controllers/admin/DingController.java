package controllers.admin;

import java.util.Collection;

import models.Ding;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import com.avaje.ebean.Page;

import controllers.ControllerHelper;
import controllers.ViewType;

//@Security.Authenticated(Secured.class)
public class DingController extends Controller {

	private static Form<Ding> form = form(Ding.class);

	public static Result listAll() {
		return redirect("/ding/list");
	}
 
	public static Result list(final int page, final int rowsToShow, final String sortBy,
			final String order, final String filter) {
		return ok(dingList.render(Ding.page(org.id, page, rowsToShow, sortBy, order, filter), rowsToShow, sortBy, order, filter));
	}

	public static Result create() {
		final Ding entity = new Ding();
		final Form<Ding> form = form.fill(entity);
		return ok(views.html.dingEntity.render(form, ViewType.create));
	}

	public static Result view(final Long id) {
		return show(id, ViewType.view);
	}

	public static Result edit(final Long id) {
		return show(id, ViewType.update);
	}

	public static Result update() {
		return save(ViewType.update);
	}

	public static Result save() {
		return save(ViewType.create);
	}

	private static Result show(final Long id, final ViewType viewType,
			final ShowResultClosure showResultClojure) {
		Logger.info("show(" + id + ")");
		final Ding test = Ding.findById(id);
		if (test == null) {
			return notFound("Entity nicht vorhanden!");
		}
		final Form<Ding> form = form.ill(test);
        return ok(dingDetails.render(form, viewType));
	}

	private static Result save(final ViewType viewType) {
        Logger.info("save(" + viewType + ")");
        final Form<Ding> filledForm = filledForm.bind(ControllerHelper.getRequestMapWithMultiSelectSupport());
        if (filledForm.hasErrors()) {
            flash("error", "Fehler beim Ausfüllen des Formulars!");
            return badRequest(dingDetails.render(filledForm, viewType));
        }
        final Ding org = filledForm.get();
        org.saveOrUpdate();
        flash("success", "Organisation " + org.getLabel() + " " + viewType + " erfolgreich");
        return redirect(routes.DingController.listInitial());
	}

	@Transactional
	public static Result delete(final Long id) {
		Logger.info("delete(" + id + ")");
		final Ding test = Ding.findById(id);
		if (test == null) {
			return notFound("Ding nicht vorhanden!");
		}
		test.deleteCascading();
		flash("success", "Ding erfolgreich gelöscht");
        return redirect(routes.DingController.listInitial());
	}

}
