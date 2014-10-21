package org.opensextant.lr.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.opensextant.lr.Common;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.Common.POSFAMILY;
import org.opensextant.lr.resources.Category;
import org.opensextant.lr.resources.LanguageResource;
import org.opensextant.lr.resources.Vocabulary;
import org.opensextant.lr.store.LanguageResourceStore;

public class LoadVocabTest {

	static Map<String, Vocabulary> vocMap = new HashMap<String, Vocabulary>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String host = args[0];
		File catFile = new File(args[1]);
		File vocabDir = catFile.getParentFile();

		String taxon = "OpenSextant";
		LineIterator lineIter = null;
		try {
			lineIter = FileUtils.lineIterator(catFile, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LanguageResourceStore store = new LanguageResourceStore(host, 9300);
		System.out.println("Created Word Stat Store");

		List<LanguageResource> cats = store
				.getAllLanguageResources(LRTYPE.category);

		// read and discard header
		lineIter.next();

		while (lineIter.hasNext()) {
			String line = lineIter.next();

			// <filepath> : <LABEL> : <POSFAMILY>.<HIERARCHY> - with category
			// <filepath> : <LABEL> - no category
			String[] pieces = line.split(":");

			String vPath = pieces[0];
			File vFile = new File(vocabDir, vPath);
			Category cat = null;
			POSFAMILY family = POSFAMILY.OTHER;
			if (pieces.length == 3) {
				// String vPath = pieces[0];
				String label = pieces[1];
				String familyAndHierarchy = pieces[2];
				String[] subPieces = familyAndHierarchy.split("\\.", 2);

				String hier = label.toUpperCase();
				if (subPieces.length == 2) {
					try {
						family = Common.POSFAMILY.valueOf(subPieces[0]);
					} catch (Exception e) {
						family = POSFAMILY.OTHER;
						System.err.println("Unknown POSFAMILY:" + subPieces[0]
								+ " used with Category" + pieces[2]);
						// log error and continue
					}

					hier = subPieces[1];
				}

				cat = new Category(label, hier, taxon);
				if (!cats.contains(cat)) {
					System.err.println("Unknown category:" + label + "/" + hier
							+ " Adding it");
					store.save(cat);
					cats.add(cat);
				}
			}

			if (cat != null) {
				loadCategorizedFile(vFile, family, cat, store);
				// System.out.println("Loading " + vFile.getName()
				// + ": as Categorized vocabulary");
			} else {
				loadUncategorizedFile(vFile, store);
				System.out.println("Loading " + vFile.getName()
						+ ": as Uncategorized vocabulary");
			}

		}

		store.flush();
		
		System.out.println("System has:");
		for (LRTYPE t : Common.LRTYPE.values()) {
			List<LanguageResource> lrs = store.getAllLanguageResources(t);
			System.out.println(lrs.size() + " " + t.name());
		}

	}

	public static void loadCategorizedFile(File singleVocab, POSFAMILY family,
			Category cat, LanguageResourceStore store) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					singleVocab), "UTF-8"));
			String word;
			while ((word = br.readLine()) != null) {
				word = word.trim();
				Vocabulary voc = null;
				if (vocMap.containsKey(word)) {
					voc = vocMap.get(word);
				} else {
					voc = new Vocabulary();
					voc.setStore(store);
					voc.setWord(word);
					vocMap.put(word, voc);
				}
				voc.addCategory(family, cat);
				voc.save();
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Could read  vocab  file " + e.getMessage());
		} catch (FileNotFoundException e) {
			System.err.println("Could read  vocab  file " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Could read  vocab  file " + e.getMessage());
		}

	}

	public static void loadUncategorizedFile(File singleVocab,
			LanguageResourceStore store) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					singleVocab), "UTF-8"));
			String word;
			while ((word = br.readLine()) != null) {
				word = word.trim();
				Vocabulary voc = null;
				if (vocMap.containsKey(word)) {
					voc = vocMap.get(word);
				} else {
					voc = new Vocabulary();
					vocMap.put(word, voc);
					voc.setWord(word);
					voc.setStore(store);
				}
				voc.save();
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Could read  vocab  file " + e.getMessage());
		} catch (FileNotFoundException e) {
			System.err.println("Could read  vocab  file " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Could read  vocab  file " + e.getMessage());
		}

	}

}
