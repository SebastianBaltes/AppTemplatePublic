package controllers.admin;

import models.Ding;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.DynamicImagesController;

public class GenericImagesController extends Controller {

//	private static DynamicImagesController imagesController = new DynamicImagesController().put("ding", Ding.class);
	
	public static Result thumbnail(String prefix, long id) {
		return ok("");
//		return imagesController.thumbnail(prefix, id);
	}
	
	public static Result image(String prefix, long id) {
		return ok("");
//		return imagesController.image(prefix, id);
	}
	
}
