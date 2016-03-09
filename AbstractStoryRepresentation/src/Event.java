import java.util.HashMap;
import java.util.List;
import java.util.Map;

import noun.Location;
import noun.Noun;

public class Event {
	
	private String action;
	private Map<String, Noun> doers;
	private Map<String, Noun> receivers;
	private Map<String, Noun> directObject;
	private Location location;	
	private float polarity;
	
	public Event() {
		this.doers = new HashMap<String, Noun>();
		this.receivers = new HashMap<String, Noun>();
		this.directObject = new HashMap<String, Noun>();
		this.location = null;
		this.action = null;
		this.polarity = 0;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public void addDoer(String id, Noun noun) {
		this.doers.put(id, noun);
	}
	
	public Noun getDoer(String id) {
		return this.doers.get(id);
	}
	
	public Map<String, Noun> getManyDoers() {
		return this.doers;
	}
	
	public void addReceiver(String id, Noun noun) {
		this.receivers.put(id, noun);
	}
	
	public Noun getReceiver(String id) {
		return this.receivers.get(id);
	}
	
	public Map<String, Noun> getManyReceivers() {
		return this.receivers;
	}
	
	public void addDirectObject(String id, Noun noun) {
		this.directObject.put(id, noun);
	}
	
	public Noun getDirectObject(String id) {
		return this.directObject.get(id);
	}
	
	public Map<String, Noun> getManyDirectObjects() {
		return this.directObject;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}
	
	public float getPolarity() {
		return this.polarity;
	}
	

}