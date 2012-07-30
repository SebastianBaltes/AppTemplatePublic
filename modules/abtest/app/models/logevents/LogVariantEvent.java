package models.logevents;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Constraints;

import models.CrudFinder;
import models.CrudModel;
import models.MvTestVariant;

@SuppressWarnings("serial")
@Entity
@Table(name = "log_variant_event")
public class LogVariantEvent extends CrudModel<LogVariantEvent> {
	
	public static final ModelFinder find = new ModelFinder();
	
	private Long userId;
	@Constraints.Required
	@ManyToOne
	private MvTestVariant mvTestVariant;
	@Constraints.Required
	private Timestamp date;
	@Constraints.Required
	private String info;
	
	public LogVariantEvent() {
	}

	public LogVariantEvent(final Long userId, final MvTestVariant mvTestVariant, final String info) {
		this.userId = userId; 
		this.mvTestVariant = mvTestVariant;
		this.info = info;
		date = new Timestamp(System.currentTimeMillis());
	}
	
	@Override
	public String label() {
		return "info";
	}
	@Override
	public CrudFinder<LogVariantEvent> getCrudFinder() {
		return find;
	} 
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long _userId) {
		userId = _userId;
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
			super(new Finder<Long, LogVariantEvent>(Long.class, LogVariantEvent.class),"info");
		}
	}
	
}
