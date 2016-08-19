package model.knowledge_base.conceptnet;

/**
 * Used to store the concept relation retrieved from the database
 */
public class Concept {

	/**
	 * The concept relation id
	 */
	private int id;
	/**
	 * The start word of the concept relation
	 */
	private String start;
	/**
	 * The part of speech of the start word of the concept relation
	 */
	private String startPOS;
	/**
	 * The relation of the start and end word of the concept relation
	 */
	private String relation;
	/**
	 * The end word of the concept relation
	 */
	private String end;
	/**
	 * The part of speech of the end word of the concept relation
	 */
	private String endPOS;

	/**
	 * @param id
	 *            the id to set
	 * @param start
	 *            the start to set
	 * @param startPOS
	 *            the startPOS to set
	 * @param relation
	 *            the relation to set
	 * @param end
	 *            the end to set
	 * @param endPOS
	 *            the endPOS to set
	 */
	public Concept(int id, String start, String startPOS, String relation,
			String end, String endPOS) {
		this.id = id;
		this.start = start;
		this.startPOS = startPOS;
		this.relation = relation;
		this.end = end;
		this.endPOS = endPOS;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the startPOS
	 */
	public String getStartPOS() {
		return startPOS;
	}

	/**
	 * @param startPOS
	 *            the startPOS to set
	 */
	public void setStartPOS(String startPOS) {
		this.startPOS = startPOS;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @param relation
	 *            the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * @return the endPOS
	 */
	public String getEndPOS() {
		return endPOS;
	}

	/**
	 * @param endPOS
	 *            the endPOS to set
	 */
	public void setEndPOS(String endPOS) {
		this.endPOS = endPOS;
	}

}
