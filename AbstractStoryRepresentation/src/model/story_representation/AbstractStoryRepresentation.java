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

/**
 * Used to store the extracted story elements
 */
public class AbstractStoryRepresentation {

	private static Logger log = Logger
			.getLogger(AbstractStoryRepresentation.class.getName());

	/**
	 * List of story sentences mapped by the part of story
	 */
	private Map<String, List<StorySentence>> storySentencesMap;
	/**
	 * Map of nouns, having the noun’s position id as the key. <br>
	 * <br>
	 * Position id is from Stanford’s CoreNLP coreference tool.
	 */
	private Map<String, Noun> nounsMap;
	/**
	 * Stores the probable conflict of the story
	 */
	private SpecialClause conflict;
	/**
	 * Stores the resolution of the story for the conflict
	 */
	private SpecialClause resolution;
	/**
	 * Stores the current part of the story
	 */
	private String partOfStory;

	/**
	 * "start" string constant
	 */
	public static final String start = "start";
	/**
	 * "middle" string constant
	 */
	public static final String middle = "middle";
	/**
	 * "end" string constant
	 */
	public static final String end = "end";

	/**
	 * intializes the variables
	 */
	public AbstractStoryRepresentation() {
		this.storySentencesMap = new LinkedHashMap<String, List<StorySentence>>();
		this.nounsMap = new HashMap<String, Noun>();
		this.conflict = null;
		this.resolution = null;
		this.partOfStory = start;
	}

	/**
	 * @return the conflict
	 */
	public SpecialClause getConflict() {
		return this.conflict;
	}

	/**
	 * @return the resolution
	 */
	public SpecialClause getResolution() {
		return this.resolution;
	}

	/**
	 * Adds the story sentence to the list of story sentences mapped by the part
	 * of story, using the current part of story.
	 * 
	 * @param storySentence
	 *            story sentence to add
	 */
	public void addStorySentence(StorySentence storySentence) {

		List<StorySentence> storySentences = this.storySentencesMap
				.get(partOfStory);

		if (storySentences == null) {
			storySentences = new ArrayList<StorySentence>();
			this.storySentencesMap.put(partOfStory, storySentences);
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

		for (Event event : storySentence.getManyEvents().values()) {
			log.debug("p_concepts: " + event.getConcepts());
		}
		for (Description description : storySentence.getManyDescriptions()
				.values()) {
			log.debug("n_concepts: " + description.getConcepts());
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

	/**
	 * @return latest story sentence added
	 */
	public StorySentence getCurrentStorySentence() {

		List<StorySentence> storySentences = this.storySentencesMap
				.get(partOfStory);

		if (storySentences == null) {
			switch (partOfStory) {
				case start :
					return null;
				case middle :
					storySentences = this.storySentencesMap.get(start);
					break;
				case end :
					storySentences = this.storySentencesMap.get(middle);
					break;
			}
		}

		return storySentences.get(storySentences.size() - 1);

	}

	/**
	 * @return the storySentencesMap
	 */
	public Map<String, List<StorySentence>> getStorySentencesMap() {
		return this.storySentencesMap;
	}

	/**
	 * Returns list of story sentences filtered by part of story paramm
	 * 
	 * @param partOfStory
	 *            the partOfStory filter
	 * @return list of story sentences
	 */
	public List<StorySentence> getStorySentencesBasedOnPart(
			String partOfStory) {
		return this.storySentencesMap.get(partOfStory);
	}

	/**
	 * Returns list of story sentences filtered by the current part of story
	 * 
	 * @return list of story sentences
	 */
	public List<StorySentence> getStorySentencesBasedOnCurrentPart() {
		return this.storySentencesMap.get(this.partOfStory);
	}

	/**
	 * Sets the noun to the nounsMap using the key param as key
	 * 
	 * @param key
	 *            the key to set
	 * @param noun
	 *            the noun to set
	 */
	public void addNoun(String key, Noun noun) {
		this.nounsMap.put(key, noun);
	}

	/**
	 * Returns the noun from the nounsMap having the key param as the key
	 * 
	 * @param key
	 *            the key
	 * @return the noun
	 */
	public Noun getNoun(String key) {
		return this.nounsMap.get(key);
	}

	/**
	 * @return the nounsMap
	 */
	public Map<String, Noun> getNounsMap() {
		return this.nounsMap;
	}

	/**
	 * @return the partOfStory
	 */
	public String getCurrentPartOfStory() {
		return partOfStory;
	}

	/**
	 * @param partOfStory
	 *            the partOfStory to set
	 */
	public void setPartOfStory(String partOfStory) {
		this.partOfStory = partOfStory;
	}

	/**
	 * @param conflict
	 *            the conflict to set
	 */
	public void setConflict(SpecialClause conflict) {
		this.conflict = conflict;
	}

	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(SpecialClause resolution) {
		this.resolution = resolution;
	}

}
