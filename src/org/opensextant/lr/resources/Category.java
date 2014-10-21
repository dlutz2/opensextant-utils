package org.opensextant.lr.resources;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opensextant.lr.Common.LRTYPE;

public class Category extends LanguageResource {

	static String hierarchyFieldName = "hierarchy";
	static String taxonomyFieldName = "taxonomy";
	static LRTYPE storeType = LRTYPE.category;

	public Category() {
		this.setField(storeTypeFieldName, storeType);
	}

	public Category(String word, String hier, String tax) {
		super(word);
		this.setField(storeTypeFieldName, storeType);
		this.setField(hierarchyFieldName, hier);
		this.setField(taxonomyFieldName, tax);
	}
	@JsonIgnore
	public String getHierarchy() {
		return (String) this.getField(hierarchyFieldName);
	}
	@JsonIgnore
	public String getTaxonomy() {
		return (String) this.getField(taxonomyFieldName);
	}
	@Override
	public String toString() {
		return this.getWord() + " (" + this.getHierarchy() + ":" + this.getTaxonomy() + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getHierarchy() == null) ? 0 : this.getHierarchy().hashCode());
		result = prime * result
				+ ((this.getTaxonomy() == null) ? 0 : this.getTaxonomy().hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (this.getHierarchy() == null) {
			if (other.getHierarchy() != null)
				return false;
		} else if (!this.getHierarchy().equals(other.getHierarchy()))
			return false;
		if (this.getTaxonomy() == null) {
			if (other.getTaxonomy() != null)
				return false;
		} else if (!this.getTaxonomy().equals(other.getTaxonomy()))
			return false;
		return true;
	}

	@Override
	public LRTYPE getStoreType() {
		return storeType;
	}

	
	
}
