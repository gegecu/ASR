package model.story_representation.story_element.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.story_representation.story_element.StoryElement;
import model.story_representation.story_element.noun.Noun;

public class StorySentence extends StoryElement {

	private Map<String, Noun> subjects;
	private Map<String, Predicate> predicates;
	private Map<String, List<String>> attributes;
	private Map<String, List<Noun>> references;
	private Noun location;
	private List<String> concepts;
	private double polarity;

	public StorySentence() {
		subjects = new HashMap<>();
		predicates = new HashMap<>();
		attributes = new HashMap<>();
		references = new HashMap<>();
	}

	public void setPolarity(double polarity) {
		this.polarity = polarity;
	}

	public double getPolarity() {
		return polarity;
	}

	public void setLocation(Noun location) {
		this.location = location;
	}

	public Noun getLocation() {
		return location;
	}

	public void addSubject(String id, Noun subject) {
		subjects.put(id, subject);
	}

	public Noun getSubjectsById(String id) {
		return subjects.get(id);
	}

	public void addPredicate(String id, Predicate predicate) {
		predicates.put(id, predicate);
	}

	public Predicate getPredicateById(String id) {
		return predicates.get(id);
	}

	public void addAttribute(String id, String attribute) {
		if (attributes.containsKey(id) == false) {
			attributes.put(id, new ArrayList<>());
		}
		attributes.get(id).add(attribute);
	}

	public List<String> getAttributesById(String id) {
		return attributes.get(id);
	}

	public void addReferences(String id, Noun reference) {
		if (references.containsKey(id) == false) {
			references.put(id, new ArrayList<>());
		}
		references.get(id).add(reference);
	}

	public List<Noun> getReferences(String id) {
		return references.get(id);
	}

	public void setConcept(List<String> concepts) {
		this.concepts = concepts;
	}

	public List<String> getConcepts() {
		return this.concepts;
	}

}
