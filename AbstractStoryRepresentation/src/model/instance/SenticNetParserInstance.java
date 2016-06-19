package model.instance;

import java.util.concurrent.Semaphore;

import model.knowledge_base.senticnet.SenticNetParser;

public class SenticNetParserInstance {

	private static Semaphore semaphore;
	private static SenticNetParser senticNetParser;
	private static boolean processed;

	static {
		processed = false;
		semaphore = new Semaphore(0);
		senticNetParser = new SenticNetParser();
		semaphore.release(1);
		processed = true;
	}

	public static synchronized SenticNetParser getInstance() {
		if (!processed) {
			try {
				semaphore.acquire();
				semaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return senticNetParser;
	}

}
