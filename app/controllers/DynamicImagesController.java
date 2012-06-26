package controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import models.CrudFinder;
import models.DbImage;
import play.mvc.Controller;
import play.mvc.Result;
import eu.medsea.mimeutil.MimeUtil;

public class DynamicImagesController extends Controller {
	
//	public static interface ImageGetter {
//		public byte[] getBytes(DbImage hasImage);
//	}
//	
//	static {
//		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
//	}
//	
//	private Map<String,Class<? extends DbImage>> prefix2Class = new HashMap<String, Class<? extends DbImage>>();
//
//	public DynamicImagesController() {
//	}
//	
//	public DynamicImagesController put(String prefix, Class<? extends DbImage> clazz) {
//		prefix2Class.put(prefix, clazz);
//		return this;
//	}
//
//	public Result image(String prefix, long id) {
//		return render(prefix, id, new ImageGetter() {
//			@Override
//			public byte[] getBytes(DbImage hasImage) {
//				return hasImage.getImage();
//			}
//		});
//	}
//	
//	public Result thumbnail(String prefix, long id) {
//		return render(prefix, id, new ImageGetter() {
//			@Override
//			public byte[] getBytes(DbImage hasImage) {
//				return hasImage.getThumbnail();
//			}
//		});
//	}
//	
//	private Result render(String prefix, long id, ImageGetter imageGetter) {
//		Class<? extends DbImage> clazz = prefix2Class.get(prefix);
//		if (clazz==null) {
//			return status(404);
//		}
//		DbImage hasImage = findDynamicHasImage(id, clazz);
//		if (hasImage==null) {
//			return status(404);
//		}
//		byte[] bytes = imageGetter.getBytes(hasImage);
//		return asImage(bytes);
//	}
//
//	private static DbImage findDynamicHasImage(long id, Class<? extends DbImage> clazz) {
//		@SuppressWarnings("rawtypes")
//		CrudFinder finder = DynamicCRUDController.getCrudFinder(clazz);
//		DbImage hasImage = (DbImage)finder.byId(id);
//		return hasImage;
//	}
//
//	private static Result asImage(byte[] bytes) {
//		Collection<?> mimeTypes = MimeUtil.getMimeTypes(bytes);
//		if (mimeTypes==null || mimeTypes.isEmpty()) {
//			return status(500, "unknown mimetype of image");
//		}
//		String mimetype = mimeTypes.iterator().next().toString();
//		return ok(bytes).as(mimetype);
//	}
	
}
