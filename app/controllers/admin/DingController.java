package controllers.admin;

import models.Ding;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.DynamicCRUDController;
import controllers.InnerCRUDController;

//@Security.Authenticated(Secured.class)
public class DingController extends Controller {

	private static InnerCRUDController<Ding> crudController = new DynamicCRUDController<Ding>("/admin/ding", Ding.class, "Ding");

	public static Result listAll() {
		return crudController.listAll();
	}

	public static Result list(final int page, final int rowsToShow, final String sortBy, final String order, final String filter) {
		return crudController.list(page, rowsToShow, sortBy, order, filter);
	}

	public static Result create() {
		return crudController.create();
	}

	public static Result view(final Long id) {
		return crudController.view(id);
	}

	public static Result edit(final Long id) {
		return crudController.edit(id);
	}

	public static Result update() {
		return crudController.update();
	}

	public static Result save() {
		return crudController.save();
	}

	@Transactional
	public static Result delete(final Long id) {
		return crudController.delete(id);
	}

}
