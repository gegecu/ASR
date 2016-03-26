package model.knowledge_base.conceptnet;

public class Concept {
	private int id;
	private String start;
	private String relation;
	private String end;
	
	public Concept(int id, String start, String relation, String end) {
		this.id = id;
		this.start = start;
		this.relation = relation;
		this.end = end;
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
