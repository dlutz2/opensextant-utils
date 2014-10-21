package org.opensextant.lr.resources;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.Common.POSFAMILY;

public class Vocabulary extends LanguageResource {

	static String categoriesFieldName = "categories";
	static LRTYPE storeType = LRTYPE.vocabulary;


	public Vocabulary() {
		this.setField(storeTypeFieldName, storeType);
	}

	public Vocabulary(String word) {
		super(word);
		this.setField(storeTypeFieldName, storeType);
	}

	@JsonIgnore
	public List<VocabCategory> getCategories() {
		 List<VocabCategory> tmpList = (List<VocabCategory>) this.getField(categoriesFieldName);
		
		 if(tmpList == null){
			 tmpList = new ArrayList<VocabCategory>();
			 this.setField(categoriesFieldName, tmpList);
		 }
		
		return tmpList;
	}

	public void setCategories(List<VocabCategory> categories) {
		this.setField(categoriesFieldName, categories);
	}

	public void addCategory(POSFAMILY pos, Category cat) {
		VocabCategory vc = new VocabCategory();
		vc.setFamily(pos);
		vc.setCategory(cat);
		this.getCategories().add(vc);
	}

	public String toString() {
		String w =  this.getWord();
		List<VocabCategory> c =  this.getCategories();
		String cs = c.toString();
		
		return w + " " + cs;
	}

	@Override
	public LRTYPE getStoreType() {
		return storeType;
	};

}
