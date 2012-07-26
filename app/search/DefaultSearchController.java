package search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import play.data.Form;
import play.data.validation.Constraints.Required;

import play.mvc.Controller;
import de.objectcode.play2.plugin.search.EntityConfig;
import de.objectcode.play2.plugin.search.SearchPlugin;

public class DefaultSearchController extends Controller {

	protected static Form<SearchForm> form = form(SearchForm.class);
	protected static final Integer DEFAULT_MAX_ROWS = 25;
	
	/**
	 * search and find primary keys.
	 * 
	 * FIXME: add Filter to restrict search to the entity's fields
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	protected static List<Long> find(final String q, final int maxRows, final EntityConfig entityConfig,
		final String[] searchFields) throws IOException, ParseException {
		
		IndexReader reader = null;
		try {
			reader = IndexReader.open(SearchPlugin.Search.get().getDirectory());
			final IndexSearcher searcher = new IndexSearcher(reader);

			final QueryParser parser = new MultiFieldQueryParser(entityConfig.getVersion(), searchFields,
					entityConfig.getAnalyser());

			parser.setAllowLeadingWildcard(true);
			parser.setDefaultOperator(Operator.AND);

			final Query query = parser.parse(q);
			final TopDocs top = searcher.search(query, null, maxRows);
			final ScoreDoc[] hits = top.scoreDocs;

			final List<Long> list = new ArrayList<Long>(hits.length);

			for (int i = 0; i < hits.length; i++) {
				final Long id = Long.parseLong(searcher.doc(hits[i].doc).get(
						entityConfig.fieldName(entityConfig.indexField())));
				if (id == null) continue;
				list.add(id);
			}
			return list;
		} finally {
			reader.close();
		}
	}
	
	public static class SearchForm {
		@Required
		public String query; 
		public Integer maxRows;
	}
	
}
