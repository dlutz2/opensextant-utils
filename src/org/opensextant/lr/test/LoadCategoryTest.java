package org.opensextant.lr.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.opensextant.lr.Common;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.Common.POSFAMILY;
import org.opensextant.lr.resources.Category;
import org.opensextant.lr.resources.LanguageResource;
import org.opensextant.lr.store.LanguageResourceStore;

public class LoadCategoryTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		String host = args[0];
		File catFile = new File(args[1]);

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

		// read and discard header
		lineIter.next();
		int catCount = 0;
		while (lineIter.hasNext()) {
			String line = lineIter.next();

			// <filepath> : <LABEL> : <POSFAMILY>.<HIERARCHY> - with category
			// <filepath> : <LABEL> - no category
			String[] pieces = line.split(":");

			// need all three pieces to make a category
			if (pieces.length == 3) {
				// String vPath = pieces[0];
				String label = pieces[1];
				String familyAndHierarchy = pieces[2];
				String[] subPieces = familyAndHierarchy.split("\\.", 2);
				POSFAMILY family = POSFAMILY.OTHER;
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

				Category cat = new Category(label, hier, taxon);
				System.out.println("\t" + cat);
				store.save(cat);
				catCount++;
			}

		}

		store.flush();
		
		
		System.out.println("Wrote " + catCount + " categories");
		List<LanguageResource> catList = store
				.getAllLanguageResources(LRTYPE.category);
		System.out.println("Store has " + catList.size() + " categories");
		for (LanguageResource cat : catList) {
			System.out.println("\t" + cat);
		}

	}

}
