import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;

public class NewFileOnRebootAppender extends FileAppender {

	static String file_directory = "log";
	static String file_name = "log4j";
	static String file_extension = "log";

	public NewFileOnRebootAppender() {
	}

	@Override
	public void setFile(String file) {
		super.setFile(prependDate(file));
	}

	private static String prependDate(String filename) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH;mm;ss");
		return file_directory + "/" + file_name + "_"
				+ dateFormat.format(new Date()) + "." + file_extension;
	}

}