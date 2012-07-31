package controllers;

import play.mvc.Controller;
import play.mvc.Result;

@SuppressWarnings("rawtypes")
public class RoutingCRUDController implements ICRUDController {
	
	private ICRUDController[] crudControllers;
	
	public RoutingCRUDController(ICRUDController ... crudControllers) {
		this.crudControllers = crudControllers;
	}
	
	private ICRUDController routeController() {
		String path = Controller.request().path();
		for (ICRUDController crudController : crudControllers) {
			if (path.startsWith(crudController.getCrudBaseUrl())) {
				return crudController;
			}
		}
		throw new RuntimeException("no CRUD controller registered for "+path);
	}

	public Result list(int page, int rowsToShow, String sortBy, String order,
			String filter) {
		return routeController().list(page, rowsToShow, sortBy, order, filter);
	}


	public Result create() {
		return routeController().create();
	}


	public Result view(Long id) {
		return routeController().view(id);
	}


	public Result edit(Long id) {
		return routeController().edit(id);
	}


	public Result update() {
		return routeController().update();
	}


	public Result save() {
		return routeController().save();
	}


	public Result delete(Long id) {
		return routeController().delete(id);
	}

	public Result listAll() {
		return routeController().listAll();
	}

	@Override
	public String getCrudBaseUrl() {
		throw new RuntimeException("RoutingCRUDController has no specific CrudBaseUrl");
	}

}
