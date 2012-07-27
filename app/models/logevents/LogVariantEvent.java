package models.logevents;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Constraints;

import models.CrudFinder;
import models.CrudModel;
import models.MvTestVariant;
import models.User;

@SuppressWarnings("serial")
@Entity
@Table(name = "log_variant_event")
public class LogVariantEvent extends CrudModel<LogVariantEvent> {
	
	public static final ModelFinder find = new ModelFinder();
	
	@ManyToOne
	private User user; 
	@Constraints.Required
	@ManyToOne
	private MvTestVariant mvTestVariant;
	@Constraints.Required
	private Timestamp date;
	@Constraints.Required
	private String info;
	
	public LogVariantEvent() {
	}

	public LogVariantEvent(final User user, final MvTestVariant mvTestVariant, final String info) {
		this.user = user; 
		this.mvTestVariant = mvTestVariant;
		this.info = info;
		date = new Timestamp(System.currentTimeMillis());
	}
	
	@Override
	public String label() {
		return String.valueOf(id);
	}
	@Override
	public CrudFinder<LogVariantEvent> getCrudFinder() {
		return find;
	} 
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User _user) {
		user = _user;
	}

	public MvTestVariant getMvTestVariant() {
		return mvTestVariant;
	}

	public void setMvTestVariant(MvTestVariant _mvTestVariant) {
		mvTestVariant = _mvTestVariant;
	}

	public Timestamp getDate() {
		return date;
	}
	
	public void setDate(Timestamp _date) {
		date = _date;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String _info) {
		info = _info;
	}

	public static class ModelFinder extends CrudFinder<LogVariantEvent> {
		public ModelFinder() {
			super(new Finder<Long, LogVariantEvent>(Long.class, LogVariantEvent.class),"id");
		}
	}
	
}
