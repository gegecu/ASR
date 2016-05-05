package model.instance;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;

public class JLanguageToolInstance {

	private static JLanguageTool langTool;

	static {
		langTool = new JLanguageTool(new BritishEnglish());
	}

	public static synchronized JLanguageTool getInstance() {
		return langTool;
	}

}
