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

// flatten the vocabulary into single tab separated file

public class VocabFlattener {

	// word to type index
	static HashMap<String, List<String>> wordIndex = new HashMap<String, List<String>>();
	// type to words index
	static HashMap<String, List<String>> typeIndex = new HashMap<String, List<String>>();

	static BufferedWriter vocabWriter = null;
	
	public static void main(String[] args) {

		// get the vocabulary definition file
		File vocabDefFile = new File(args[0]);

		// where do the reports go
		File outputDir = new File(args[1]);

		// create the report files
		File flatVocabFile = new File(outputDir, "flatVocab.txt");

		//BufferedWriter vocabWriter = null;


		try {
			vocabWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(flatVocabFile), "UTF-8"));

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

				//cleanFile(vFile);
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
		
		
		for(String w: wordIndex.keySet()){
			List<String> types = wordIndex.get(w);
			
			for(String t : types){
				try {
					vocabWriter.write(w + "\t" + t);
					vocabWriter.newLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
		try {
			vocabWriter.flush();
			vocabWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
