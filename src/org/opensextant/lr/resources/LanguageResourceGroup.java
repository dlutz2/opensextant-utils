package org.opensextant.lr.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensextant.lr.Common.LRTYPE;

public class LanguageResourceGroup {

	private Map<LRTYPE, List<LanguageResource>> resMap = new HashMap<LRTYPE, List<LanguageResource>>();

	public List<LanguageResource> getResources(LRTYPE typ) {
		if (this.resMap.containsKey(typ)) {
			return resMap.get(typ);
		}
		return new ArrayList<LanguageResource>();
	}

	public Set<LRTYPE> getResourceTypes() {
		return this.resMap.keySet();
	}
	
	
	public void setResources(LRTYPE typ, List<LanguageResource> res) {
		resMap.put(typ, res);
	}

	public void addResources(List<LanguageResource> resources) {
		for (LanguageResource lr : resources) {
			this.addResources(lr);
		}
	}

	public void addResources(LanguageResource resource) {
		LRTYPE typ = resource.getStoreType();
		if(!this.resMap.containsKey(resource.getStoreType())){
			this.resMap.put(typ, new ArrayList<LanguageResource>());
		}
		resMap.get(resource.getStoreType()).add(resource);
	}

	public void save() {
		for (LRTYPE typ : this.resMap.keySet()) {
			List<LanguageResource> resources = this.resMap.get(typ);
			for (LanguageResource lr : resources) {
				lr.save();
			}
		}
	}

	@Override
	public String toString() {
		return this.resMap.toString();
	}

}
