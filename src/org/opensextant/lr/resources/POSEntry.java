package org.opensextant.lr.resources;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.Common.POSTAG;

public class POSEntry extends LanguageResource {

	static String posFieldName = "posEntries";
	static LRTYPE storeType = LRTYPE.posentry;


	public POSEntry() {
		this.setField(storeTypeFieldName, storeType);
		this.setField(posFieldName, new ArrayList<POSCount>());
	}

	public POSEntry(String base) {
		super(base);
		this.setField(storeTypeFieldName, storeType);
		this.setField(posFieldName, new ArrayList<POSCount>());
	}

	public boolean hasTag(POSTAG tag) {
		for (POSCount cnt : this.getEntries()) {
			if (cnt.getPos().equals(tag)) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public List<POSTAG> getTags() {
		List<POSTAG> tagList = new ArrayList<POSTAG>();
		for (POSCount tag : this.getEntries()) {
			tagList.add(tag.getPos());
		}
		return tagList;

	}

	@JsonIgnore
	public List<POSCount> getEntries() {
		return (List<POSCount>) this.getField(posFieldName);
	}

	public void setEntries(List<POSCount> entries) {
		this.setField(posFieldName, entries);
	}

	public void addEntry(POSTAG tag, Integer cnt) {

		POSCount tmp = new POSCount(tag, cnt);

		this.getEntries().add(tmp);
	}

	public String toString() {
		String tmp = this.getWord() + " " + this.getEntries();
		return tmp;
	}

	@Override
	public LRTYPE getStoreType() {
		return storeType;
	}

}
