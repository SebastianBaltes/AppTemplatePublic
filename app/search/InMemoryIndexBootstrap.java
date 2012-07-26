package search;

import java.util.List;
import models.Ding;
import play.Logger;
import search.entities.DingListener;
import de.objectcode.play2.plugin.search.Bootstrapable;

public class InMemoryIndexBootstrap implements Bootstrapable {

	@Override
	public void create() {
		final List<Ding> all = Ding.find.all();
		Logger.info("about to add " + all.size() + " Ding entities to Lucene-Index");
		
		final DingListener l = new DingListener();
		for (final Ding ding : all) {
			Logger.info("about to index ding=" + ding);
			l.inserted(ding);
		}
	}
}
