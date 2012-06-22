package controllers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import models.CrudFinder;
import models.CrudModel;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;

import com.avaje.ebean.Page;


public class DynamicCRUDController<T extends CrudModel<T>> extends AbstractCRUDController<T> {

	private Class<T> entityClass;
	private Method renderListMethod;
	private Method renderDetailsMethod;
	
	@SuppressWarnings("unchecked")
	public DynamicCRUDController(String crudBaseUrl, Class<T> entityClass, String entityLabel) {
		
		super(crudBaseUrl, entityLabel, (CrudFinder<T>)getCrudFinder(entityClass), Controller.form(entityClass));
		this.entityClass = entityClass;
		this.renderListMethod = getRenderMethod("List", new Class[] {Page.class, CrudListState.class});
		this.renderDetailsMethod = getRenderMethod("Details", new Class[] {CrudDetailsState.class, Form.class, ViewType.class});
	}

	@Override
	public Html renderList(Page<T> page, CrudListState listState) {
		return invokeRenderMethod(renderListMethod, page, listState);
	}

	@Override
	public Html renderDetails(CrudDetailsState crudDetailsState, Form<T> filledForm, ViewType viewType) {
		return invokeRenderMethod(renderDetailsMethod, crudDetailsState, filledForm, viewType);
	}

	@Override
	public T createEntity() {
		try {
			return entityClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private Html invokeRenderMethod(Method renderMethod, Object ... params) {
		try {
			return (Html)renderMethod.invoke(null, params);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Method getRenderMethod(String renderTemplateSuffix, @SuppressWarnings("rawtypes") Class ... paramTypes) {
		String className = "views.html."+crudBaseUrl.substring(1).replace('/','.')+renderTemplateSuffix;
		Class<?> viewListClass;
		try {
			viewListClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Method renderMethod;
		try {
			renderMethod = viewListClass.getMethod("render",paramTypes);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		if (!Html.class.isAssignableFrom(renderMethod.getReturnType())) {
			throw new RuntimeException("method "+renderMethod.toGenericString()+" has wrong return type, must be "+Html.class);
		}
		return renderMethod;
	}

	@SuppressWarnings("rawtypes")
	private static CrudFinder getCrudFinder(Class entityClass_) {
		Field field;
		try {
			field = entityClass_.getField("find");
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		try {
			return (CrudFinder) field.get(null);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
