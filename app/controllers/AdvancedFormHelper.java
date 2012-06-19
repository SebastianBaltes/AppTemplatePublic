package controllers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import play.data.Form;

public class AdvancedFormHelper<T> {

	public Form<T> filledForm;
	public T entity;

	public static interface FormConverter<T> {
		public void transformRequestMap(Map<String, String> requestMap);

		public void transformEntity(T entity);
	}

	public static abstract class MultiSelectEntityConverter<T, TargetEntity>
			implements FormConverter<T> {
		public abstract String getIdRequestPattern();

		public abstract Collection<TargetEntity> getCollection(T entity);

		public abstract TargetEntity findById(Long id);

		private Set<Long> ids;

		@Override
		public void transformRequestMap(final Map<String, String> requestMap) {
			ids = filterIds(requestMap, getIdRequestPattern());
		}

		@Override
		public void transformEntity(final T entity) {
			final Collection<TargetEntity> collection = getCollection(entity);
			collection.clear();
			for (final Long id : ids) {
				collection.add(findById(id));
			}
		}
	}

	public AdvancedFormHelper(final Form<T> form,
			final FormConverter<T>... formConverters) {
		final Map<String, String> requestMap = ControllerHelper
				.getRequestMapWithMultiSelectSupport();
		for (final FormConverter<T> converter : formConverters) {
			converter.transformRequestMap(requestMap);
		}
		filledForm = form.bind(requestMap);
		if (filledForm.hasErrors()) {
			return;
		}
		entity = filledForm.get();
		for (final FormConverter<T> converter : formConverters) {
			converter.transformEntity(entity);
		}
	}

	public static Set<Long> filterIds(final Map<String, String> requestMap,
			final String pattern) {
		final Set<Long> ids = new HashSet<Long>();
		final Pattern p = Pattern.compile(pattern);
		for (final Iterator<Map.Entry<String, String>> i = requestMap
				.entrySet().iterator(); i.hasNext();) {
			final Map.Entry<String, String> entry = i.next();
			if (p.matcher(entry.getKey()).matches()) {
				ids.add(Long.parseLong(entry.getValue()));
				i.remove();
			}
		}
		return ids;
	}

}
