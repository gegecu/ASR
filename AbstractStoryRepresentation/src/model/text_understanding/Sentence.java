package model.text_understanding;

import java.util.Map;
import java.util.TreeMap;

public class Sentence {

	private Map<Integer, Word> words;

	public Sentence() {
		words = new TreeMap<Integer, Word>();
	}

	public void addWord(Integer key, Word value) {
		words.put(key, value);
	}

	public Word getWord(Integer key) {
		return words.get(key);
	}

	public String getWholeSentence() {
		String sentence = "";
		for (Map.Entry<Integer, Word> entry : words.entrySet()) {
			sentence += entry.getValue().getInfo("text") + " ";
		}
		return sentence;
	}

	public void removeWord(Integer key) {
		words.remove(key);
	}

}
