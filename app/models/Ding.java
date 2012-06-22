package models;

import java.math.BigDecimal;

import javax.persistence.Entity;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints;

@SuppressWarnings("serial")
@Entity
public class Ding extends CrudModel<Ding> {

    public static final DingFinder find = new DingFinder();

    @Constraints.Required
    @NonEmpty
    public String name;
    public String description;
    public boolean special;
    @Constraints.Required
    public BigDecimal price;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSpecial() {
		return special;
	}

	public void setSpecial(boolean special) {
		this.special = special;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	protected CrudFinder<Ding> getCrudFinder() {
		return find;
	}
    
	public static class DingFinder extends CrudFinder<Ding> {
		
		public DingFinder() {
			super(new Finder<Long, Ding>(Long.class, Ding.class),"name");
		}

		public Ding byName(String name) {
			return byLabel(name);
		}
	}
	
}
