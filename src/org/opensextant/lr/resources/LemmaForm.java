package org.opensextant.lr.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opensextant.lr.Common.LRTYPE;

public class LemmaForm extends LanguageResource {

	private Map<String, List<LanguageResource>> affixMap = new HashMap<String, List<LanguageResource>>();

	public LemmaForm() {

	}

	public LemmaForm(String lem) {
		super(lem);
	}
	@JsonIgnore
	public Map<String, List<LanguageResource>> getAffixMap() {
		return affixMap;
	}
	@JsonIgnore
	public void setAffixMap(Map<String, List<LanguageResource>> affixMap) {
		this.affixMap = affixMap;
	}

	public void addAffixEntry(String affix, LanguageResource bf) {

		if (!this.affixMap.containsKey(affix)) {
			this.affixMap.put(affix, new ArrayList<LanguageResource>());
		}

		this.affixMap.get(affix).add(bf);

	}

	@Override
	public LRTYPE getStoreType() {
		return LRTYPE.lemma;
	}

}
