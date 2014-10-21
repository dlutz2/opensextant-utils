package org.opensextant.lr.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class GoogleGramAggregrator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File indir = new File(args[0]);
		File outFile = new File(args[1]);

		// the basename of the 2009 google n-gram files
		String fileNameBase = "googlebooks-eng-all-1gram-20090715-";

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();

		}

		String line = "";
		long linesRead = 0L;
		long linesWritten = 0L;
		String currentGram = "Unigram";
		long totalGramCount = 0L;
		long totalPageCount = 0L;
		long totalVolumeCount = 0L;
		int yearCount = 0;
		// loop over all 10 n-gram files
		for (int i = 0; i < 10; i++) {

			File gramFile = new File(indir, fileNameBase + i + ".csv.zip");
			BufferedReader buff = null;
			ZipFile zf = null;

			try {
				zf = new ZipFile(gramFile);
				ZipEntry ze = (ZipEntry) zf.entries().nextElement();
				buff = new BufferedReader(new InputStreamReader(
						zf.getInputStream(ze)));
			} catch (ZipException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			System.out.println("opening " + gramFile.getName());

			// loop over the ngram file
			while (true) {

				try {
					line = buff.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (line == null) {
					break;
				}

				linesRead++;
				// tab separated
				String[] pieces = line.split("\t");

				// gram<tab>year<tab>count<tab>numPages<tab>numVolumes
				String gram = pieces[0];
				// String year = pieces[1];
				long gramCount = Long.parseLong(pieces[2]);
				long pageCount = Long.parseLong(pieces[3]);
				long volumeCount = Long.parseLong(pieces[4]);

				if (gram.equals(currentGram)) {
					// accumulate
					totalGramCount = totalGramCount + gramCount;
					totalPageCount = totalPageCount + pageCount;
					totalVolumeCount = totalVolumeCount + volumeCount;
					yearCount++;
				} else {
					// write
					if (totalGramCount > 0L) {
						linesWritten++;
						try {
							out.write(currentGram + "\t" + totalGramCount
									+ "\t" + totalPageCount + "\t"
									+ totalVolumeCount + "\t" + yearCount);
							out.newLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}// else{
						// System.err.println("Zero count for token " +
						// currentGram);
					// }
					currentGram = gram;
					totalGramCount = gramCount;
					totalPageCount = pageCount;
					totalVolumeCount = volumeCount;
					yearCount = 0;
				}

			}// end read loop
			try {
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end file set loop
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Read " + linesRead + ". Wrote " + linesWritten);

	}

}
