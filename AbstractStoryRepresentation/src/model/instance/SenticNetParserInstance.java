package model.instance;

import java.util.concurrent.Semaphore;

import model.knowledge_base.senticnet.SenticNetParser;

public class SenticNetParserInstance {

	private static Semaphore semaphore;
	private static SenticNetParser senticNetParser;

	static {
		semaphore = new Semaphore(0);
		senticNetParser = new SenticNetParser();
		semaphore.release(1);
	}

	public static synchronized SenticNetParser getInstance() {
		try {
			semaphore.acquire();
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return senticNetParser;
	}

}
