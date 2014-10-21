package org.opensextant.lr.resources;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.Common.ORTHOTYPE;

public class OrthoStats extends LanguageResource {

	static String mixedFormsFieldName = "mixedForms";
	static String countsFieldName = "counts";
	static String caseDecisionFieldName = "caseDecision";
	static String caseConfidenceFieldName = "caseConfidence";
	static LRTYPE storeType = LRTYPE.orthostat;

	public OrthoStats() {
		this.setField(storeTypeFieldName, storeType);
		Long[] tmpCounts = new Long[ORTHOTYPE.values().length];
		// initialize all form counts to zero
		for (ORTHOTYPE i : ORTHOTYPE.values()) {
			tmpCounts[i.ordinal()] = 0L;
		}
		this.setField(countsFieldName, tmpCounts);

		// set default case decision and confidence
		this.setField(caseDecisionFieldName, ORTHOTYPE.OTHER);
		this.setField(caseConfidenceFieldName, 0.0);

	}

	public OrthoStats(String base) {
		super(base);
		this.setField(storeTypeFieldName, storeType);
		Long[] tmpCounts = new Long[ORTHOTYPE.values().length];
		// initialize all form counts to zero
		for (ORTHOTYPE i : ORTHOTYPE.values()) {
			tmpCounts[i.ordinal()] = 0L;
		}
		this.setField(countsFieldName, tmpCounts);

		// set default case decision and confidence
		this.setField(caseDecisionFieldName, ORTHOTYPE.OTHER);
		this.setField(caseConfidenceFieldName, 0.0);
	}

	// total number of times word has been seen
	public Long totalSeen() {
		Long tmp = this.getCounts()[ORTHOTYPE.UPPER.ordinal()]
				+ this.getCounts()[ORTHOTYPE.LOWER.ordinal()]
				+ this.getCounts()[ORTHOTYPE.INITIAL.ordinal()]
				+ this.getCounts()[ORTHOTYPE.MIXED.ordinal()];
		return tmp;
	}

	// total number of times word has been seen adjusted for sentence initials
	public Long totalSeenAdjusted() {
		Long tmp = this.getCounts()[ORTHOTYPE.UPPER.ordinal()]
				+ this.getCounts()[ORTHOTYPE.LOWER.ordinal()]
				+ this.getCounts()[ORTHOTYPE.INITIAL.ordinal()]
				+ this.getCounts()[ORTHOTYPE.MIXED.ordinal()]
				- this.getCounts()[ORTHOTYPE.SENTENCE_INITIAL.ordinal()];
		return tmp;
	}

	public void addCount(Long count, ORTHOTYPE cse) {
		this.getCounts()[cse.ordinal()] = this.getCounts()[cse.ordinal()]
				+ count;
	}

	public void addCountAndUpdate(Long count, ORTHOTYPE cse) {
		this.getCounts()[cse.ordinal()] = this.getCounts()[cse.ordinal()]
				+ count;
		analyze();
	}

	// analyze counts to determine case decision and confidence
	public void analyze() {
		Long max = -1L;
		ORTHOTYPE dec = ORTHOTYPE.OTHER;

		for (int i = 0; i <= ORTHOTYPE.MIXED.ordinal(); i++) {
			// subtract SENTENCE_INITIAL from INITIAL
			Long adjustedCount = this.getCounts()[i];
			if (i == ORTHOTYPE.INITIAL.ordinal()) {
				adjustedCount = adjustedCount
						- this.getCounts()[ORTHOTYPE.SENTENCE_INITIAL.ordinal()];
			}

			if (adjustedCount > max) {
				dec = ORTHOTYPE.values()[i];
				max = adjustedCount;
			}
		}

		if (dec.ordinal() > ORTHOTYPE.MIXED.ordinal()) {
			System.err.println("Bad decision for " + this.getBaseForm() + "->"
					+ dec);
			dec = ORTHOTYPE.OTHER;
		}

		this.setCaseConfidence(1.0 * max / this.totalSeenAdjusted());
		this.setCaseDecision(dec);

		if (this.getCaseConfidence().isNaN()) {
			this.setCaseConfidence(0.0);
			this.setCaseDecision(ORTHOTYPE.OTHER);
		}
	}
	@JsonIgnore
	public Long getCount(ORTHOTYPE cse) {
		return this.getCounts()[cse.ordinal()];
	}

	public Set<String> getMixedForms() {
		return (Set<String>) this.getField(mixedFormsFieldName);
	}

	public void addMixedForm(String form) {
		this.getMixedForms().add(form);
	}
	@JsonIgnore
	public ORTHOTYPE getCaseDecision() {
		return (ORTHOTYPE) this.getField(caseDecisionFieldName);
	}

	public Double getCaseConfidence() {
		return (Double) this.getField(caseConfidenceFieldName);
	}
	@JsonIgnore
	public Long[] getCounts() {
		return this.getCounts();
	}

	public void setCounts(Long[] counts) {
		this.setField(countsFieldName, counts);
	}

	public void setMixedForms(Set<String> mixedForms) {
		this.setField(mixedFormsFieldName, mixedForms);
	}

	public void setCaseDecision(ORTHOTYPE caseDecision) {
		this.setField(caseDecisionFieldName, caseDecision);
	}

	public void setCaseConfidence(Double caseConfidence) {
		this.setField(caseConfidenceFieldName, caseConfidence);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		// sb.append(this.baseForm + "\t");
		// sb.append(this.stem + "\t");
		// sb.append(this.suffix + "\t");
		sb.append(this.getMixedForms() + "\t");
		sb.append(this.getCaseDecision() + "\t");
		sb.append(this.getCaseConfidence() + "\t");
		sb.append(this.totalSeen());

		for (ORTHOTYPE c : ORTHOTYPE.values()) {
			sb.append("\t" + this.getCounts()[c.ordinal()]);
		}

		return sb.toString();
	}

	// header string that matches the toString form
	public static String headerString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LanguageResource" + "\t");
		// sb.append("Stem" + "\t");
		// sb.append("Suffix" + "\t");
		sb.append("MixedForms" + "\t");
		sb.append("CaseDecision" + "\t");
		sb.append("CaseConfidence");
		sb.append("Total");

		for (ORTHOTYPE c : ORTHOTYPE.values()) {
			sb.append("\t" + c.name());
		}

		return sb.toString();
	}

	@Override
	public LRTYPE getStoreType() {
		return storeType;
	}

}
