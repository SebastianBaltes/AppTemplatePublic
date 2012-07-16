package global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import models.MvTestFeature;
import models.MvTestVariant;
import play.libs.Akka;
import akka.util.Duration;

/**
 * see https://docs.google.com/document/d/1vCSSA3piVk_mrvpROOL9f3BRuVIfMvVz-zZB0NZGI0A/edit#
 * @author sbaltes
 */
public class MvTest {
	
	private final static MvTest INSTANCE = new MvTest();
	
	public static MvTest get() {
		return INSTANCE;
	}
	
	private AtomicReference<Map<String,MvTestFeature>> features = new AtomicReference<Map<String, MvTestFeature>>(new HashMap<String, MvTestFeature>());

	public boolean active(String featureName) {
		MvTestFeature feature = features.get().get(featureName);
		if (feature==null) {
			return false;
		}
		return feature.isCurrentlyActive(); 
	}
	
	public String variant(double fixRandomNumber, String featureName) {
		MvTestFeature feature = features.get().get(featureName);
		if (feature==null) {
			return null;
		}
		if (!feature.isCurrentlyActive()) {
			return null;
		}
		MvTestVariant variant = feature.getVariantFor(fixRandomNumber);
		if (variant==null) {
			return null;
		}
		return variant.getName();
	}

	public void startAutoUpdate() {
		Akka.system().scheduler().schedule(
		  Duration.create(0, TimeUnit.MILLISECONDS),
		  Duration.create(1, TimeUnit.MINUTES),
		  new Runnable() {
		    public void run() {
		    	updateFeatures();
		    }
		  }
		);		
	}
	
	public void updateFeatures() {
		Map<String,MvTestFeature> map = new HashMap<String, MvTestFeature>();
		List<MvTestFeature> list = MvTestFeature.find.all();
		for (MvTestFeature f : list) {
			map.put(f.getName(), f);
		}
		features.set(map);
	}

}
