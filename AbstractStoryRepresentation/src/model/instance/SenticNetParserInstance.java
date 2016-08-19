package model.instance;

import model.knowledge_base.senticnet.SenticNetParser;

/**
 * Singleton implementation of SenticNetParser
 */
public class SenticNetParserInstance {

	/**
	 * The singleton SencticNetParser instance
	 */
	private static SenticNetParser senticNetParser;

	static {
		senticNetParser = new SenticNetParser();
	}

	/**
	 * Returns the singleton SenticNetParser instance
	 * 
	 * @return SenticNetParser instance
	 */
	public static synchronized SenticNetParser getInstance() {
		return senticNetParser;
	}

}
