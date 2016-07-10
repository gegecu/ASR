package utility;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class EvaluationLog {

	private static String fileDir = "log/Evaluation Log/";
	private static String filename = "log/Evaluation Log/Eval Log";
	private static PrintWriter pw;

	static {
		File file = new File(fileDir);
		if (!(file.exists())) {
			file.mkdirs();
		}
		for (int i = 0;; i++) {
			String name = filename + " " + i + ".txt";
			file = new File(name);
			if (file.exists())
				continue;
			try {
				pw = new PrintWriter(new FileWriter(file), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public static void log(String string) {
		pw.println(string);
	}

}
