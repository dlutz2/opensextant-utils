package org.opensextant.lr;

//import gate.creole.ResourceInstantiationException;
//import gate.creole.morph.Interpret;

import gate.creole.ResourceInstantiationException;
import gate.creole.morph.Interpret;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

import org.opensextant.lr.Common.ORTHOTYPE;
import org.opensextant.lr.Common.POSTAG;
import org.opensextant.lr.Common.TOKENTYPE;
import org.opensextant.lr.resources.POSEntry;
import org.opensextant.phonetic.Phoneticizer;
//import org.tartarus.snowball.SnowballStemmer;
//import org.tartarus.snowball.ext.porterStemmer;

public class LanguageResourceUtils {

	// the thing that creates the base form of the ngram
	static Phoneticizer phoner = new Phoneticizer();
	// static SnowballStemmer stemmer = new englishStemmer();
	// static SnowballStemmer stemmer = new porterStemmer();

	public static Dictionary wnDict = null;
	static Interpret interpret = new Interpret();

	static {
		try {
			wnDict = Dictionary.getDefaultResourceInstance();
			URL rulesURL = LanguageResourceUtils.class.getClassLoader()
					.getResource("org/opensextant/lr/default.rul");
			interpret.init(rulesURL);

		} catch (JWNLException | ResourceInstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ------- Ortho Patterns ----------
	// all uppercase letters plus some punct
	static String upperPattern = "[-'\\.\\p{Lu} ]+";
	// all lowercase letters plus some punct
	static String lowerPattern = "[-'\\.\\p{Ll} ]+";
	// a single uppercase followed by lowercase plus some punct
	static String initialPattern = "\\p{Lu}[-'\\.\\p{Ll} ]+";
	// any combo of letters and some punct
	static String mixedPattern = "[-'\\.\\p{L} ]+";
	// english possessive
	static String possessivePattern = ".*'[sS]$|.*[sS]'";

	static Pattern upper = Pattern.compile(upperPattern);
	static Pattern lower = Pattern.compile(lowerPattern);
	static Pattern initial = Pattern.compile(initialPattern);
	static Pattern mixed = Pattern.compile(mixedPattern);
	static Pattern possessive = Pattern.compile(possessivePattern);

	// -------- TokenType patterns ----------------------
	static String wordPattern = "[-'\\.\\p{L}]+";
	static String numberPattern = ".*[\\p{Nd}].*";
	static String punctPattern = ".*[+\\p{P}].*";

	static Pattern word = Pattern.compile(wordPattern);
	static Pattern number = Pattern.compile(numberPattern);
	static Pattern punct = Pattern.compile(punctPattern);

	public static ORTHOTYPE casePattern(String word) {

		if (LanguageResourceUtils.upper.matcher(word).matches()) {
			return ORTHOTYPE.UPPER; // "upper";
		}

		if (LanguageResourceUtils.lower.matcher(word).matches()) {
			return ORTHOTYPE.LOWER; // "lower";
		}

		if (LanguageResourceUtils.initial.matcher(word).matches()) {
			return ORTHOTYPE.INITIAL; // "initial";
		}

		if (LanguageResourceUtils.mixed.matcher(word).matches()) {
			return ORTHOTYPE.MIXED; // "mixed";
		}

		// System.err.println("Bad match " + word);
		return ORTHOTYPE.OTHER; // "mixed";
	}

	// line = word<space>POS_tag<space>count ...
	public static POSEntry parsePOSLine(String line) {

		String[] pieces = line.split("\\s+");
		String word = pieces[0];
		POSEntry pos = new POSEntry(word);

		int len = pieces.length;
		if ((len & 1) == 0) {
			System.err.println("Bad input line. Wrong number of components "
					+ word);
			return null;
		}

		int numEntries = (len - 1) / 2;

		for (int i = 1; i <= numEntries; i++) {
			String tagString = pieces[2 * i - 1];
			Integer cnt = Integer.valueOf(pieces[2 * i]);
			POSTAG tag = null;
			try {
				tag = POSTAG.valueOf(tagString);
			} catch (Exception e) {
				System.err.println("Unknown POSTag:" + tagString
						+ " used in word" + word);
				// log error and continue
			}

			if (tag != null) {
				pos.addEntry(tag, cnt);
			}
		}

		return pos;
	}

	public static String baseForm(String gram) {
		return stripPossessive(LanguageResourceUtils.phoner.phoneticForm(gram,
				"Diacritic_Insensitive").toLowerCase());
	}

	public static boolean isPossessive(String gram) {
		return possessive.matcher(gram).matches();
	}

	public static String stripPossessive(String gram) {
		if (isPossessive(gram)) {
			return gram.replaceFirst("'[sS]$", "").replaceFirst("'$", "");
		}
		return gram;
	}

	public static String[] stem(String gram) {
		String[] ret = { "", "" };

		String rootWord = interpret.runMorpher(gram, "*");
		String affix = interpret.getAffix();

		if (affix == null || affix.equalsIgnoreCase(" ")) {
			affix = "";
		}

		ret[0] = rootWord;
		ret[1] = affix;

		return ret;
	}

	public static String commonStart(String base, String stem) {

		if (base.equalsIgnoreCase(stem)) {
			return base;
		}

		if (base.startsWith(stem)) {
			return stem;
		}

		int l = Math.min(base.length(), stem.length());
		String common = "";
		for (int i = 0; i < l; i++) {
			if (base.charAt(i) != stem.charAt(i)) {
				common = base.substring(0, i);
				break;
			}
		}

		return common;
	}

	public static String findSuffix(String base, String stem) {
		String suffix = "";

		if (stem.equalsIgnoreCase(base)) {
			return suffix;
		}

		if (base.startsWith(stem)) {
			suffix = base.substring(stem.length());
			return suffix;
		}

		String common = LanguageResourceUtils.commonStart(base, stem);

		suffix = base.substring(common.length());

		// adjustments needed here?

		return suffix;

	}

	private static IndexWord getWNIndexWord(String word) {

		try {
			for (POS pos : POS.getAllPOS()) {
				IndexWord iw = wnDict.lookupIndexWord(pos, word);
				if (iw != null) {
					return iw;
				}
			}
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String[] adjustStem(String stem, String suffix) {

		boolean handled = false;
		String adjStem = stem;
		String adjSuffix = suffix;
		String guess = "";

		if (stem.endsWith("i")) {
			guess = stem.replaceFirst("i$", "y");
			if (getWNIndexWord(guess) != null) {
				adjStem = guess;
				adjSuffix = suffix.replaceFirst("^y", "");
				handled = true;
			}
		}

		guess = stem + "e";
		if (!handled && getWNIndexWord(guess) != null
				&& !suffix.equalsIgnoreCase("ed")) {
			adjStem = guess;
			adjSuffix = suffix.replaceFirst("^e", "");
			handled = true;
		}

		guess = stem + "y";
		if (!handled && getWNIndexWord(guess) != null) {
			adjStem = guess;
			adjSuffix = suffix.replaceFirst("^y", "");
			handled = true;
		}

		String[] ret = { adjStem, adjSuffix };

		return ret;
	}

	public static List<IndexWord> getWordNetVocab() {

		List<IndexWord> indexWords = new ArrayList<IndexWord>();

		for (POS pos : POS.getAllPOS()) {
			Iterator<IndexWord> iwIter = null;
			try {
				iwIter = wnDict.getIndexWordIterator(pos);
			} catch (JWNLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (iwIter.hasNext()) {
				indexWords.add(iwIter.next());
			}
		}
		return indexWords;
	}

	private static String getWNIndexWordWord(String word) {

		IndexWord iw = getWNIndexWord(word);

		if (iw != null) {
			return iw.getLemma();
		}

		return null;
	}

	public static String internString(String in) {
		if (in != null) {
			in = in.intern();
		}
		return in;
	}

	static TOKENTYPE TokenType(String tok) {

		if (word.matcher(tok).matches()) {
			return TOKENTYPE.WORD;
		}

		if (number.matcher(tok).matches()) {
			return TOKENTYPE.NUMBER;
		}

		if (punct.matcher(tok).matches()) {
			return TOKENTYPE.PUNCT;
		}

		return TOKENTYPE.OTHER;

	}

}
