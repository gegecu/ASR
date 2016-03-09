import noun.Character;
import noun.Location;
import noun.Object;
public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AbstractStoryRepresentation asr = new AbstractStoryRepresentation();
		
		
		//Beginning
		//I went to the park.
		Event e1 = new Event();
		e1.addDoer("Gege", new Character("Gege"));
		e1.setAction("go");
		e1.setLocation(new Location("park"));
		e1.setPolarity(0);
		asr.addEvent(e1);
		
		System.out.println("I went to the park.");
		System.out.println("Character exist? " + asr.getCheckList().isCharacterExist());
		System.out.println("Location exist? " + asr.getCheckList().isLocationExist());
		System.out.println("Conflict exist? " + asr.getCheckList().isConflictExist());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		
		
		//The park has slides.
		Object slide = new Object("slide");
		asr.getNoun("park").addReference("hasA", slide); 
		asr.addNoun("slide", slide); // should asr be in "noun" list too?
		
		System.out.println("The park has slides.");
		System.out.println("Character exist? " + asr.getCheckList().isCharacterExist());
		System.out.println("Location exist? " + asr.getCheckList().isLocationExist());
		System.out.println("Conflict exist? " + asr.getCheckList().isConflictExist());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences().get("hasA").get(0).getId());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		
		
		//I was with my friend John.
		Event e2 = new Event();
		e2.addDoer(asr.getNoun("Gege").getId(), asr.getNoun("Gege"));
		e2.setAction("is"); //unsure with verb
		e2.addDirectObject("John", new Character("John"));
		//hasProperty friend?
		e2.setLocation(asr.getCurrentEvent().getLocation());
		e2.setPolarity(0);
		asr.addEvent(e2);
		
		System.out.println("I was with my friend John.");
		System.out.println("Character exist? " + asr.getCheckList().isCharacterExist());
		System.out.println("Location exist? " + asr.getCheckList().isLocationExist());
		System.out.println("Conflict exist? " + asr.getCheckList().isConflictExist());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		
		
		//John and I had a fight.
		Event e3 = new Event();
		e3.addDoer(asr.getNoun("Gege").getId(), asr.getNoun("Gege"));
		e3.addDoer(asr.getNoun("John").getId(), asr.getNoun("John"));
		e3.setAction("has");
		e3.addDirectObject("fight", new Object("fight"));
		e3.setLocation(asr.getCurrentEvent().getLocation());
		e3.setPolarity(-1);
		asr.addEvent(e3);
		asr.setConflict(e3);
		
		System.out.println("John and I had a fight.");
		System.out.println("Character exist? " + asr.getCheckList().isCharacterExist());
		System.out.println("Location exist? " + asr.getCheckList().isLocationExist());
		System.out.println("Conflict exist? " + asr.getCheckList().isConflictExist());
		System.out.println("Beginning comeplete? " + asr.getCheckList().isBeginningComplete());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		
		
		//Middle
		//John and I were playing basketball
		Event e4 = new Event();
		e4.addDoer(asr.getNoun("Gege").getId(), asr.getNoun("Gege"));
		e4.addDoer(asr.getNoun("John").getId(), asr.getNoun("John"));
		e4.setAction("is playing");
		e4.addDirectObject("basketball", new Object("basketball"));
		e4.setLocation(asr.getCurrentEvent().getLocation());
		e4.setPolarity(0);
		asr.addEvent(e4);
		
		System.out.println("John and I were playing basketball.");
		System.out.println("Series of action? " + asr.getCheckList().isSeriesActionExist());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		
		
		//John did not want to give me the ball.
		Event e5 = new Event();
		e5.addDoer(asr.getNoun("John").getId(), asr.getNoun("John"));
		e5.setAction("did"); //did or did not? //give?
		e5.addReceiver(asr.getNoun("Gege").getId(), asr.getNoun("Gege"));
		e5.addDirectObject("ball", new Object("ball"));
		e5.setLocation(asr.getCurrentEvent().getLocation());
		e5.setPolarity((float)-0.5);
		asr.addEvent(e5);
		
		System.out.println("John did not want to give me the ball.");
		System.out.println("Series of action? " + asr.getCheckList().isSeriesActionExist());
		System.out.println("Middle complete? " + asr.getCheckList().isMiddleComplete());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		//End
		//John apologized
		Event e6 = new Event();
		e6.addDoer(asr.getNoun("John").getId(), asr.getNoun("John"));
		e6.setAction("apologize");
		e6.setLocation(asr.getCurrentEvent().getLocation());
		e6.setPolarity((float) 0.5);
		asr.addEvent(e6);
		asr.setResolution(e6);
		
		
		System.out.println("John apologized.");
		System.out.println("Resolution exist? " + asr.getCheckList().isResolutionExist());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		
		
		
		//He also gave me the ball
		Event e7 = new Event();
		e7.addDoer(asr.getNoun("John").getId(), asr.getNoun("John"));
		e7.setAction("give");
		e7.addReceiver(asr.getNoun("Gege").getId(), asr.getNoun("Gege"));
		e7.addDirectObject(asr.getNoun("ball").getId(), asr.getNoun("ball"));
		e7.setLocation(asr.getCurrentEvent().getLocation());
		e7.setPolarity((float) 0.1);
		asr.addEvent(e7);
		
		System.out.println("John apologized.");
		System.out.println("Resolution exist? " + asr.getCheckList().isResolutionExist());
		System.out.println("Ending complete? " + asr.getCheckList().isEndingComplete());
		System.out.println();
		
		System.out.println("Doers: " + asr.getCurrentEvent().getManyDoers());
		System.out.println("Direct Objects: " + asr.getCurrentEvent().getManyDirectObjects());
		System.out.println("Receivers: " + asr.getCurrentEvent().getManyReceivers());
		System.out.println("action: " + asr.getCurrentEvent().getAction());
		System.out.println("location: " + asr.getCurrentEvent().getLocation().getId() + ", " + asr.getCurrentEvent().getLocation().getAttributes() + ", " + asr.getCurrentEvent().getLocation().getReferences());
		System.out.println("polarity: " + asr.getCurrentEvent().getPolarity());
		System.out.println();
		
		
	}

}
