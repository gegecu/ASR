package model.story_database;

/**
 * Used to store story data
 */
public class Story {

	/**
	 * the id of the story
	 */
	private int storyId;
	/**
	 * the title of the story
	 */
	private String storyTitle;
	/**
	 * the body of the story
	 */
	private String storyBody;

	/**
	 * @param storyId
	 *            the story id to set
	 * @param storyTitle
	 *            the story title to set
	 */
	public Story(int storyId, String storyTitle) {
		this.storyId = storyId;
		this.storyTitle = storyTitle;
	}

	/**
	 * @param storyId
	 *            the story id to set
	 * @param storyTitle
	 *            the story title to set
	 * @param storyBody
	 *            the story body to set
	 */
	public Story(int storyId, String storyTitle, String storyBody) {
		this.storyId = storyId;
		this.storyTitle = storyTitle;
		this.storyBody = storyBody;
	}

	/**
	 * @param storyTitle
	 *            the story title to set
	 * @param storyBody
	 *            the story body to set
	 */
	public Story(String storyTitle, String storyBody) {
		this.storyTitle = storyTitle;
		this.storyBody = storyBody;
	}

	/**
	 * @return the storyTitle
	 */
	public String getStoryTitle() {
		return storyTitle;
	}

	/**
	 * @param storyTitle
	 *            the storyTitle to set
	 */
	public void setStoryTitle(String storyTitle) {
		this.storyTitle = storyTitle;
	}

	/**
	 * @return the storyBody
	 */
	public String getStoryBody() {
		return storyBody;
	}

	/**
	 * @param storyBody
	 *            the storyBody to set
	 */
	public void setStoryBody(String storyBody) {
		this.storyBody = storyBody;
	}

	/**
	 * @return the storyId
	 */
	public int getStoryId() {
		return storyId;
	}

}
