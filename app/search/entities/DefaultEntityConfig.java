package search.entities;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import de.objectcode.play2.plugin.search.ebean.DefaultBeanPersistListener;

public abstract class DefaultEntityConfig<T> extends DefaultBeanPersistListener<T> {

	@Override
	public Analyzer getAnalyser() {
		return new StandardAnalyzer(getVersion());
	}

	@Override
	public Version getVersion() {
		return Version.LUCENE_36;
	}

}
