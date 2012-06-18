import com.avaje.ebean.Ebean;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;

public class Global extends GlobalSettings {

	@Override
	public void onStart(final Application app) {
		Logger.debug("onStart()");
//		setupLogging(app);
		
	}

	private void setupLogging(final Application app) {
		if (Play.isDev() || Play.isTest()) {
			Ebean.getServer(null).getAdminLogging().setDebugGeneratedSql(true);
		}
	}

}
