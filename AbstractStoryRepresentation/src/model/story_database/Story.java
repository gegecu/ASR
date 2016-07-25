package model.story_database;

public class Story {

	private int storyId;
	private String storyTitle;
	private String storyText;

	public Story(int storyId, String storyTitle) {
		this.storyId = storyId;
		this.storyTitle = storyTitle;
	}

	public Story(int storyId, String storyTitle, String storyText) {
		this.storyId = storyId;
		this.storyTitle = storyTitle;
		this.storyText = storyText;
	}

	public Story(String storyTitle, String storyText) {
		this.storyTitle = storyTitle;
		this.storyText = storyText;
	}

	public int getStoryId() {
		return storyId;
	}

	public void setStoryId(int storyId) {
		this.storyId = storyId;
	}

	public String getStoryTitle() {
		return storyTitle;
	}

	public void setStoryTitle(String storyTitle) {
		this.storyTitle = storyTitle;
	}

	public String getStoryText() {
		return storyText;
	}

	public void setStoryText(String storyText) {
		this.storyText = storyText;
	}

}
