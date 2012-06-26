package global;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

import models.User;
import models.logevents.LogHttpRequest;

import org.apache.commons.io.FileUtils;

import com.avaje.ebean.Ebean;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.data.format.Formatters;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http.Context;
import play.mvc.Http.Request;

public class Global extends GlobalSettings {

    public final static String APP_NAME = "AppTemplate"; 

    //FIXME: add remote IP (from header and response code)
    @Override
    public Action onRequest(final Request _request, final Method _actionMethod) {
        return new Action.Simple() {
            public Result call(Context ctx) throws Throwable {

            	final LogHttpRequest log = new LogHttpRequest();
            	log.setStartTime(System.currentTimeMillis());
            	log.setFromIP(null);
            	log.setHost(_request.host());
            	log.setMethod(_request.method());
            	log.setReferer(_request.getHeader("referer"));
            	log.setUrl(_request.uri());
            	log.setUserAgent(_request.getHeader("user-agent"));
            	
                final Result result = delegate.call(ctx);
                
            	log.setEndTime(System.currentTimeMillis());
            	log.save();
                return result; 
            }
        };
    }
    
	@Override
	public void onStart(final Application app) {
		Logger.debug("onStart()");
		setupLogging(app);
		createInitialDatabase(app);
		Formatters.register(Timestamp.class, new TimeStampFormatter());
	}

	private void setupLogging(final Application app) {
		if (Play.isDev() || Play.isTest()) {
			Ebean.getServer(null).getAdminLogging().setDebugGeneratedSql(true);
		}
	}
	
	private void createInitialDatabase(final Application app)  {
		
		if (Ebean.find(User.class).findRowCount() != 0) {
			Logger.info("found entries in user table, skipping creation of initial database");
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
