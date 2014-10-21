package org.opensextant.lr;

import org.opensextant.lr.resources.Category;
import org.opensextant.lr.resources.LRPlace;
import org.opensextant.lr.resources.LanguageResource;
import org.opensextant.lr.resources.LemmaForm;
import org.opensextant.lr.resources.OrthoStats;
import org.opensextant.lr.resources.POSEntry;
import org.opensextant.lr.resources.Vocabulary;

public class Common {

	public static String LRNAME = "languageresource";
	public static String KEYFIELD = "id";

	public enum LRTYPE {
		orthostat(OrthoStats.class),
		posentry(POSEntry.class),
		place(LRPlace.class),
		category(Category.class),
		lemma(LemmaForm.class),
		generic(LanguageResource.class),
		vocabulary(Vocabulary.class);

		private Class<? extends LanguageResource> clazz;

		LRTYPE(Class<? extends LanguageResource> clz) {
			this.clazz = clz;
		}

		public Class<? extends LanguageResource> getClazz() {
			return clazz;
		}

	}

	public enum TOKENTYPE {
		WORD, NUMBER, PUNCT, OTHER
	}

	public enum ORTHOTYPE {
		UPPER, LOWER, INITIAL, MIXED, SENTENCE_INITIAL, POSSESSIVE, OTHER
	}

	public enum INFLECTION {
		NONE, S, ED, ING
	}

	public enum POSFAMILY {
		NOUN, PROPER, VERB, ADJ, ADV, CONN, DET, MOD, PUNCT, TO, OTHER
	}

	public enum POSTAG {
		CC(POSFAMILY.CONN, false), CS(POSFAMILY.CONN, false), IN(
				POSFAMILY.CONN, false), ABL(POSFAMILY.DET, false), ABN(
				POSFAMILY.DET, false), ABX(POSFAMILY.DET, false), AP(
				POSFAMILY.DET, false), AT(POSFAMILY.DET, false), DT(
				POSFAMILY.DET, false), DTI(POSFAMILY.DET, false), DTS(
				POSFAMILY.DET, false), DTX(POSFAMILY.DET, false), PP$(
				POSFAMILY.DET, false), WDT(POSFAMILY.DET, false), JJ(
				POSFAMILY.ADJ, true), JJR(POSFAMILY.ADJ, true), JJS(
				POSFAMILY.ADJ, true), JJT(POSFAMILY.ADJ, true), RB(
				POSFAMILY.ADV, true), RBR(POSFAMILY.ADV, true), RN(
				POSFAMILY.ADV, true), RP(POSFAMILY.ADV, true), RBT(
				POSFAMILY.ADV, true), NOT(POSFAMILY.MOD, false), CD(
				POSFAMILY.MOD, false), OD(POSFAMILY.MOD, false), POSS(
				POSFAMILY.MOD, false), QLP(POSFAMILY.MOD, false), QL(
				POSFAMILY.MOD, false), WRB(POSFAMILY.MOD, false), WQL(
				POSFAMILY.MOD, false), EX(POSFAMILY.NOUN, true), NN(
				POSFAMILY.NOUN, true), NNS(POSFAMILY.NOUN, true), NP(
				POSFAMILY.PROPER, true), NPS(POSFAMILY.PROPER, true), NR(
				POSFAMILY.NOUN, true), NRS(POSFAMILY.NOUN, true), PN(
				POSFAMILY.NOUN, false), PP$$(POSFAMILY.NOUN, false), PPL(
				POSFAMILY.NOUN, false), PPLS(POSFAMILY.NOUN, false), PPO(
				POSFAMILY.NOUN, false), PPS(POSFAMILY.NOUN, false), PPSS(
				POSFAMILY.NOUN, false), WP$(POSFAMILY.NOUN, false), WPO(
				POSFAMILY.NOUN, false), WPS(POSFAMILY.NOUN, false), APOS(
				POSFAMILY.PUNCT, false), CLOSEPAREN(POSFAMILY.PUNCT, false), COLON(
				POSFAMILY.PUNCT, false), COMMA(POSFAMILY.PUNCT, false), DASH(
				POSFAMILY.PUNCT, false), DQUOTE(POSFAMILY.PUNCT, false), OPENPAREN(
				POSFAMILY.PUNCT, false), PERIOD(POSFAMILY.PUNCT, false), TIC(
				POSFAMILY.PUNCT, false), TO(POSFAMILY.TO, false), BE(
				POSFAMILY.VERB, false), BED(POSFAMILY.VERB, false), BEDZ(
				POSFAMILY.VERB, false), BEG(POSFAMILY.VERB, false), BEM(
				POSFAMILY.VERB, false), BEN(POSFAMILY.VERB, false), BER(
				POSFAMILY.VERB, false), BEZ(POSFAMILY.VERB, false), DO(
				POSFAMILY.VERB, false), DOD(POSFAMILY.VERB, false), DOZ(
				POSFAMILY.VERB, false), HV(POSFAMILY.VERB, false), HVD(
				POSFAMILY.VERB, false), HVG(POSFAMILY.VERB, false), HVN(
				POSFAMILY.VERB, false), HVZ(POSFAMILY.VERB, false), MD(
				POSFAMILY.VERB, false), VB(POSFAMILY.VERB, true), VBD(
				POSFAMILY.VERB, true), VBG(POSFAMILY.VERB, true), VBN(
				POSFAMILY.VERB, true), VBZ(POSFAMILY.VERB, true);

		private POSFAMILY family;
		private boolean open;

		POSTAG(POSFAMILY fam, boolean open) {
			this.family = fam;
			this.open = open;
		}

		public POSFAMILY getPOSFamily() {
			return this.family;
		}

		public boolean isOpen() {
			return open;
		}

	}

}