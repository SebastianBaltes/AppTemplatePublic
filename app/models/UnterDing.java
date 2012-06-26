package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints;

@SuppressWarnings("serial")
@Entity
public class UnterDing extends CrudModel<UnterDing> {

    public static final ModelFinder find = new ModelFinder();

    @Constraints.Required
    @NonEmpty
    public String name;
	@ManyToOne(fetch=FetchType.EAGER)
	public Ding ding;
	@OneToOne
	public DbImage image;
	
    @Override
    public String label() {
    	return name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Ding getDing() {
		return ding;
	}

	public void setDing(Ding ding) {
		this.ding = ding;
	}

	public DbImage getImage() {
		return image;
	}

	public void setImage(DbImage image) {
		this.image = image;
	}

	@Override
	public CrudFinder<UnterDing> getCrudFinder() {
		return find;
	}
    
	public static class ModelFinder extends CrudFinder<UnterDing> {
		
		public ModelFinder() {
			super(new Finder<Long, UnterDing>(Long.class, UnterDing.class),"name");
		}

		public UnterDing byName(String name) {
			return byLabel(name);
		}
	}
	

}
