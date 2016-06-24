/**
 * 
 */
package view.mode;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;

import view.ViewPanel;
import view.utilities.AutoResizingTextAreaWithPlaceHolder;

/**
 * @author Alice
 * @since December 31, 2015
 */
@SuppressWarnings("serial")
public class StoryViewPanel extends ViewPanel {

	private JTextArea storyViewArea;

	public StoryViewPanel(int panelWidthPercentage,
			int verticalScrollBarWidthPercentage) {
		super(panelWidthPercentage, verticalScrollBarWidthPercentage);
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

}