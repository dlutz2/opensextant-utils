package org.opensextant.lr.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.opensextant.lr.LanguageResourceUtils;
import org.opensextant.lr.resources.POSEntry;
import org.opensextant.lr.store.LanguageResourceStore;

public class LoadPOSLexiconTest {

	public static void main(String[] args) {

		String host = args[0];
		// get the lexicon file
		File lexFile = new File(args[1]);

		LanguageResourceStore store = new LanguageResourceStore(host, 9300);

		System.out.println("Created Word Stat Store");

		loadLexicon(lexFile, store);
		store.flush();

	}

	public static void loadLexicon(File lexFile, LanguageResourceStore store) {
		BufferedReader br = null;
		int entryCount = 0;
		;
		int lineCount = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					lexFile), "UTF-8"));

			String line;

			while ((line = br.readLine()) != null) {
				lineCount++;

				POSEntry tmpEntry = LanguageResourceUtils.parsePOSLine(line);
				String base = LanguageResourceUtils
						.baseForm(tmpEntry.getWord());
				tmpEntry.setBaseForm(base);
				tmpEntry.setStore(store);
				tmpEntry.save();
				entryCount++;
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

		System.out.println("Read " + lineCount + " lines, created "
				+ entryCount + " entries");

	}

}
