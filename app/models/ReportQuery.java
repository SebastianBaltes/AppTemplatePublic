package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import play.data.validation.Constraints;

import com.avaje.ebean.annotation.PrivateOwned;

@Entity
@SuppressWarnings("serial")
public class ReportQuery extends CrudModel<ReportQuery> {

	public static final ModelFinder find = new ModelFinder();

	@Constraints.Required
	private String name;
	private String description;
	@Constraints.Required
	private String query;
	
	@PrivateOwned
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "reportQuery")
	@OrderBy("id ASC")
	private List<ReportQueryParameter> queryParameters;

	@Override
	public String label() {
		return "name";
	}

	@Override
	public CrudFinder<ReportQuery> getCrudFinder() {
		return find;
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String _query) {
		query = _query;
	}

	public List<ReportQueryParameter> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(List<ReportQueryParameter> _queryParameters) {
		queryParameters = _queryParameters;
	}

	public static class ModelFinder extends CrudFinder<ReportQuery> {

		public ModelFinder() {
			super(new Finder<Long, ReportQuery>(Long.class, ReportQuery.class), "name");
		}
	}

}
