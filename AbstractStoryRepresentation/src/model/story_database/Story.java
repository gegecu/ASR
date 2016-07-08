package model.story_database;

public class Story implements Comparable<Story> {

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

	public int getStoryId() {
		return storyId;
	}

	public String getStoryTitle() {
		return storyTitle;
	}

	public String getStoryText() {
		return storyText;
	}

	public void setStoryText(String storyText) {
		this.storyText = storyText;
	}

	@Override
	public int compareTo(Story s) {
		return storyId - s.storyId;
	}

}
