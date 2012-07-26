package search;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import de.objectcode.play2.plugin.search.DirectoryProvider;

public class InMemoryIndexProvider implements DirectoryProvider {

	@Override
	public Directory provide() {
		return new RAMDirectory();
	}

}
