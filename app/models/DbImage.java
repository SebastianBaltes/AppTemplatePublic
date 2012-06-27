package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.avaje.ebean.annotation.PrivateOwned;

import play.data.validation.Constraints.Required;

@SuppressWarnings("serial")
@Entity
public class DbImage extends CrudModel<DbImage> {

	public static final ModelFinder find = new ModelFinder();

	@Required
	public String label;
	@Required
	public String filename;
	@Required
	@PrivateOwned
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public RawImage image;
	@Required
	@PrivateOwned
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public RawImage thumbnail;

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

	public RawImage getImage() {
		return image;
	}

	public void setImage(RawImage image) {
		this.image = image;
	}

	public RawImage getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(RawImage thumbnail) {
		this.thumbnail = thumbnail;
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
			super(new Finder<Long, DbImage>(Long.class, DbImage.class), "label");
		}

		public DbImage byFilename(String filename) {
			return finder.where().eq("filename", filename).findUnique();
		}
	}

}
