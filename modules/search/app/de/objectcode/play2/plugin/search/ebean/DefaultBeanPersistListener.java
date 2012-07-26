package de.objectcode.play2.plugin.search.ebean;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import play.Logger;

import com.avaje.ebean.event.BeanPersistListener;

import de.objectcode.play2.plugin.search.EntityConfig;
import de.objectcode.play2.plugin.search.SearchPlugin;

public abstract class DefaultBeanPersistListener<T> implements BeanPersistListener<T>, EntityConfig {

	@Override
	public boolean inserted(T _bean) {
		return false;
	}

	@Override
	public boolean updated(T _bean, Set<String> _updatedProperties) {
		return false;
	}

	@Override
	public boolean deleted(T _bean) {
		return false;
	}

	@Override
	public void remoteInsert(Object _id) {
	}

	@Override
	public void remoteUpdate(Object _id) {
	}

	@Override
	public void remoteDelete(Object _id) {
	}
	
	protected IndexWriter getWriter() {
		try {
			final Directory dir = SearchPlugin.Search.get().getDirectory();
			final IndexWriterConfig config = new IndexWriterConfig(getVersion(), getAnalyser());
			return new IndexWriter(dir, config);
		} catch (final Exception e) {
			Logger.error("Could not abtain IndexWriter due to " + e, e);
		}
		return null;
	}
	
	protected void closeWriter(final IndexWriter writer) {
		if (writer == null) return;
		try {
			writer.close();
		} catch (final Exception e) {
			Logger.error("Could not close IndexWriter due to " + e, e);
		}
	}
	
	protected void perform(WriterCallback callback) {
		IndexWriter writer = null;
		try {
			writer = getWriter();
			callback.perform(writer);
		} catch (final Exception e) {
			Logger.error("Could not update Index due to " + e, e);
		} finally {
			closeWriter(writer);
		}
	}
	
	protected void delete(final Term term) {
		final IndexWriter writer = getWriter();
		try {
			writer.deleteDocuments(term);
		} catch (final Exception e) {
			Logger.error("Could not delete for term=" + term + " from index due to " + e, e);
		} finally {
			closeWriter(writer);
		}
	}

	public static interface WriterCallback {
		void perform(final IndexWriter indexWriter) throws CorruptIndexException, IOException;
	}
	
}
