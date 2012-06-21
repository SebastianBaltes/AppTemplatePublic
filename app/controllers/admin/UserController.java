package controllers.admin;

import models.User;
import play.Logger;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.userDetails;
import views.html.admin.userList;
import controllers.ControllerHelper;
import controllers.CrudListState;
import controllers.ViewType;

//@Security.Authenticated(Secured.class)
public class UserController extends Controller {

	private static Form<User> form = form(User.class);

	public static Result listAll() {
		return redirect("/admin/user/list");
	}
 
	public static Result list(final int page, final int rowsToShow, final String sortBy, final String order, final String filter) {
		return ok(userList.render(User.page(page, rowsToShow, sortBy, order, filter), new CrudListState("/admin/user",rowsToShow, sortBy, order, filter)));
	}

	public static Result create() {
		final User entity = new User();
		final Form<User> filledForm = form.fill(entity);
		return ok(userDetails.render(filledForm, ViewType.create));
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
		final User test = User.findById(id);
		if (test == null) {
			return notFound("Entity nicht vorhanden!");
		}
		final Form<User> filledForm = form.fill(test);
        return ok(userDetails.render(filledForm, viewType));
	}

	private static Result save(final ViewType viewType) {
        Logger.info("save(" + viewType + ")");
        final Form<User> filledForm = form.bind(ControllerHelper.getRequestMapWithMultiSelectSupport());
        if (filledForm.hasErrors()) {
            flash("error", "Fehler beim Ausfüllen des Formulars!");
            return badRequest(userDetails.render(filledForm, viewType));
        }
        final User org = filledForm.get();
        org.saveOrUpdate();
        flash("success", "Organisation " + org.getLabel() + " " + viewType + " erfolgreich");
        return redirect(routes.DingController.listAll());
	}

	@Transactional
	public static Result delete(final Long id) {
		Logger.info("delete(" + id + ")");
		final User user = User.findById(id);
		if (user == null) {
			return notFound("User nicht vorhanden!");
		}
		user.delete();
		flash("success", "User erfolgreich gelöscht");
        return redirect(routes.DingController.listAll());
	}

}
