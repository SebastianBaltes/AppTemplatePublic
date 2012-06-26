package models;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class ImageData {

	@Lob
	public byte[] image;
	public int width;
	public int height;
	public String mimetype;
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
	
}
