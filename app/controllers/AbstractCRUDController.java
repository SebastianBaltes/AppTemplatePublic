package controllers;

import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.avaje.ebean.Page;

import models.CrudFinder;
import models.CrudModel;
import models.Ding;
import play.Logger;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Results.Status;

public abstract class AbstractCRUDController<T extends CrudModel<T>> implements ICRUDController<T> {

	protected Class<T> entityClass;
	protected String crudBaseUrl;
	protected String entityLabel;
	protected Form<T> form;
	protected CrudFinder<T> crudFinder;
	
	public AbstractCRUDController(Class<T> entityClass, String crudBaseUrl, String entityLabel, CrudFinder<T> crudFinder, Form<T> form) {
		this.entityClass = entityClass;
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

	@Override
	public String getCrudBaseUrl() {
		return crudBaseUrl;
	}

	public String getListUrl() {
		return crudBaseUrl+"/list";
	}

	@Override
	public Result list(final int page, final int rowsToShow, final String sortBy, final String order, final String filter) {
		return Results.ok(renderList(crudFinder.page(page, rowsToShow, sortBy, order, filter), new CrudListState(crudBaseUrl, entityLabel, rowsToShow, sortBy, order, filter)));
	}

	@Override
	public Result create() {
		final T entity = createEntity();
		final Form<T> filledForm = form.fill(entity);
		return Results.ok(renderDetailsShortcut(ViewType.create, filledForm));
	}

	@Override
	public Result view(final Long id) {
		return show(id, ViewType.view);
	}

	@Override
	public Result edit(final Long id) {
		return show(id, ViewType.update);
	}

	@Override
	public Result update() {
		return save(ViewType.update);
	}

	@Override
	public Result save() {
		return save(ViewType.create);
	}

	private Result show(final Long id, final ViewType viewType) {
		Logger.info("show(" + id + ")");
		final T entity = crudFinder.byId(id);
		Logger.info(ReflectionToStringBuilder.toString(entity));
		if (entity == null) {
			return notFoundResult();
		}
		Object o = entity;
		final Form<T> filledForm = form.fill(entity);
        return Results.ok(renderDetailsShortcut(viewType, filledForm));
	}

	private Result save(final ViewType viewType) {
        Logger.info("save(" + viewType + ")");
        final Form<T> filledForm = form.bindFromRequest();
        if (filledForm.hasErrors()) {
        	Controller.flash("error", filledForm.hasGlobalErrors()?filledForm.globalError().message():"Fehler beim Ausfüllen des Formulars!");
            return Results.badRequest(renderDetailsShortcut(viewType, filledForm));
        }
        final T entity = filledForm.get();
        entity.saveOrUpdate();
        Controller.flash("success", entityLabel + " " + entity.label() + " " + viewType + " erfolgreich");
        return listAll();
	}

	@Override
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
		return Results.notFound(entityLabel+" nicht vorhanden!");
	}

	@Override
	public Result listAll() {
		return Results.redirect(getListUrl());
	}
    
}
