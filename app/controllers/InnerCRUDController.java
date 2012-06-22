package controllers;

import com.avaje.ebean.Page;

import models.CrudFinder;
import models.CrudModel;
import play.Logger;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results.Status;

public abstract class InnerCRUDController<T extends CrudModel<T>> {

	protected String crudBaseUrl;
	protected String entityLabel;
	protected Form<T> form;
	protected CrudFinder<T> crudFinder;
	
	public InnerCRUDController(String crudBaseUrl, String entityLabel,
			CrudFinder<T> crudFinder, Form<T> form) {
		
		this.crudBaseUrl = crudBaseUrl;
		this.entityLabel = entityLabel;
		this.form = form;
		this.crudFinder = crudFinder;
	}
	
	public abstract Html renderList(Page<T> page, CrudListState listState);
	public abstract Html renderDetails(CrudDetailsState crudDetailsState, Form<T> filledForm, ViewType viewType);
	public abstract T createEntity();
	
	private Html renderDetailsShortcut(final ViewType viewType,
			final Form<T> filledForm) {
		return renderDetails(new CrudDetailsState(crudBaseUrl, entityLabel), filledForm, viewType);
	}

	public String getListUrl() {
		return crudBaseUrl+"/list";
	}

	public Result list(final int page, final int rowsToShow, final String sortBy, final String order, final String filter) {
		return Controller.ok(renderList(crudFinder.page(page, rowsToShow, sortBy, order, filter), new CrudListState(crudBaseUrl, entityLabel, rowsToShow, sortBy, order, filter)));
	}

	public Result create() {
		final T entity = createEntity();
		final Form<T> filledForm = form.fill(entity);
		return Controller.ok(renderDetailsShortcut(ViewType.create, filledForm));
	}

	public Result view(final Long id) {
		return show(id, ViewType.view);
	}

	public Result edit(final Long id) {
		return show(id, ViewType.update);
	}

	public Result update() {
		return save(ViewType.update);
	}

	public Result save() {
		return save(ViewType.create);
	}

	private Result show(final Long id, final ViewType viewType) {
		Logger.info("show(" + id + ")");
		final T entity = crudFinder.byId(id);
		if (entity == null) {
			return notFoundResult();
		}
		final Form<T> filledForm = form.fill(entity);
        return Controller.ok(renderDetailsShortcut(viewType, filledForm));
	}

	private Result save(final ViewType viewType) {
        Logger.info("save(" + viewType + ")");
        final Form<T> filledForm = form.bind(ControllerHelper.getRequestMapWithMultiSelectSupport());
        if (filledForm.hasErrors()) {
            Controller.flash("error", "Fehler beim Ausfüllen des Formulars!");
            return Controller.badRequest(renderDetailsShortcut(viewType, filledForm));
        }
        final T entity = filledForm.get();
        entity.saveOrUpdate();
        Controller.flash("success", entityLabel + " " + entity.label() + " " + viewType + " erfolgreich");
        return listAll();
	}

	public Result delete(final Long id) {
		Logger.info("delete(" + id + ")");
		final T entity = crudFinder.byId(id);
		if (entity == null) {
			return notFoundResult();
		}
		entity.delete();
		Controller.flash("success", entityLabel+" erfolgreich gelöscht");
        return listAll();
	}

	private Status notFoundResult() {
		return Controller.notFound(entityLabel+" nicht vorhanden!");
	}

	public Result listAll() {
		return Controller.redirect(getListUrl());
	}
    
}
