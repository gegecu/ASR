package model.utility;

import java.util.Random;


public class Randomizer {
	public static int random(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
}
