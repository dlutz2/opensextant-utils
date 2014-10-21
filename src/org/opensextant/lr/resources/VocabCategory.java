package org.opensextant.lr.resources;

import org.opensextant.lr.Common.POSFAMILY;

public class VocabCategory {

	POSFAMILY family;
	Category category;

	public VocabCategory() {

	}

	public POSFAMILY getFamily() {
		return family;
	}

	public void setFamily(POSFAMILY family) {
		this.family = family;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	@Override
	public String toString(){
		return this.family + "-" + this.category;
	}
	
}
