package model.knowledge_base.conceptnet;

public class Concept {

	private int id;
	private String start;
	private String startPOS;
	private String relation;
	private String end;
	private String endPOS;

	public Concept(int id, String start, String startPOS, String relation,
			String end, String endPOS) {
		this.id = id;
		this.start = start;
		this.startPOS = startPOS;
		this.relation = relation;
		this.end = end;
		this.endPOS = endPOS;
	}

	public String getStartPOS() {
		return startPOS;
	}

	public void setStartPOS(String startPOS) {
		this.startPOS = startPOS;
	}

	public String getEndPOS() {
		return endPOS;
	}

	public void setEndPOS(String endPOS) {
		this.endPOS = endPOS;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

}
