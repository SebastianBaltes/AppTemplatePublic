package controllers.admin;

import models.Ding;
import models.User;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.DynamicCRUDController;
import controllers.RoutingCRUDController;

//@Security.Authenticated(Secured.class)
public class GenericCRUDController extends Controller {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static RoutingCRUDController crudController = new RoutingCRUDController(
			new DynamicCRUDController("/admin/ding", Ding.class, "Ding"),
			new DynamicCRUDController("/admin/user", User.class, "Benutzer")
	);

	public static Result listAll(String crud) {
		return crudController.listAll();
	}

	public static Result list(String crud, final int page, final int rowsToShow, final String sortBy, final String order, final String filter) {
		return crudController.list(page, rowsToShow, sortBy, order, filter);
	}

	public static Result create(String crud) {
		return crudController.create();
	}

	public static Result view(String crud, final Long id) {
		return crudController.view(id);
	}

	public static Result edit(String crud, final Long id) {
		return crudController.edit(id);
	}

	public static Result update(String crud) {
		return crudController.update();
	}

	public static Result save(String crud) {
		return crudController.save();
	}

	@Transactional
	public static Result delete(String crud, final Long id) {
		return crudController.delete(id);
	}

}
