package model.instance;

import model.knowledge_base.senticnet.SenticNetParser;

public class SenticNetParserInstance {

	private static SenticNetParser senticNetParser;

	static {
		senticNetParser = new SenticNetParser();
	}

	public static synchronized SenticNetParser getInstance() {
		return senticNetParser;
	}

}
