package org.opensextant.lr.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.opensextant.lr.Common;
import org.opensextant.lr.Common.POSTAG;
import org.opensextant.lr.LanguageResourceUtils;
import org.opensextant.lr.resources.POSCount;
import org.opensextant.lr.resources.POSEntry;

// start of a analysis for the POS lexicon, just looks at single/plural forms of nouns so far
public class POSLexiconCleaner {

	// lexicon entries
	// word (lowercase) to pos types index
	static HashMap<String, List<POSEntry>> wordIndex = new HashMap<String, List<POSEntry>>();
	// pos type to word index
	static HashMap<POSTAG, List<POSEntry>> typeIndex = new HashMap<POSTAG, List<POSEntry>>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// get the lexicon file
		File lexFile = new File(args[0]);

		loadLexicon(lexFile);

		/*
		 * for( String tag : typeIndex.keySet()){ System.out.println(tag);
		 * System.out.println("----------------"); for(POSEntry w
		 * :typeIndex.get(tag) ){ System.out.println("\t" + w.getWord()); } }
		 */

		checkCase();

		// checkPlurals();

		System.out.println();

	}

	public static void loadLexicon(File lexFile) {
		BufferedReader br = null;
		int lineCount = 0;
		int dupeCount = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					lexFile), "UTF-8"));
			String line;

			while ((line = br.readLine()) != null) {
				lineCount++;
				POSEntry tmpEntry = LanguageResourceUtils.parsePOSLine(line);

				String word = tmpEntry.getWord().toLowerCase();

				if (!wordIndex.containsKey(word)) {
					wordIndex.put(word, new ArrayList<POSEntry>());
				} else {
					dupeCount++;
				}

				wordIndex.get(word).add(tmpEntry);

				for (POSCount tag : tmpEntry.getEntries()) {

					if (!typeIndex.containsKey(tag.getPos())) {
						typeIndex.put(tag.getPos(), new ArrayList<POSEntry>());
					}

					typeIndex.get(tag.getPos()).add(tmpEntry);
				}

			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Read " + lineCount + " lines from lexicon file");
		System.out.println("Word Index has " + wordIndex.size() + " entries"
				+ " (" + dupeCount + " words with multiple entries)");
		System.out.println("Type Index has " + typeIndex.size() + " entries");
	}

	public static void checkCase() {

		System.out.println("Multiple entries with Case difference ");
		for (List<POSEntry> entries : wordIndex.values()) {
			Set<POSTAG> types = new TreeSet<POSTAG>();
			if (entries.size() > 1) {
				for (POSEntry e : entries) {
					types.addAll(e.getTags());
				}
				System.out.println("\t" + entries + "\t" + types);
			}
		}

		System.out.println("\n --------  Lowercase propers ------------");
		for (POSEntry entry : typeIndex.get(Common.POSTAG.NP)) {
			String wrd = entry.getWord();
			if (!wrd.matches("[A-Z].*")) {
				System.out.println("\t" + wrd);
			}
		}

		for (POSEntry entry : typeIndex.get(Common.POSTAG.NPS)) {
			String wrd = entry.getWord();
			if (!wrd.matches("[A-Z].+")) {
				System.out.println("\t" + wrd);
			}
		}

		System.out.println("\n --------  Uppercase non-propers ------------");

		for (POSTAG type : typeIndex.keySet()) {

			for (POSEntry entry : typeIndex.get(type)) {
				String wrd = entry.getWord();
				if (!type.name().matches("NP.*")) {
					if (wrd.matches("[A-Z].*")) {
						System.out.println("\t" + wrd + " " + type);
					}
				}
			}

		}

	}

	public static void checkPlurals() {

		List<POSEntry> singles = new ArrayList<POSEntry>();
		singles.addAll(typeIndex.get("NN"));
		// singles.addAll(typeIndex.get("NR"));
		// singles.addAll(typeIndex.get("NP"));

		List<POSEntry> plurals = new ArrayList<POSEntry>();
		plurals.addAll(typeIndex.get("NNS"));
		// plurals.addAll(typeIndex.get("NRS"));
		// plurals.addAll(typeIndex.get("NPS"));

		int singleCount = singles.size();
		int pluralCount = plurals.size();

		int pluralsFoundCount = 0;

		for (POSEntry e : singles) {
			String singleWord = e.getWord();

			List<String> possPlurals = plurals(singleWord);
			boolean foundPlural = false;
			for (String p : possPlurals) {
				List<POSEntry> pl = wordIndex.get(p);
				if (pl != null && hasTag(pl, Common.POSTAG.NNS)) {
					foundPlural = true;

					pluralsFoundCount++;
					// System.out.println(singleWord + "\t" + pl.getWord());
				}

			}
			if (!foundPlural) {
				// System.out.println(singleWord + "\t" + possPlurals);
			}
		}

		System.out.println("Found plurals for " + pluralsFoundCount
				+ " out of " + singleCount + " single nouns");
		System.out.println();

		int singlesFoundCount = 0;

		for (POSEntry plu : plurals) {
			String pluralWord = plu.getWord();

			List<String> possSingles = singles(pluralWord);
			boolean foundSingle = false;
			for (String s : possSingles) {
				List<POSEntry> sngl = wordIndex.get(s);

				if (sngl != null && hasTag(sngl, Common.POSTAG.NN)) {
					foundSingle = true;

					singlesFoundCount++;
					// System.out.println(singleWord + "\t" + pl.getWord());
				}
			}
			if (!foundSingle) {
				// System.out.println(pluralWord + "\t" + possSingles);
			}

		}
		System.out.println("Found singles for " + singlesFoundCount
				+ " out of " + pluralCount + " plural nouns");

	}

	public static boolean hasTag(List<POSEntry> entries, POSTAG tag) {

		for (POSEntry entry : entries) {
			if (entry.hasTag(tag)) {
				return true;
			}
		}

		return false;
	}

	public static List<String> plurals(String singleWord) {

		List<String> plurals = new ArrayList<String>();

		String pl1 = singleWord + "s";
		plurals.add(pl1);

		if (singleWord.endsWith("y")) {
			String pl2 = singleWord.replaceAll("y$", "ies");
			plurals.add(pl2);
		}

		if (singleWord.endsWith("ey")) {
			String pl2 = singleWord.replaceAll("ey$", "ies");
			plurals.add(pl2);
		}

		if (singleWord.endsWith("man")) {
			String pl2 = singleWord.replaceAll("man$", "men");
			plurals.add(pl2);
		}

		if (singleWord.endsWith("o")) {
			String pl2 = singleWord.replaceAll("o$", "oes");
			plurals.add(pl2);
		}

		if (singleWord.endsWith("s")) {
			String pl2 = singleWord.replaceAll("s$", "ses");
			plurals.add(pl2);
		}

		return plurals;

	}

	public static List<String> singles(String pluralWord) {
		List<String> singles = new ArrayList<String>();

		if (pluralWord.endsWith("s")) {
			String sl1 = pluralWord.replaceAll("s$", "");
			singles.add(sl1);
		}

		if (pluralWord.endsWith("ies")) {
			String sl2 = pluralWord.replaceAll("ies$", "y");
			singles.add(sl2);
		}

		if (pluralWord.endsWith("ses")) {
			String sl3 = pluralWord.replaceAll("ses$", "s");
			singles.add(sl3);
		}

		if (pluralWord.endsWith("men")) {
			String sl3 = pluralWord.replaceAll("men$", "man");
			singles.add(sl3);
		}

		return singles;

	}

}
