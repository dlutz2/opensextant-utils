package org.opensextant.lr.resources;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.store.LanguageResourceStore;
import org.opensextant.tagger.ElasticDocument;

public class LanguageResource extends ElasticDocument implements Comparable<Object> {

	static String wordFieldName = "word";
	static String baseFormFieldName = "baseForm";
	static String storeTypeFieldName = "storeType";


	@Override
	public String getId() {
		return this.getBaseForm();
	}


	@Override
	public void setId(String id) {
		this.setBaseForm(id);
	}

	@JsonIgnore
	protected LanguageResourceStore store;

	public LanguageResource() {

	}

	public LanguageResource(String word) {
		this.setField(wordFieldName, word);
	}

	@JsonIgnore
	public String getWord() {
		return (String) this.getField(wordFieldName);
	}
	@JsonIgnore
	public void setWord(String word) {
		this.setField(wordFieldName, word);
	}

	@JsonIgnore
	public String getBaseForm() {
		String base = (String) this.getField(baseFormFieldName);
		if (base == null) {
			return this.getWord().toLowerCase();
		}
		return base;
	}

	public void setBaseForm(String baseForm) {
		this.setField(baseFormFieldName, baseForm);
	}
	
	@JsonIgnore
	public LRTYPE getStoreType() {
		LRTYPE typ = (LRTYPE) this.getField(storeTypeFieldName);
		if(typ == null){
			return LRTYPE.generic;
		}
		return typ;
	};

	public void setStoreType(LRTYPE typ){
		this.setField(storeTypeFieldName, typ);
	}
	
	
	public LanguageResourceStore getStore() {
		return store;
	}

	public void setStore(LanguageResourceStore store) {
		this.store = store;
	}

	public void save() {
		if (this.store != null) {
			this.store.save(this);
		} else {
			// trying to save with no store defined
		}

	}

	
	
	@Override
	public String toString() {
		return this.getWord() + "/" + this.getBaseForm() + " (" + this.getStoreType() + "): " + this.getFields();
	}


	@Override
	public int compareTo(Object other) {

		if (!(other instanceof LanguageResource)) {
			return 0;
		}
		LanguageResource lr = (LanguageResource) other;
		if (this.getWord().equals(lr.getWord()) && this.getStoreType() == lr.getStoreType()) {
			return 0;
		}
		return this.getWord().compareTo(lr.getWord());

	}

}
