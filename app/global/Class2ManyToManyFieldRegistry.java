package global;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.ManyToMany;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import play.Logger;

@SuppressWarnings("rawtypes")
public final class Class2ManyToManyFieldRegistry {
	
	private static final Class2ManyToManyFieldRegistry instance = new Class2ManyToManyFieldRegistry();
	
	public static final Class2ManyToManyFieldRegistry get() {
		return instance;
	}
	
	private Map<Class,List<String>> manyToManyfieldMap = new ConcurrentHashMap<Class, List<String>>();
	
	public List<String> get(Class clazz) {
		List<String> list = manyToManyfieldMap.get(clazz);
		if (list==null) {
			list = new ArrayList<String>();
			Field[] fields = clazz.getFields();
			for (Field field : fields) {
            	ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            	if (manyToMany!=null) {
            		list.add(field.getName());
            	}
			}
			manyToManyfieldMap.put(clazz, list);
		}
		return list;
	}
	
}
