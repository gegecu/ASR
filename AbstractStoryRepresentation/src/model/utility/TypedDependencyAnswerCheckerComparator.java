package model.utility;

import java.util.Comparator;

import edu.stanford.nlp.trees.TypedDependency;

public class TypedDependencyAnswerCheckerComparator implements Comparator<TypedDependency> {
	public int compare(TypedDependency td1, TypedDependency td2) {

		if (td1.reln().toString().equals("compound")) {
			return -1;
		} else if (td2.reln().toString().equals("compound")) {
			return 1;
		}

		return 0;

	}
}
