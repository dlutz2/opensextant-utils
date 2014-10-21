package org.opensextant.lr.resources;

import org.opensextant.lr.Common.POSTAG;

public class POSCount {

	POSTAG pos;
	Integer count;

	public POSCount() {
		pos = null;
		count = null;
	}

	public POSCount(POSTAG tag, Integer cnt) {
		pos = tag;
		count = cnt;
	}

	public POSTAG getPos() {
		return pos;
	}

	public void setPos(POSTAG pos) {
		this.pos = pos;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	public String toString(){
		return this.pos + ":" + this.count;
	}
}
