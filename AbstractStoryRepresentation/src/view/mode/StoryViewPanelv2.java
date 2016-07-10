/**
 * 
 */
package view.mode;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;

import view.ViewPanelv2;
import view.utility.AutoResizingTextAreaWithPlaceHolder;

/**
 * @author Alice
 * @since December 31, 2015
 */
@SuppressWarnings("serial")
public class StoryViewPanelv2 extends ViewPanelv2 {

	private JTextArea storyViewArea;

	public StoryViewPanelv2() {
		super(100, 0);
	}

	@Override
	protected void initializeUI() {

		super.initializeUI();

		storyViewArea = new AutoResizingTextAreaWithPlaceHolder();

		storyViewArea.setEditable(false);
		storyViewArea.setForeground(Color.BLACK);
		storyViewArea.setBorder(null);

		storyViewArea.setLineWrap(true);
		storyViewArea.setWrapStyleWord(true);
		storyViewArea.setFont(new Font("Arial", Font.BOLD, 25));

		// border layout because miglayout doesn't resize
		scrollPane.setViewportView(storyViewArea);

		storyViewArea.setText("The quick brown fox jumped over the lazy dog.");

	}

	public void appendStory(String story) {
		//storyViewArea.append(story);
		String text = storyViewArea.getText();
		if (text.isEmpty()) {
			storyViewArea.setText(story);
		} else {
			storyViewArea
					.setText(text + (text.endsWith(".") ? " " : ". ") + story);
		}
	}

	public void setStory(String story) {
		storyViewArea.setText(story);
	}

	public void setText(String text) {
		setStory(text);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (storyViewArea != null)
			storyViewArea.setFont(font);
	}

	public String getStory() {
		return storyViewArea.getText();
	}

	public void setTextColor(Color fg) {
		storyViewArea.setForeground(fg);
	}

}
