package de.objectcode.play2.plugin.search;

import javax.swing.JOptionPane;

import org.apache.lucene.store.Directory;

import play.Application;
import play.Configuration;
import play.Logger;
import play.Plugin;

/**
 * 
 * SearchPlugin.
 * <p/>
 * Handles configuration and creation of the {@link Search} singleton
 * 
 */
public class SearchPlugin extends Plugin {

	private static boolean isInitialized;
	
	public static final String CONF_PREFIX = "ocsearch";
	public static final String CONF_DIRECTORY_PROVIDER = "dirprovider";
	public static final String CONF_INDEX_MODE = "indexmode";
	public static final String CONF_BOOTSTRAP_CLASS = "bootstrap";

	private final Application application;

	public static enum IndexMode {
		create, append
	}

	public SearchPlugin(final Application application) {
		this.application = application;
	}

	@Override
	public void onStart() {
		if (isInitialized) return;
		isInitialized = true;
		
		Logger.info("SearchPlugin initializing");

		final Configuration config = application.configuration().getConfig(CONF_PREFIX);
		if (config == null) {
			Logger.warn("no configuration found for prefix=" + CONF_PREFIX);
			return;
		}

		final String dirProviderName = config.getString(CONF_DIRECTORY_PROVIDER);
		if (dirProviderName == null) {
			Logger.error("no class name configured for key=" + CONF_DIRECTORY_PROVIDER);
			throw config.reportError(CONF_DIRECTORY_PROVIDER, "no class name configured for key=" + CONF_DIRECTORY_PROVIDER, null);
		}

		final String indexModeName = config.getString(CONF_INDEX_MODE);
		if (indexModeName == null) {
			Logger.error("missing config for key=" + CONF_INDEX_MODE);
			throw config.reportError(CONF_INDEX_MODE, "missing config for key=" + CONF_INDEX_MODE, null);
		}

		final IndexMode indexMode = IndexMode.valueOf(indexModeName);

		if (indexMode == null) {
			Logger.error("invalid indexmode=" + indexModeName);
			throw config.reportError(CONF_INDEX_MODE, "invalid indexmode=" + indexModeName, null);
		}

		final String bootstrap = config.getString(CONF_BOOTSTRAP_CLASS);
		if (bootstrap == null && indexMode == IndexMode.create) {
			Logger.error("mode is 'create'. missing config for key=" + CONF_BOOTSTRAP_CLASS);
			throw config.reportError(CONF_BOOTSTRAP_CLASS, "mode is 'create'. missing config for key=" + CONF_BOOTSTRAP_CLASS, null);
		}

		try {
			final DirectoryProvider provider = newInstance(dirProviderName, DirectoryProvider.class);
			Search.get().directory = provider.provide();
		} catch (final Exception e) {
			throw config.reportError(CONF_DIRECTORY_PROVIDER, "Error creating directory implementation: " + e, e);
		}	
		
		if (indexMode == IndexMode.create) {
			try {
				final Bootstrapable boot = (Bootstrapable) newInstance(bootstrap, Bootstrapable.class);
				Search.get().bootstrapable = boot;
				boot.create();
			}
			catch (final Exception e) {
				throw config.reportError(CONF_BOOTSTRAP_CLASS, "Error creating Bootstrapable implementation: " + e, e);
			}
		}
	}
	
	private <T> T newInstance(final String className, final Class<T> checkInterface) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		final Class<?> clazz = Class.forName(className);
		if (!checkInterface.isAssignableFrom(clazz)) {
			Logger.error("Class " + className + " does not implement " + checkInterface.getSimpleName());
			throw new InstantiationException("Class " + className + " does not implement " + checkInterface.getSimpleName());
		}
		@SuppressWarnings("unchecked")
		final T object = (T) clazz.newInstance();
		return object;
	}

	@Override
	public void onStop() {
		Logger.info("SearchPlugin stopping");
	}

	public static class Search {
		private Directory directory;
		private Bootstrapable bootstrapable;
		
		private static final Search INSTANCE = new Search();

		public static Search get() {
			return INSTANCE;
		}
		
		public Directory getDirectory() {
			return directory;
		}
		
		public Bootstrapable getBootstrapable() {
			return bootstrapable;
		}
	}

}
