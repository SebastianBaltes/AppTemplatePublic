package search.entities;

import java.io.IOException;
import java.util.Set;

import models.Ding;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import play.Logger;

public class DingListener extends DefaultEntityConfig<Ding> {

	public static final DingListener INSTANCE = new DingListener();
	
	public static final int FIELD_ID = 0;
	public static final int FIELD_SPECIAL = 1;
	public static final int FIELD_NAME = 2;
	public static final int FIELD_DESCRIPTION = 3;
	public static final int FIELD_PRICE = 4;
	public static final int FIELD_SOME_DATE = 5;
	public static final int FIELD_SOME_TIME = 6;
	public static final int FIELD_DING_ENUM = 7;
	public static final int FIELD_USER_EMAIL = 8;
	
	public static final String[] ALL_FIELDS = new String[] {
		"ding.id",
		"ding.special",
		"ding.name",
		"ding.description",
		"ding.price",
		"ding.someDate",
		"ding.someTime",
		"ding.dingEnum",
		"ding.user_email"
	};
	
	@Override
	public String[] fieldNames() {
		return ALL_FIELDS;
	}

	@Override
	public String fieldName(int _index) {
		return ALL_FIELDS[_index];
	}
	
	@Override
	public int indexField() {
		return FIELD_ID;
	}	
	
	private void insert(final Ding ding) {
		perform(new WriterCallback() {
			@Override
			public void perform(final IndexWriter indexWriter) throws CorruptIndexException, IOException {
				final Document doc = new Document();
				
				doc.add(new Field(fieldName(FIELD_ID), String.valueOf(ding.getId()), Store.YES, Index.NOT_ANALYZED));
				doc.add(new Field(fieldName(FIELD_SPECIAL), String.valueOf(ding.isSpecial()), Store.NO, Index.ANALYZED));

				if (ding.getName() != null) 
					doc.add(new Field(fieldName(FIELD_NAME), ding.getName(), Store.YES, Index.ANALYZED));
				if (ding.getDescription() != null)
					doc.add(new Field(fieldName(FIELD_DESCRIPTION), ding.getDescription(), Store.NO, Index.ANALYZED));
				if (ding.getPrice() != null)
					doc.add(new Field(fieldName(FIELD_PRICE), ding.getPrice().toString(), Store.NO, Index.ANALYZED));
				if (ding.getSomeDate() != null)
					doc.add(new Field(fieldName(FIELD_SOME_DATE), ding.getSomeDate().toString(), Store.NO, Index.ANALYZED));
				if (ding.getSomeTime() != null)
					doc.add(new Field(fieldName(FIELD_SOME_TIME), ding.getSomeTime().toString(), Store.NO, Index.ANALYZED));
				if (ding.getDingEnum() != null)
					doc.add(new Field(fieldName(FIELD_DING_ENUM), ding.getDingEnum().toString(), Store.NO, Index.ANALYZED));
				if (ding.getUser() != null && ding.getUser().getEmail() != null)
					doc.add(new Field(fieldName(FIELD_USER_EMAIL), ding.getUser().getEmail(), Store.NO, Index.ANALYZED));
				
				indexWriter.addDocument(doc);
			}
		});
	}
	
	public boolean inserted(final Ding ding) {
		Logger.info("DingListener::inserted = " + ding);
		insert(ding);
		return false;
	}

	@Override
	public boolean updated(final Ding ding, final Set<String> updatedProperties) {
		Logger.info("DingListener::updated = " + ding);
		deleted(ding);
		insert(ding);
		return false;
	}

	@Override
	public boolean deleted(final Ding ding) {
		Logger.info("DingListener::deleted = " + ding);
		delete(new Term(fieldName(FIELD_ID), String.valueOf(ding.getId())));
		return false;
	}
}
