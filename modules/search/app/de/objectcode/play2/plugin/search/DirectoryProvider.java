package de.objectcode.play2.plugin.search;

import org.apache.lucene.store.Directory;


public interface DirectoryProvider {
	public Directory provide();
}
