package view.viewstory;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.ViewStoryController;
import model.story_database.Story;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.TemplatePanel;
import view.mode.StoryViewPanel;
import view.utility.AutoResizingButton;
import view.utility.AutoResizingTextFieldWithPlaceHolder;
import view.utility.RoundedBorder;

public class ViewStoryPanel extends TemplatePanel {

	private static final long serialVersionUID = 1L;

	public static final String cancelButtonActionCommand = "cancel";

	private JButton backButton;
	private JButton deleteButton;

	private AutoResizingTextFieldWithPlaceHolder titleField;

	private StoryViewPanel storyViewPanel;

	private ViewStoryController viewStoryController;

	@Override
	protected void initializeUI() {

		backButton = new AutoResizingButton();
		deleteButton = new AutoResizingButton();

		titleField = new AutoResizingTextFieldWithPlaceHolder();

		storyViewPanel = new StoryViewPanel(95, 5, 10, 15, 15, 15);

		setLayout(new MigLayout());
		setBackground(Color.decode("#609AD1"));

		backButton.setText("<");
		backButton.setActionCommand(cancelButtonActionCommand);
		backButton.setFocusPainted(false);
		backButton.setBackground(Color.WHITE);
		backButton.setForeground(Color.BLACK);
		backButton.setFont(new Font("Arial", Font.BOLD, 50));
		backButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		titleField.setFont(new Font("Arial", Font.BOLD, 40));
		titleField.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 20, 10, 20, 10));
		titleField.setBackground(Color.decode("#F0F0F0"));
		titleField.setForeground(Color.BLACK);
		titleField.setEditable(false);
		titleField.setHorizontalAlignment(SwingConstants.CENTER);

		deleteButton.setIcon(new ImageIcon("res/delete.png"));
		deleteButton.setFocusPainted(false);
		deleteButton.setBackground(Color.RED);
		deleteButton.setForeground(Color.BLACK);
		deleteButton.setFont(new Font("Arial", Font.BOLD, 40));
		deleteButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		storyViewPanel.setBackground(Color.decode("#36B214"));
		storyViewPanel.setFont(new Font("Arial", Font.BOLD, 36));
		storyViewPanel.setTextColor(Color.decode("#333333"));

		// top panel
		JPanel panel1 = new JPanel(new MigLayout("insets 10 10 5 10"));
		panel1.setBackground(Color.decode("#609AD1"));
		panel1.add(backButton, "w 13%, growy");
		panel1.add(titleField, "w 74%, grow");
		panel1.add(deleteButton, "w 13%, grow");

		JPanel panel3 = new JPanel(new MigLayout("insets 15 15 15 10"));
		panel3.setBackground(Color.decode("#36B214"));
		panel3.setBorder(new RoundedBorder(Color.BLACK, 3, 12));
		panel3.add(storyViewPanel, "w 100%, h 100%, wrap");

		add(panel1, "w 100%, h 10%, grow, wrap");
		add(panel3, "w 100%, h 90%, grow, wrap");

		viewStoryController = new ViewStoryController();
		backButton.addActionListener(viewStoryController);
		deleteButton.addActionListener(viewStoryController);

	}

	@Override
	protected void addUIEffects() {

		deleteButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) deleteButton
					.getBorder();

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					border.setThickness(5);
				} else if (false == model.isRollover()) {
					border.setThickness(3);
				}
			}

		});
		
		backButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) backButton
					.getBorder();

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					border.setThickness(5);
				} else if (false == model.isRollover()) {
					border.setThickness(3);
				}
			}

		});

	}

	@Override
	protected void addUXFeatures() {

	}

	public void setStory(Story story) {
		titleField.setText(story.getStoryTitle());
		storyViewPanel.setStoryText(story.getStoryBody());
		deleteButton.setActionCommand(String.valueOf(story.getStoryId()));
	}

	public void setMainFrame(MainFrame mainFrame) {
		viewStoryController.setMainFrame(mainFrame);
	}

}
