package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints;

import com.avaje.ebean.annotation.PrivateOwned;

@SuppressWarnings("serial")
@Entity
public class Ding extends CrudModel<Ding> {

    public static final ModelFinder find = new ModelFinder();

    @Constraints.Required
    @NonEmpty
    public String name;
    public String description;
    public boolean special;
    @Constraints.Required
    @Column(precision=15, scale=2)    
    public BigDecimal price;
	@ManyToOne(fetch=FetchType.EAGER)
	public User user;
	public Date someDate;
	public Timestamp someTime;
	public DingEnum dingEnum;
	@ManyToOne(fetch=FetchType.EAGER)
	public DbImage image;
	
	// Springs formbinding does not permit Set (which would be semantically correct), the collection must be indexed, so using List!
	@ManyToMany(fetch=FetchType.EAGER)
	public List<User> watchedBy;
	
	@PrivateOwned
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ding")
	public List<UnterDing> unterDinge;

	// Currently not supported by eBean!
	//  	@ElementCollection(targetClass=DingEnum.class)
	//    @Enumerated(EnumType.STRING)
	//    @CollectionTable(name="ding_dingenums")
	//    @Column(name="dingenum")
	//    public Collection<DingEnum> dingEnumSet;
	// workaround: use a one-to-many entity wich only contains the string 
	
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getSomeDate() {
		return someDate;
	}

	public void setSomeDate(Date someDate) {
		this.someDate = someDate;
	}

	public Timestamp getSomeTime() {
		return someTime;
	}

	public void setSomeTime(Timestamp someTime) {
		this.someTime = someTime;
	}

	public DingEnum getDingEnum() {
		return dingEnum;
	}

	public void setDingEnum(DingEnum dingEnum) {
		this.dingEnum = dingEnum;
	}

	public List<UnterDing> getUnterDinge() {
		return unterDinge;
	}

	public void setUnterDinge(List<UnterDing> unterDinge) {
		this.unterDinge = unterDinge;
	}

	public DbImage getImage() {
		return image;
	}

	public void setImage(DbImage image) {
		this.image = image;
	}
	
	public List<User> getWatchedBy() {
		return watchedBy;
	}

	public void setWatchedBy(List<User> watchedBy) {
		this.watchedBy = watchedBy;
	}

	@Override
	public CrudFinder<Ding> getCrudFinder() {
		return find;
	}
    
	public static class ModelFinder extends CrudFinder<Ding> {
		
		public ModelFinder() {
			super(new Finder<Long, Ding>(Long.class, Ding.class),"name");
		}

		public Ding byName(String name) {
			return byLabel(name);
		}
	}
	
}
