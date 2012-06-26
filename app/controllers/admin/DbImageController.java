package controllers.admin;

import java.awt.Dimension;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import models.DbImage;
import net.coobird.thumbnailator.Thumbnails;

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
import eu.medsea.mimeutil.MimeUtil;

public class DbImageController extends Controller {

	static {
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
	}

	public static Result thumbnail(long id) {
		Logger.info("############### thumbnail("+id+")");
		DbImage image = DbImage.find.byId(id);
		if (image == null) {
			return status(404);
		}
		return ok(image.getThumbnail()).as(image.getThumbnailMimetype());
	}

	public static Result image(long id) {
		Logger.info("############### thumbnail("+id+")");
		DbImage image = DbImage.find.byId(id);
		if (image == null) {
			return status(404);
		}
		return ok(image.getImage()).as(image.getMimetype());
	}

	/**
	 * @return json with {"id":id} of the newly created or updated image
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
			image.setMimetype(getMimeType(bytes, filePart.getContentType()));
			setImageWithSizeAndThumbnail(image, bytes);
			image.saveOrUpdate();
			ObjectNode result = dbImageToJson(image);
			return ok(result);
		} catch (Exception e) {
			return status(400, e.getMessage());
		}
	}

	private static ObjectNode dbImageToJson(DbImage image) {
		ObjectNode result = Json.newObject();
		result.put("id", image.getId());
		result.put("filename", image.getFilename());
		result.put("label", image.getLabel());
		result.put("width", image.getWidth());
		result.put("height", image.getHeight());
		result.put("mimetype", image.getMimetype());
		result.put("thumbnailWidth", image.getThumbnailWidth());
		result.put("thumbnailHeight", image.getThumbnailHeight());
		result.put("thumbnailMimetype", image.getMimetype());
		return result;
	}
	
	
	public static void setImageWithSizeAndThumbnail(DbImage image, byte[] bytes) throws IOException {
		image.setImage(bytes);
		sizeFromImage(image);
		setThumbnailFromImage(image);
	}

	private static void setThumbnailFromImage(DbImage image) throws IOException {
		Configuration config = Play.application().configuration();
		int thumbnailWidth = config.getInt("thumbnails.width");
		int thumbnailHeight = config.getInt("thumbnails.height");
		ByteArrayOutputStream out = new ByteArrayOutputStream(thumbnailWidth*thumbnailHeight*4);
		Thumbnails.of(new ByteArrayInputStream(image.getImage())).size(thumbnailWidth, thumbnailHeight).outputFormat("png").toOutputStream(out);
		image.setThumbnail(out.toByteArray());
		sizeFromThumbnail(image);
	}

	private static void sizeFromImage(DbImage image) throws IOException {
		Dimension d = dimension(image.getImage());
		image.setWidth(d.width);
		image.setHeight(d.height);
	}
	
	private static void sizeFromThumbnail(DbImage image) throws IOException {
		Dimension d = dimension(image.getThumbnail());
		image.setThumbnailWidth(d.width);
		image.setThumbnailHeight(d.height);
	}

	private static Dimension dimension(byte[] bytes) throws IOException {
		Image img = ImageIO.read(new ByteArrayInputStream(bytes));
		return new Dimension(img.getWidth(null),img.getHeight(null));
	}	

	private static Long getId(MultipartFormData body) {
		Map<String, String[]> params = body.asFormUrlEncoded();
		String[] ids = params.get("id");
		String id_ = ids == null || ids.length == 0 ? null : ids[0];
		if (StringUtils.isEmpty(id_)) {
			return null;
		}
		return Long.valueOf(id_);
	}

	/**
	 * paranoid - don't trust the transmitted mime type
	 */
	private static String getMimeType(byte[] bytes, String mimeType) {
		Collection<?> mimeTypes = MimeUtil.getMimeTypes(bytes);
		if (mimeTypes == null || mimeTypes.isEmpty()) {
			return mimeType;
		}
		return mimeTypes.iterator().next().toString();
	}

}
