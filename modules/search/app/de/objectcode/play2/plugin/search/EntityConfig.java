package de.objectcode.play2.plugin.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;


public interface EntityConfig {
	String[] fieldNames();
	String fieldName(final int index);
	int indexField();
	
	Analyzer getAnalyser();
	Version getVersion();	
}
