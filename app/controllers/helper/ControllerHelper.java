package controllers.helper;

import java.util.HashMap;
import java.util.Map;

public class ControllerHelper {

	public static Map<String, String> getRequestMapWithMultiSelectSupport() {
		final Map<String, String> newData = new HashMap<String, String>();
		final Map<String, String[]> urlFormEncoded = play.mvc.Controller
				.request().body().asFormUrlEncoded();
		if (urlFormEncoded != null) {
			for (final String key : urlFormEncoded.keySet()) {
				final String[] value = urlFormEncoded.get(key);
				if (value.length == 1) {
					newData.put(key, value[0]);
				} else if (value.length > 1) {
					String keyPrefix = key;
					String keyPostfix = "";
					final int pos = key.indexOf(".");
					if (pos > -1) {
						keyPrefix = key.substring(0, pos);
						keyPostfix = key.substring(pos, key.length());
					}
					for (int i = 0; i < value.length; i++) {
						newData.put(keyPrefix + "[" + i + "]" + keyPostfix,
								value[i]);
					}
				}
			}
		}
		return newData;
	}

}
