package controllers;

import models.CrudModel;
import play.mvc.Result;

public interface ICRUDController<T extends CrudModel<T>> {

	public Result list(final int page, final int rowsToShow, final String sortBy, final String order, final String filter);
	public Result create();
	public Result view(final Long id);
	public Result edit(final Long id);
	public Result update();
	public Result save();
	public Result delete(final Long id);
	public Result listAll();
	public String getCrudBaseUrl();
    
}
