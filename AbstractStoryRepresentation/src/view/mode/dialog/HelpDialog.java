package view.mode.dialog;

import javax.swing.JDialog;

import view.mode.StoryViewPanel;

@SuppressWarnings("serial")
public abstract class HelpDialog extends JDialog {

	public static enum HelpAnswer {
		ACCEPT, REJECT, CANCEL;
	};

	protected String helpText;
	protected StoryViewPanel storyViewPanel;
	protected HelpAnswer result = HelpAnswer.CANCEL;

	public HelpDialog() {
		initializeUI();
		addUIEffects();
		addUXFeatures();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}

	protected void initializeUI() {
		storyViewPanel = new StoryViewPanel(95, 5);
	}

	protected abstract void addUIEffects();

	protected abstract void addUXFeatures();

	public final void setHelpText(String helpText) {
		storyViewPanel.setStory(helpText);
	}

	public final HelpAnswer showDialog() {
		setVisible(true);
		dispose();
		return result;
	}

	public final String getHelpText() {
		return helpText;
	}

}
