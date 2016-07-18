import java.io.File;
import java.util.Scanner;

import org.apache.log4j.Logger;

import model.knowledge_base.MySQLConnector;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import model.text_understanding.TextUnderstanding;

public class BlackBox {

	private static Logger log = Logger.getLogger(BlackBox.class.getName());

	public static void main(String[] args) throws Exception {

		Scanner scan;

		boolean input = false;

		if (!input)
			scan = new Scanner(new File(""));
		else {
			scan = new Scanner(System.in);
		}

		MySQLConnector.getInstance().getConnection();
		AbstractStoryRepresentation asr = new AbstractStoryRepresentation();
		Checklist cl = new Checklist(asr);
		TextUnderstanding textUnderstanding = new TextUnderstanding(asr);

		System.out.println("finished");

		while (scan.hasNextLine()) {
			String in = scan.nextLine();
			try {
				asr = new AbstractStoryRepresentation();
				cl = new Checklist(asr);
				textUnderstanding = new TextUnderstanding(asr);
				textUnderstanding.processInput(in);
				System.out.println(in);
				log.debug(in);
			} catch (Exception e) {
				System.out.println("Error:" + in);
				e.printStackTrace();
			}
		}

		scan.close();

	}

}
