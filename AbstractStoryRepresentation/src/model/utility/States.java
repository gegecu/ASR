package model.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class States {

	public static final Map<String, String> CONFLICT_RESOLUTION;

	static {
		Map<String, String> temp = new HashMap<>();
		temp.put("sad", "happy");
		temp.put("hungry", "full");
		CONFLICT_RESOLUTION = Collections.unmodifiableMap(temp);
	}

	public static final List<String> STATES;

	static {
		List<String> temp = new ArrayList<>();
		temp.add("sad");
		temp.add("happy");
		temp.add("hungry");
		temp.add("full");
		STATES = Collections.unmodifiableList(temp);
	}

}
