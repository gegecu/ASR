package model.utility;

import java.util.Random;

public class Randomizer {

	/**
	 * Returns a random value between the Minimum and Maximum possible value,
	 * inclusive.
	 * 
	 * @param min
	 *            Minimum possible value of the generated integer
	 * @param max
	 *            Maximum possible value of the generated integer
	 * @return Returns a random value between the Minimum and Maximum possible
	 *         value, inclusive.
	 */
	public static int random(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

}
