package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import model.story_representation.story_element.SpecialClause;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;

public class AbstractStoryRepresentation {

	private static Logger log = Logger
			.getLogger(AbstractStoryRepresentation.class.getName());

	private Map<String, List<StorySentence>> storySentences;

	private Map<String, Noun> nouns;

	//	private Conflict conflict;
	//
	//	private Resolution resolution;

	private SpecialClause conflict;

	private SpecialClause resolution;

	private String partOfStory;

	public static final String start = "start";
	public static final String middle = "middle";
	public static final String end = "end";

	public AbstractStoryRepresentation() {
		this.storySentences = new LinkedHashMap<String, List<StorySentence>>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.resolution = null;
		this.partOfStory = start;
	}

	public SpecialClause getConflict() {
		return this.conflict;
	}

	public SpecialClause getResolution() {
		return this.resolution;
	}

	public void addEvent(StorySentence storySentence) {

		List<StorySentence> storySentences = this.storySentences
				.get(partOfStory);

		if (storySentences == null) {
			storySentences = new ArrayList<StorySentence>();
			this.storySentences.put(partOfStory, storySentences);
		}

		storySentences.add(storySentence);

		log.debug("part of story: " + this.partOfStory);

		for (Event p : storySentence.getManyEvents().values()) {
			log.debug("doers: ");
			for (Map.Entry<String, Noun> entry : p.getManyDoers().entrySet()) {

				log.debug("doer: " + entry.getValue().getId());
				log.debug("common noun? " + entry.getValue().getIsCommon());

				log.debug("doers' attributes: ");
				for (Map.Entry<String, List<String>> entry2 : entry.getValue()
						.getAttributes().entrySet()) {
					log.debug(entry2.getKey() + " ");
					log.debug(entry2.getValue());

				}

				log.debug("doers' references: ");
				for (Map.Entry<String, Map<String, Noun>> entry2 : entry
						.getValue().getReferences().entrySet()) {
					log.debug(entry2.getKey() + " ");
					for (Noun n : entry2.getValue().values()) {
						log.debug(n.getId() + " ");
					}
				}
				
			}

			log.debug("action: " + p.getVerb().getAction());
			log.debug("isNegated: " + p.isNegated());

			log.debug("receivers: ");
			for (Map.Entry<String, Noun> entry2 : p.getReceivers().entrySet()) {
				log.debug("receiver: " + entry2.getValue().getId());
				log.debug("common noun? " + entry2.getValue().getIsCommon());

				log.debug("receiver's attributes");
				for (Map.Entry<String, List<String>> entry3 : entry2.getValue()
						.getAttributes().entrySet()) {
					log.debug(entry3.getKey() + " ");
					log.debug(entry3.getValue() + " ");

				}

				log.debug("receiver's references");
				for (Map.Entry<String, Map<String, Noun>> entry3 : entry2
						.getValue().getReferences().entrySet()) {
					log.debug(entry3.getKey() + " ");
					for (Noun n : entry3.getValue().values()) {
						log.debug(n.getId() + " ");
					}

				}
			}

			log.debug("dobjs: ");
			for (Map.Entry<String, Noun> entry3 : p.getDirectObjects()
					.entrySet()) {
				log.debug("dobject: " + entry3.getValue().getId());
				log.debug("common noun? " + entry3.getValue().getIsCommon());

				log.debug("dobj's attributes ");
				for (Map.Entry<String, List<String>> entry4 : entry3.getValue()
						.getAttributes().entrySet()) {
					log.debug(entry4.getKey() + " ");
					log.debug(entry4.getValue() + " ");

				}

				log.debug("dobj's references ");
				for (Map.Entry<String, Map<String, Noun>> entry4 : entry3
						.getValue().getReferences().entrySet()) {
					log.debug(entry4.getKey() + " ");
					for (Noun n : entry4.getValue().values()) {
						log.debug(n.getId() + " ");
					}

				}
			}
		}

		log.debug("descriptions ");

		for (Entry<String, Description> entry : storySentence
				.getManyDescriptions().entrySet()) {

			log.debug("isNegated: " + entry.getValue().isNegated());

			log.debug("doers: ");

			for (Map.Entry<String, Noun> doer : entry.getValue().getManyDoers()
					.entrySet()) {

				log.debug("doer: " + doer.getValue().getId());
				log.debug("common noun? " + doer.getValue().getIsCommon());

				log.debug("doers' attributes: ");
				for (Map.Entry<String, List<String>> entry2 : doer.getValue()
						.getAttributes().entrySet()) {
					log.debug(entry2.getKey() + " ");
					log.debug(entry2.getValue());
				}

				log.debug("doers' references: ");
				for (Map.Entry<String, Map<String, Noun>> entry2 : doer
						.getValue().getReferences().entrySet()) {
					log.debug(entry2.getKey() + " ");
					for (Noun n : entry2.getValue().values()) {
						log.debug(n.getId() + " ");
					}
				}

			}

			log.debug("attributes ");
			for (Entry<String, List<String>> entry2 : entry.getValue()
					.getAttributes().entrySet()) {
				log.debug(entry2.getKey() + " ");
				log.debug(entry2.getValue() + " ");
			}

			log.debug("references ");
			for (Entry<String, Map<String, Noun>> entry2 : entry.getValue()
					.getReferences().entrySet()) {
				log.debug(entry2.getKey() + " ");
				for (Noun n : entry2.getValue().values()) {
					log.debug(n.getId() + " ");
				}
			}

		}

		for (Event predicate : storySentence.getManyEvents().values()) {
			log.debug("p_concepts: " + predicate.getConcepts());
			//			log.debug(
			//					" polarity: " + predicate.getPolarity());
		}
		for (Description description : storySentence.getManyDescriptions()
				.values()) {
			log.debug("n_concepts: " + description.getConcepts());
			//			log.debug(
			//					" polarity: " + description.getPolarity());
		}

		if (this.getConflict() != null) {
			log.debug(this.getConflict().getMainConcept() + ", "
					+ this.getConflict().getPolarity());
		}

		if (this.getResolution() != null) {
			log.debug(this.getResolution().getMainConcept() + ", "
					+ this.getResolution().getPolarity());
		}

		log.debug("\n");

	}

	public StorySentence getCurrentStorySentence() {

		List<StorySentence> storySentences = this.storySentences
				.get(partOfStory);

		if (storySentences == null) {
			switch (partOfStory) {
				case start :
					return null;
				case middle :
					storySentences = this.storySentences.get(start);
					break;
				case end :
					storySentences = this.storySentences.get(middle);
					break;
			}
		}

		return storySentences.get(storySentences.size() - 1);

	}

	public Map<String, List<StorySentence>> getStorySentencesMap() {
		return this.storySentences;
	}

	public List<StorySentence> getStorySentencesBasedOnPart(
			String partOfStory) {
		return this.storySentences.get(partOfStory);
	}

	public List<StorySentence> getStorySentencesBasedOnCurrentPart() {
		return this.storySentences.get(this.partOfStory);
	}

	public void addNoun(String key, Noun noun) {
		this.nouns.put(key, noun);
	}

	public Noun getNoun(String key) {
		return this.nouns.get(key);
	}

	public Map<String, Noun> getNounMap() {
		return this.nouns;
	}

	public String getCurrentPartOfStory() {
		return partOfStory;
	}

	public void setPartOfStory(String partOfStory) {
		this.partOfStory = partOfStory;
	}

	public void setConflict(SpecialClause conflict) {
		this.conflict = conflict;
	}

	public void setResolution(SpecialClause resolution) {
		this.resolution = resolution;
	}

}
