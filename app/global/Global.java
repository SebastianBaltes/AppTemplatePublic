package global;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import models.User;
import models.logevents.LogHttpRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.data.format.Formatters;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;

import authenticate.Authenticated;

import com.avaje.ebean.Ebean;

public class Global extends GlobalSettings {

    public final static String APP_NAME = "AppTemplate"; 

    //FIXME: add remote IP (from header and response code)
    @SuppressWarnings("rawtypes")
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
                
            	log.setUserId(ctx.session().get(Authenticated.SESSION_KEY_UUID));
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
		Formatters.register(java.sql.Date.class, new Html5DateFormatter());
		Formatters.register(BigDecimal.class, new BigDecimalFormatter());
		MvTest.get().startAutoUpdate();
	}

	private void setupLogging(final Application app) {
		if (Play.isDev() || Play.isTest()) {
//			Ebean.getServer(null).getAdminLogging().setDebugGeneratedSql(false);
		}
	}
	
	private void createInitialDatabase(final Application app)  {
		if (Ebean.find(User.class).findRowCount() != 0) {
			Logger.info("found entries in user table, skipping creation of initial database");
			return;
		}
		final File sqlFile = app.getFile("conf/database/" + getDatabaseFile(app));
		if (!sqlFile.canRead()) {
			Logger.error("File not found: " + sqlFile.getAbsolutePath());
			return;
		}
		List<String> lines;
		try {
			lines = FileUtils.readLines(sqlFile, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (String row : lines) {
			if (row.startsWith("--") || StringUtils.isBlank(row)) {
				continue;
			}
			Logger.info("init sql statement: " + row);
			Ebean.createSqlUpdate(row).execute();
		}
	}

	private String getDatabaseFile(final Application app) {
		if (app.isDev()) return app.configuration().getString("my.database.initdata.devel"); 
		if (app.isProd()) return app.configuration().getString("my.database.initdata.prod");
		if (app.isTest()) return app.configuration().getString("my.database.initdata.test");
		return null;
	}
	
}
