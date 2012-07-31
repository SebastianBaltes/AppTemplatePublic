package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints;

@SuppressWarnings("serial")
@Entity
public class MvTestVariant extends CrudModel<MvTestVariant> {

	public static final ModelFinder find = new ModelFinder();

	@Constraints.Required
	public int index;

	@Constraints.Required
	@NonEmpty
	public String name;

	@Constraints.Required
	public double percent;

	@Constraints.Required
	@ManyToOne
	public MvTestFeature feature;

	@Override
	public String label() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public MvTestFeature getFeature() {
		return feature;
	}

	public void setFeature(MvTestFeature feature) {
		this.feature = feature;
	}

	@Override
	public CrudFinder<MvTestVariant> getCrudFinder() {
		return find;
	}

	public static class ModelFinder extends CrudFinder<MvTestVariant> {

		public ModelFinder() {
			super(new Finder<Long, MvTestVariant>(Long.class,
					MvTestVariant.class), "name");
		}

		public MvTestVariant byName(String name) {
			return byLabel(name);
		}
	}

}
