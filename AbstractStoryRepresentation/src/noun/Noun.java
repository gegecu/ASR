package noun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Noun {
	protected String id;
	protected Map<String, List<String>> attributes;
	protected Map<String, List<Noun>> references;
	
	public Noun(String id) {
		this.id = id;
		this.attributes = new HashMap<String, List<String>>();
		this.references = new HashMap<String, List<Noun>>();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void addAttribute(String key, String attribute) {
		List<String> temp = this.attributes.get(key);
		
		if(temp == null) {
			temp = new ArrayList<String>();
		}
		temp.add(attribute);
		this.attributes.put(key, temp);	
	}
	
	public Map<String, List<String>> getAttributes() {
		return this.attributes;
	}
	
	public void addReference(String key, Noun reference) {
		List<Noun> temp = this.references.get(key);
		
		if(temp == null) {
			temp = new ArrayList<Noun> ();
		}
		temp.add(reference);
		this.references.put(key, temp);	
	}
	
	public Map<String, List<Noun>> getReferences() {
		return this.references;
	}
}
