import play.Application;
import play.GlobalSettings;
import play.Logger;



public class Global extends GlobalSettings {

	@Override
	public void onStart(final Application app) {
		Logger.debug("onStart()");
	}
	
	
	
	
}
