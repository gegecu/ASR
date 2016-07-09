package model.utility;

import java.util.Comparator;

import edu.stanford.nlp.trees.TypedDependency;

public class PartOfSpeechComparator implements Comparator<TypedDependency> {

	@Override
	public int compare(TypedDependency td1, TypedDependency td2) {

		if (td1.reln().equals(td2.reln())) {
			if (td1.dep().tag().toString().equals("PRP")) {
				return 1;
			} else if (td2.dep().tag().toString().equals("PRP")) {
				return -1;
			}
		}

		return 0;

	}

}
