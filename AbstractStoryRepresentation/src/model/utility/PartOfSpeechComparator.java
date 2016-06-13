package model.utility;

import java.util.Comparator;

import edu.stanford.nlp.trees.TypedDependency;

public class PartOfSpeechComparator implements Comparator<TypedDependency>{

	@Override
	public int compare(TypedDependency arg0, TypedDependency arg1) {
		// TODO Auto-generated method stub
		if(arg0.reln().equals(arg1.reln())) {
			if (arg0.dep().tag().toString().equals("PRP")) {
				return 1;
			} else if (arg1.dep().tag().toString().equals("PRP")) {
				return -1;
			}
		}

		return 0;
	}

}
