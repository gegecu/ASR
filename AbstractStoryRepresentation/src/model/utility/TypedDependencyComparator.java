package model.utility;

import java.util.Comparator;

import edu.stanford.nlp.trees.TypedDependency;

public class TypedDependencyComparator implements Comparator<TypedDependency> {

	public int compare(TypedDependency arg0, TypedDependency arg1) {

		if (arg0.reln().toString().equals("nsubj")) {
			return -1;
		} else if (arg1.reln().toString().equals("nsubj")) {
			return 1;
		}

		return 0;

	}

}
