package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints;

import com.avaje.ebean.annotation.PrivateOwned;

@SuppressWarnings("serial")
@Entity
public class MvTestFeature extends CrudModel<MvTestFeature> {

	private final static Random rnd = new Random(); 
	
	public static final ModelFinder find = new ModelFinder();

	@Constraints.Required
	@NonEmpty
	public String name;

	@Constraints.Required
	public Timestamp startTime;

	@Constraints.Required
	public Timestamp endTime;

	@Column(length=1024) 
	public String description;

	public long fixRandomMask = rnd.nextLong();	
	
	@PrivateOwned
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "feature")
	@OrderBy("index ASC")
	public List<MvTestVariant> variants;

	public String validate() {
		double sum = 0;
		for (MvTestVariant v : variants) {
			sum += v.getPercent();
		}
		if (Math.abs(100-sum)>0.099) {
			return "Die Prozente der Varianten ergeben nicht 100 Prozent, sondern "+String.format("%.1f",sum)+" Prozent!";
		}
		if (!startTime.before(endTime)) {
			return "Die Startzeit "+startTime+" darf nicht später als die Endzeit "+endTime+" sein!";
		}
        return null;
    }	
	
	public boolean isCurrentlyActive() {
		Date now = new Date();
		return getStartTime().compareTo(now)<=0 && now.compareTo(getEndTime())<=0; 
	}
	
	public long getFixRandomMask() {
		return fixRandomMask;
	}

	public void setFixRandomMask(long fixRandomMask) {
		this.fixRandomMask = fixRandomMask;
	}

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

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public List<MvTestVariant> getVariants() {
		return variants;
	}

	public void setVariants(List<MvTestVariant> variants) {
		this.variants = variants;
	}

	@Override
	public CrudFinder<MvTestFeature> getCrudFinder() {
		return find;
	}

	public static class ModelFinder extends CrudFinder<MvTestFeature> {

		public ModelFinder() {
			super(new Finder<Long, MvTestFeature>(Long.class,
					MvTestFeature.class), "name");
		}

		public MvTestFeature byName(String name) {
			return byLabel(name);
		}
	}

	public MvTestVariant getVariantFor(long originalRandomNumber) {
		double randomNumber = pseudoRandomNumber(originalRandomNumber);
		double sumUntilNow = 0;
		for (Iterator<MvTestVariant> i = variants.iterator(); i.hasNext();) {
			MvTestVariant variant = i.next();
			sumUntilNow+=variant.getPercent()/100.0d;
			if (randomNumber<=sumUntilNow || !i.hasNext()) {
				return variant;
			}
		}
		return null;
	}
	
	/**
	 * Erzeugt eine neue "Pseudo-Zufallszahl" im Intervall [0.0,1.0[, die fest von randomNumber und fixRandomMask abhängt 
	 * und die gleichverteilt ist, da randNumber und fixRandomMask gleichverteilt sind.
	 * Im Prinzip wird randomNumber bezüglich fixRandomMask gehashed.
	 * Wozu das ganze? Um auszuschließen, dass die Varianten unterschiedlicher Features zu den Usern korreliert sind  
	 */
	private double pseudoRandomNumber(long randomNumber) {
		return Math.abs(randomNumber ^ fixRandomMask)/((double)Long.MAX_VALUE);
	}
	
	/**
	 * usefull for tests
	 */
	public long getFakeRandomNumberMatchingGivenPercentageFactor(double percentageFactor) {
		return (long)(percentageFactor*Long.MAX_VALUE) ^ fixRandomMask;
	}

}
