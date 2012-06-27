package models;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Version;

import net.coobird.thumbnailator.Thumbnails;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import eu.medsea.mimeutil.MimeUtil;

@SuppressWarnings("serial")
@Entity
public class RawImage extends Model {

	static {
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
	}
	
	private static Finder<Long, RawImage> finder = new Finder<Long, RawImage>(Long.class, RawImage.class);
	
    @Id
    public Long id;
    @Version
    public Timestamp lastUpdate;
	@Lob
	@Required
	public byte[] image;
	@Required
	public int width;
	@Required
	public int height;
	@Required
	public String mimetype;	
	
	public static RawImage byId(Long id) {
		return finder.byId(id);
	}
	
	public void setImage(byte[] bytes) {
		try {
			Image img = ImageIO.read(new ByteArrayInputStream(bytes));
			if (img==null) {
				throw new RuntimeException("unknown image format");
			}
			this.width = img.getWidth(null);
			this.height = img.getHeight(null);
			this.mimetype = mimetype(bytes);
			this.image = bytes;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public byte[] createThumbnail(int thumbnailWidth, int thumbnailHeight) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(thumbnailWidth*thumbnailHeight*4);
		try {
			Thumbnails.of(new ByteArrayInputStream(getImage())).size(thumbnailWidth, thumbnailHeight).outputFormat("png").toOutputStream(out);
		} catch (IOException e) {
			throw new RuntimeException("can't create thumbnail",e);
		}
		byte[] thumbbytes = out.toByteArray();
		return thumbbytes; 
	}

	/**
	 * paranoid - don't trust any transmitted mime type
	 */
	private static String mimetype(byte[] bytes) {
		Collection<?> mimeTypes = MimeUtil.getMimeTypes(bytes);
		if (mimeTypes == null || mimeTypes.isEmpty()) {
			throw new RuntimeException("unknown mimetype");
		}
		return mimeTypes.iterator().next().toString();
	}
	
	public Long getId() {
		return id;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public byte[] getImage() {
		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getMimetype() {
		return mimetype;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
		RawImage other = (RawImage) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

	@Override
	public String toString() {
		return "RawImage [id=" + id + ", width=" + width + ", height=" + height + ", mimetype=" + mimetype + "]";
	}
    
}
