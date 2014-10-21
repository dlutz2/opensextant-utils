package org.opensextant.lr.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.opensextant.lr.Common.ORTHOTYPE;
import org.opensextant.lr.resources.OrthoStats;
import org.opensextant.lr.store.LanguageResourceStore;

public class LoadOrthoTest {

	public static void main(String[] args) {

		String host = args[0];
		// get the wordstats file
		File wordstatsFile = new File(args[1]);

		LanguageResourceStore store = new LanguageResourceStore(host, 9300);

		System.out.println("Created Word Stat Store");

		loadStats(wordstatsFile, store);
		store.flush();

	}

	public static void loadStats(File stats, LanguageResourceStore store) {
		BufferedReader br = null;
		int entryCount = 0;
		
		int lineCount = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					stats), "UTF-8"));

			String line;
			// read and discard header
			br.readLine();

			while ((line = br.readLine()) != null) {
				lineCount++;

				String[] pieces = line.split("\t");
				String name = pieces[0];
				Long upper = Long.parseLong(pieces[1]);
				Long lower = Long.parseLong(pieces[2]);
				Long init = Long.parseLong(pieces[3]);
				Long mixed = Long.parseLong(pieces[4]);

				OrthoStats ortho = new OrthoStats(name);

				ortho.addCount(upper, ORTHOTYPE.UPPER);
				ortho.addCount(lower, ORTHOTYPE.LOWER);
				ortho.addCount(init, ORTHOTYPE.INITIAL);
				ortho.addCount(mixed, ORTHOTYPE.MIXED);

				ortho.setStore(store);
				ortho.analyze();
				ortho.save();
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
