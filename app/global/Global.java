package global;
import java.io.File;
import java.io.IOException;
import java.util.List;

import models.Role;

import org.apache.commons.io.FileUtils;

import com.avaje.ebean.Ebean;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;

public class Global extends GlobalSettings {

    public final static String APP_NAME = "AppTemplate"; 

	@Override
	public void onStart(final Application app) {
		Logger.debug("onStart()");
		setupLogging(app);
		createInitialDatabase(app);
	}

	private void setupLogging(final Application app) {
		if (Play.isDev() || Play.isTest()) {
			Ebean.getServer(null).getAdminLogging().setDebugGeneratedSql(true);
		}
	}
	
	private void createInitialDatabase(final Application app)  {
		
		if (Ebean.find(Role.class).findRowCount() != 0) {
			Logger.info("found entries in role table, skipping creation of initial database");
			return;
		}
		try {
			final File sqlFile = app.getFile("conf/database/" + getDatabaseFile(app));
			final StringBuilder query = new StringBuilder();
			
			final List<String> readLines = FileUtils.readLines(sqlFile);
			for (final String row : readLines) {
				if (row.startsWith("--")) continue;
				query.append(row);
				query.append("\n");
			}
			
			Logger.info("about to apply sql for database file=" + sqlFile.getAbsolutePath());
			Logger.info(" => " + query.toString());
			
			Ebean.createSqlUpdate(query.toString()).execute();
		}
		catch(final IOException e) {
			Logger.error(getClass().getSimpleName() + ": could not create database due to " +e,e);
		}
	}

	private String getDatabaseFile(final Application app) {
		if (app.isDev()) return app.configuration().getString("my.database.initdata.devel"); 
		if (app.isProd()) return app.configuration().getString("my.database.initdata.prod");
		if (app.isTest()) return app.configuration().getString("my.database.initdata.test");
		return null;
	}
	
}
