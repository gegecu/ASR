package model.utility;

import java.util.Comparator;

import edu.stanford.nlp.trees.TypedDependency;

public class TypedDependencyComparator implements Comparator<TypedDependency> {

	public int compare(TypedDependency td1, TypedDependency td2) {

		String td1Reln = td1.reln().toString();
		String td2Reln = td2.reln().toString();

		if (td1Reln.equals("neg")) {
			return 1;
		} else if (td2Reln.equals("neg")) {
			return -1;
		} else if (td1Reln.contains("nsubj")) {
			return -1;
		} else if (td2Reln.contains("nsubj")) {
			return 1;
		} else if (td1Reln.contains("conj")) {
			return -1;
		} else if (td2Reln.contains("conj")) {
			return 1;
		} else if (td1Reln.equals("dobj")) {
			return -1;
		} else if (td2Reln.equals("dobj")) {
			return 1;
		}

		return 0;

	}

}
