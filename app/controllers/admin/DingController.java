package controllers.admin;

import models.Ding;
import play.Logger;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.dingDetails;
import views.html.admin.dingList;
import controllers.ControllerHelper;
import controllers.CrudListState;
import controllers.ViewType;

//@Security.Authenticated(Secured.class)
public class DingController extends Controller {

	private static Form<Ding> form = form(Ding.class);

	public static Result listAll() {
		return redirect("/admin/ding/list");
	}
 
	public static Result list(final int page, final int rowsToShow, final String sortBy, final String order, final String filter) {
		return ok(dingList.render(Ding.page(page, rowsToShow, sortBy, order, filter), new CrudListState("/admin/ding",rowsToShow, sortBy, order, filter)));
	}

	public static Result create() {
		final Ding entity = new Ding();
		final Form<Ding> filledForm = form.fill(entity);
		return ok(dingDetails.render(filledForm, ViewType.create));
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

	private static Result show(final Long id, final ViewType viewType) {
		Logger.info("show(" + id + ")");
		final Ding test = Ding.findById(id);
		if (test == null) {
			return notFound("Entity nicht vorhanden!");
		}
		final Form<Ding> filledForm = form.fill(test);
        return ok(dingDetails.render(filledForm, viewType));
	}

	private static Result save(final ViewType viewType) {
        Logger.info("save(" + viewType + ")");
        final Form<Ding> filledForm = form.bind(ControllerHelper.getRequestMapWithMultiSelectSupport());
        if (filledForm.hasErrors()) {
            flash("error", "Fehler beim Ausfüllen des Formulars!");
            return badRequest(dingDetails.render(filledForm, viewType));
        }
        final Ding org = filledForm.get();
        org.saveOrUpdate();
        flash("success", "Organisation " + org.getLabel() + " " + viewType + " erfolgreich");
        return redirect(routes.DingController.listAll());
	}

	@Transactional
	public static Result delete(final Long id) {
		Logger.info("delete(" + id + ")");
		final Ding ding = Ding.findById(id);
		if (ding == null) {
			return notFound("Ding nicht vorhanden!");
		}
		ding.delete();
		flash("success", "Ding erfolgreich gelöscht");
        return redirect(routes.DingController.listAll());
	}

}
