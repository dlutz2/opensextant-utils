package org.opensextant.lr.test;

import java.util.List;
import java.util.Set;

import org.opensextant.lr.Common;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.resources.LanguageResource;
import org.opensextant.lr.resources.LanguageResourceGroup;
import org.opensextant.lr.store.LanguageResourceStore;

public class StoreTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String host = args[0];
		String word = args[1];
		LanguageResourceStore store = new LanguageResourceStore(host, 9300);

		store.flush();
/*
		Set<String> terms = store.terms();
		System.out.println("Store has " + terms.size() + " terms");
		
		for (String term : terms) {
			LanguageResourceGroup wrdGrp = store.getResourceGroup(term);
			Set<LRTYPE> typs = wrdGrp.getResourceTypes();
			if (typs.size() > 2) {
				System.out.println(term + "\t" + typs);
			}
		}
*/
		// for (LRTYPE t : Common.LRTYPE.values()) {
		// List<LanguageResource> lrs = store.getAllLanguageResources(t);
		// System.out.println(lrs.size() + " " + t.name());
		// }

		System.out.println("For word " + word);
		for (LRTYPE t : Common.LRTYPE.values()) {
			LanguageResource lr = store.getResource(word, t);
			if (lr != null) {
				System.out.println("\t" + t.name() + ":" + lr.toString());
			} // else {
				// System.out.println("\t" + t.name() + ":" + null);
			// }
		}

		LanguageResourceGroup grp = store.getResourceGroup(word);

		for (LRTYPE typ : grp.getResourceTypes()) {
			List<LanguageResource> lrs = grp.getResources(typ);
			for (LanguageResource lr : lrs) {
				System.out.println(lr.getStoreType() + "\t" + lr.toString());
			}
		}

		List<LanguageResource> lrs = store.getAllLanguageResources(null);
		System.out.println("All types:" + lrs.size());

	}

}
