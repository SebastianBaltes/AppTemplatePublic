package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;

@Entity
@SuppressWarnings("serial")
public class ReportQueryParameter extends CrudModel<ReportQueryParameter> {
	
	public static final ModelFinder find = new ModelFinder();
	
	@ManyToOne
	@Constraints.Required
	private ReportQuery reportQuery;
	@Constraints.Required
	private String name; 
	private String description;
	
	@Override
	public String label() {
		return "name";
	}

	@Override
	public CrudFinder<ReportQueryParameter> getCrudFinder() {
		return find;
	}
	
	public ReportQuery getReportQuery() {
		return reportQuery;
	}
	
	public void setReportQuery(ReportQuery _reportQuery) {
		reportQuery = _reportQuery;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String _name) {
		name = _name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String _description) {
		description = _description;
	}
	
	public static class ModelFinder extends CrudFinder<ReportQueryParameter> {
		public ModelFinder() {
			super(new Finder<Long, ReportQueryParameter>(Long.class, ReportQueryParameter.class), "name");
		}
	}
}
