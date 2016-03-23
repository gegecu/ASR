package model.text_understanding;

import java.util.HashMap;
import java.util.Map;

public class Word {

	private Map<String, String> info;

	public Word() {
		info = new HashMap();
	}

	public void addInfo(String key, String value) {
		info.put(key, value);
	}

	public String getInfo(String key) {
		return info.get(key);
	}
}
