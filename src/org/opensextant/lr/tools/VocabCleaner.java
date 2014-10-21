package org.opensextant.lr.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

// clean the vocabulary by normalizing white space, reducing to lower case and sorting
//  then look at vocabulary for
// 1) words in more than 1 category (not an error, just informative)
// 2) which which are missing either the singular or plural forms
public class VocabCleaner {

	// word to type index
	static HashMap<String, List<String>> wordIndex = new HashMap<String, List<String>>();
	// type to words index
	static HashMap<String, List<String>> typeIndex = new HashMap<String, List<String>>();

	public static void main(String[] args) {

		// get the vocabulary definition file
		File vocabDefFile = new File(args[0]);

		// where do the reports go
		File outputDir = new File(args[1]);

		// create the report files
		File ambigReportFile = new File(outputDir, "ambigVocab.txt");
		File pluralReportFile = new File(outputDir, "missingPlural.txt");
		File pluralDetailsFile = new File(outputDir, "singlePlurals.txt");

		BufferedWriter ambigWriter = null;
		BufferedWriter pluralWriter = null;
		BufferedWriter numberWriter = null;

		try {
			ambigWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(ambigReportFile), "UTF-8"));
			pluralWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(pluralReportFile), "UTF-8"));
			numberWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(pluralDetailsFile), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.println("Couldn't get writer for report output"
					+ e.getMessage());
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't get writer for report output"
					+ e.getMessage());
		}

		loadIndexes(vocabDefFile);

		System.out.println(wordIndex.size() + " words");
		System.out.println(typeIndex.size() + " categories");

		// look for words in more than 1 category
		for (String word : wordIndex.keySet()) {
			List<String> tmpList = wordIndex.get(word);
			Collections.sort(tmpList);

			if (tmpList.size() > 1) {
				try {
					ambigWriter.write(word + "\t" + tmpList.size() + "\t"
							+ tmpList);
					ambigWriter.newLine();
				} catch (IOException e) {
					System.err.println("Couldn't write to ambig report"
							+ e.getMessage());
				}
				// System.out.println(word + "\t" + tmpList);
			}
		}

		try {
			ambigWriter.flush();
		} catch (IOException e1) {
			System.err
					.println("Couldn't flush  ambig report" + e1.getMessage());

		}

		// look for missing plurals

		for (String type : typeIndex.keySet()) {

			// list of all phrases of a single category
			List<String> wordList = new ArrayList<String>();
			wordList.addAll(typeIndex.get(type));

			for (String word : wordList) {

				boolean foundMatch = false;
				String sing = "";
				String plural = "";

				if (wordList.contains(word + "s")) {
					foundMatch = true;
					sing = word;
					plural = word + "s";
				}

				if (word.endsWith("s")
						&& wordList.contains(word.replaceAll("s$", ""))) {
					foundMatch = true;
					plural = word;
					sing = word.replaceAll("s$", "");
				}

				if (word.endsWith("man")
						&& wordList.contains(word.replaceFirst("man$", "men"))) {
					foundMatch = true;
					sing = word;
					plural = word.replaceFirst("man$", "men");
				}

				if (word.endsWith("men")
						&& wordList.contains(word.replaceFirst("men$", "man"))) {
					foundMatch = true;
					plural = word;
					sing = word.replaceFirst("men$", "man");
				}

				if (wordList.contains(word + "es")) {
					foundMatch = true;
					sing = word;
					plural = word + "es";
				}

				if (word.endsWith("es")
						&& wordList.contains(word.replaceAll("es$", ""))) {
					foundMatch = true;
					plural = word;
					sing = word.replaceAll("es$", "");
				}

				if (word.endsWith("y")
						&& wordList.contains(word.replaceAll("y$", "") + "ies")) {
					foundMatch = true;
					sing = word;
					plural = word.replaceAll("y$", "") + "ies";
				}

				if (word.endsWith("ies")
						&& wordList.contains(word.replaceAll("ies$", "") + "y")) {
					foundMatch = true;
					plural = word;
					sing = word.replaceAll("ies$", "") + "y";
				}

				if (!foundMatch) {
					try {
						pluralWriter.write(word + "\t" + type);
						pluralWriter.newLine();
					} catch (IOException e) {
						System.err.println("Could write to plural report"
								+ e.getMessage());
					}

				} else {
					try {
						numberWriter.write(sing + "\t" + plural + "\t" + type);
						numberWriter.newLine();
					} catch (IOException e) {
						System.err.println("Could write to plural details file"
								+ e.getMessage());
					}
				}
			}

		}

		try {
			pluralWriter.flush();
			numberWriter.flush();
		} catch (IOException e) {
			System.err.println("Couldn't flush ambig report" + e.getMessage());

		}

	}

	public static void cleanFile(File singleVocab) {

		BufferedReader br = null;
		BufferedWriter bw = null;
		SortedSet<String> list = new TreeSet<String>();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					singleVocab), "UTF-8"));
			String word;
			while ((word = br.readLine()) != null) {
				// remove extra spaces,convert to lower case and trim
				word = word.replaceAll("\\s+", " ").toLowerCase().trim();

				if (word.length() > 0) {
					list.add(word);
				}
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}

		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(singleVocab), "UTF-8"));

			for (String word : list) {
				bw.write(word);
				bw.newLine();
			}

			bw.close();
		} catch (UnsupportedEncodingException e) {
			System.err.println("Could write to vocab file " + e.getMessage());
		} catch (FileNotFoundException e) {
			System.err.println("Could write to vocab file " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Could write to vocab file " + e.getMessage());
		}

	}

	public static void loadIndexes(File vocabDefFile) {
		File vocabDir = vocabDefFile.getParentFile();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					vocabDefFile), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {

				// line = relative file path:Label:hierarchy
				String[] pieces = line.split(":");

				String vPath = pieces[0];
				File vFile = new File(vocabDir, vPath);
				String label = pieces[1];
				String hier = label.toUpperCase();
				if (pieces.length == 3) {
					hier = pieces[2];
				}

				cleanFile(vFile);
				loadFile(vFile, hier);
				// System.out.println(vFile + "\t" + label + "\t" + hier);

			}
		} catch (FileNotFoundException e1) {
			System.err.println("Could read  vocab def file " + e1.getMessage());

		} catch (IOException e1) {
			System.err.println("Could read  vocab def file " + e1.getMessage());
		}

		try {
			br.close();
		} catch (IOException e) {
			System.err.println("Could read  vocab def file " + e.getMessage());
		}
	}

	public static void loadFile(File singleVocab, String type) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					singleVocab), "UTF-8"));
			String word;
			while ((word = br.readLine()) != null) {
				word = word.trim();

				if (!wordIndex.containsKey(word)) {
					wordIndex.put(word, new ArrayList<String>());
				}

				if (!typeIndex.containsKey(type)) {
					typeIndex.put(type, new ArrayList<String>());
				}

				wordIndex.get(word).add(type);
				typeIndex.get(type).add(word);

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
