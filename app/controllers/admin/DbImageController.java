package controllers.admin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import models.DbImage;
import models.RawImage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.ObjectNode;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class DbImageController extends Controller {

	public static Result image(Long id) {
		RawImage rawImage = RawImage.byId(id);
		if (rawImage == null) {
			return status(404);
		}
		return ok(rawImage.getImage()).as(rawImage.getMimetype());
	}

	/**
	 * @return json with all infos of the newly created or updated dbImage
	 */
	public static Result upload() {
		try {
			MultipartFormData body = request().body().asMultipartFormData();
			List<FilePart> fileParts = body.getFiles();
			if (fileParts.isEmpty()) {
				return status(400, "request contains no fileParts");
			}
			Long id = getId(body);
			FilePart filePart = fileParts.get(0);
			DbImage image = new DbImage();
			image.setId(id);
			image.setFilename(filePart.getFilename());
			image.setLabel(filePart.getFilename());
			File imgFile = filePart.getFile();
			byte[] bytes = FileUtils.readFileToByteArray(imgFile);
			setRawImage(image, bytes);
			if (!isAllowedMimetype(image)) {
				return status(400, "mimetype not allowed");
			}
			setRawThumbnail(image);
			image.saveOrUpdate();
			ObjectNode result = dbImageToJson(image);
			return ok(result);
		} catch (Exception e) {
			Logger.warn(e.getMessage(),e);
			return status(400, e.getMessage());
		}
	}

	private static boolean isAllowedMimetype(DbImage image) {
		Configuration config = Play.application().configuration();
		int index = Arrays.binarySearch(config.getString("image.mimetypes.allowed").split(","),image.getImage().getMimetype());
		return index>=0;
	}

	private static void setRawImage(DbImage image, byte[] bytes) {
		RawImage rawImage = image.getImage();
		if (rawImage==null) {
			rawImage = new RawImage();
			image.setImage(rawImage);
		}
		rawImage.setImage(bytes);
	}

	private static void setRawThumbnail(DbImage image) {
		Configuration config = Play.application().configuration();
		int thumbnailWidth = config.getInt("thumbnails.width");
		int thumbnailHeight = config.getInt("thumbnails.height");
		RawImage thumbnail = image.getThumbnail();
		if (thumbnail==null) {
			thumbnail = new RawImage();
			image.setThumbnail(thumbnail);
		}
		byte[] thumbnailBytes = image.getImage().createThumbnail(thumbnailWidth, thumbnailHeight);
		thumbnail.setImage(thumbnailBytes);
	}
	
	private static ObjectNode dbImageToJson(DbImage image) {
		ObjectNode result = Json.newObject();
		result.put("id", image.getId());
		result.put("filename", image.getFilename());
		result.put("label", image.getLabel());
		result.put("image", rawImageToJson(image.getImage()));
		result.put("thumbnail", rawImageToJson(image.getThumbnail()));
		return result;
	}
	
	
	private static ObjectNode rawImageToJson(RawImage image) {
		ObjectNode result = Json.newObject();
		result.put("id", image.getId());
		result.put("width", image.getWidth());
		result.put("height", image.getHeight());
		result.put("mimetype", image.getMimetype());
		return result;
	}

	private static Long getId(MultipartFormData body) {
		Map<String, String[]> params = body.asFormUrlEncoded();
		String[] ids = params.get("id");
		String id_ = (ids == null || ids.length == 0) ? null : ids[0];
		if (StringUtils.isEmpty(id_)) {
			return null;
		}
		return Long.valueOf(id_);
	}

}
