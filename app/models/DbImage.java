package models;

import javax.persistence.Entity;
import javax.persistence.Lob;

@SuppressWarnings("serial")
@Entity
public class DbImage extends CrudModel<DbImage> {

	public static final ModelFinder find = new ModelFinder();

	public String filename;
	public String label;
	@Lob
	public byte[] image;
	public int width;
	public int height;
	public String mimetype;	
	@Lob
	public byte[] thumbnail;
	public int thumbnailWidth;
	public int thumbnailHeight;
	public String thumbnailMimetype;	
	
	public void saveOrUpdate() {
		if (id == null) {
			save();
		} else {
			update();
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	public String getThumbnailMimetype() {
		return thumbnailMimetype;
	}

	public void setThumbnailMimetype(String thumbnailMimetype) {
		this.thumbnailMimetype = thumbnailMimetype;
	}

	@Override
	public CrudFinder<DbImage> getCrudFinder() {
		return find;
	}

	@Override
	public String label() {
		return filename;
	}

	public static class ModelFinder extends CrudFinder<DbImage> {

		public ModelFinder() {
			super(new Finder<Long, DbImage>(Long.class, DbImage.class),"label");
		}

		public DbImage byFilename(String filename) {
	        return finder.where().eq("filename", filename).findUnique();
		}
	}

}
