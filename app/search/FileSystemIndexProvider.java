package search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import de.objectcode.play2.plugin.search.DirectoryProvider;


public class FileSystemIndexProvider implements DirectoryProvider {

	@Override
	public Directory provide() {
		try {
			return new NIOFSDirectory(new File("/tmp/luceneIndex"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
