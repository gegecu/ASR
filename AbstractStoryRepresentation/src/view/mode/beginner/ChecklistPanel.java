package view.mode.beginner;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;
import view.ViewPanel;
import view.utility.AutoResizingButton;
import view.utility.AutoResizingLabel;
import view.utility.RoundedBorder;

public class ChecklistPanel extends TemplatePanel {

	private static Logger log = Logger
			.getLogger(ChecklistPanel.class.getName());

	private static final long serialVersionUID = 1L;

	public static final String START = AbstractStoryRepresentation.start;
	public static final String MIDDLE = AbstractStoryRepresentation.middle;
	public static final String END = AbstractStoryRepresentation.end;

	private JPanel panel;
	private JPanel checklistCardPanel;
	private JPanel progressBarPanel;
	private JLabel guideLabel;

	/* start */
	private JPanel startPanel;
	private JPanel startProgressBarPart;
	private AutoResizingButton character;
	private AutoResizingButton location;
	private AutoResizingButton conflict;

	/* middle */
	private JPanel middlePanel;
	private JPanel middleProgressBarPart;
	private AutoResizingButton seriesOfActions;

	/* end */
	private JPanel endPanel;
	private JPanel endProgressBarPart;
	private AutoResizingButton resolution;

	private ViewPanel viewPanel;
	private CardLayout cardLayout;
	private JButton nextButton;

	private JLabel progressLabel;

	private double noOfProgress = 0;
	private double noOfItems = 5;

	public ChecklistPanel() {
		initialize();
		addDescriptions();
		addUIEffects();
	}

	private void initialize() {

		panel = new JPanel();
		checklistCardPanel = new JPanel();
		viewPanel = new ViewPanel(88, 12);
		progressBarPanel = new JPanel();

		guideLabel = new AutoResizingLabel();
		progressLabel = new AutoResizingLabel();

		startPanel = new JPanel();
		startProgressBarPart = new JPanel();
		nextButton = new AutoResizingButton();
		character = new AutoResizingButton();
		location = new AutoResizingButton();
		conflict = new AutoResizingButton();

		middlePanel = new JPanel();
		middleProgressBarPart = new JPanel();
		seriesOfActions = new AutoResizingButton();

		endPanel = new JPanel();
		endProgressBarPart = new JPanel();
		resolution = new AutoResizingButton();

		cardLayout = new CardLayout();
		checklistCardPanel.setLayout(cardLayout);

		setLayout(new BorderLayout());
		setBackground(Color.decode("#36B214"));

		panel.setLayout(new MigLayout("insets 0"));
		panel.setBackground(Color.decode("#36B214"));

		/** Checklist Related **/

		guideLabel.setText("To Do List");
		guideLabel.setForeground(Color.BLACK);
		guideLabel.setFont(new Font("Arial", Font.BOLD, 36));
		guideLabel.setHorizontalAlignment(SwingConstants.CENTER);

		viewPanel.setBackground(Color.decode("#36B214"));

		startPanel.setLayout(new MigLayout("insets 5 0 0 0, gapy 10"));
		startPanel.setBackground(Color.WHITE);

		character.setText("Character");
		character.setFont(new Font("Arial", Font.BOLD, 25));
		character.setFocusPainted(false);
		character.setBackground(Color.WHITE);
		character.setForeground(Color.BLACK);
		character.setHorizontalAlignment(SwingConstants.CENTER);
		character.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 5, 10, 5, 10));

		location.setText("Location");
		location.setFont(new Font("Arial", Font.BOLD, 25));
		location.setFocusPainted(false);
		location.setBackground(Color.WHITE);
		location.setForeground(Color.BLACK);
		location.setHorizontalAlignment(SwingConstants.CENTER);
		location.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 5, 10, 5, 10));

		conflict.setText("Conflict");
		conflict.setFont(new Font("Arial", Font.BOLD, 25));
		conflict.setFocusPainted(false);
		conflict.setBackground(Color.WHITE);
		conflict.setForeground(Color.BLACK);
		conflict.setHorizontalAlignment(SwingConstants.CENTER);
		conflict.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 5, 10, 5, 10));

		startPanel.add(character, "w 100%, grow, wrap");
		startPanel.add(location, "w 100%, grow, wrap");
		startPanel.add(conflict, "w 100%, grow, wrap");

		middlePanel.setLayout(new MigLayout("insets 10 0 0 0, gapy 10"));
		middlePanel.setBackground(Color.WHITE);

		seriesOfActions.setText("2 Events");
		seriesOfActions.setFont(new Font("Arial", Font.BOLD, 25));
		seriesOfActions.setFocusPainted(false);
		seriesOfActions.setBackground(Color.WHITE);
		seriesOfActions.setForeground(Color.BLACK);
		seriesOfActions.setHorizontalAlignment(SwingConstants.CENTER);
		seriesOfActions
				.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 5, 10, 5, 10));

		middlePanel.add(seriesOfActions, "w 100%, grow, wrap");

		endPanel.setLayout(new MigLayout("insets 10 0 0 0, gapy 10"));
		endPanel.setBackground(Color.WHITE);

		resolution.setText("Resolution");
		resolution.setFont(new Font("Arial", Font.BOLD, 22));
		resolution.setFocusPainted(false);
		resolution.setBackground(Color.WHITE);
		resolution.setForeground(Color.BLACK);
		resolution.setHorizontalAlignment(SwingConstants.CENTER);
		resolution
				.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 5, 10, 5, 10));

		endPanel.add(resolution, "w 100%, grow, wrap");

		checklistCardPanel.add(startPanel, ChecklistPanel.START);
		checklistCardPanel.add(middlePanel, ChecklistPanel.MIDDLE);
		checklistCardPanel.add(endPanel, ChecklistPanel.END);

		viewPanel.getScrollPanePanel().add(checklistCardPanel);

		nextButton.setText("Next Part");
		nextButton.setFocusPainted(false);
		nextButton.setBackground(Color.GREEN);
		nextButton.setForeground(Color.BLACK);
		nextButton.setHorizontalAlignment(SwingConstants.CENTER);
		nextButton.setFont(new Font("Arial", Font.BOLD, 25));
		nextButton.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 15, 10, 15, 10));

		/** Progress Bar Related **/

		progressLabel.setText("Progress 0%");
		progressLabel.setForeground(Color.BLACK);
		progressLabel.setFont(new Font("Arial", Font.BOLD, 25));

		progressBarPanel.setLayout(
				new MigLayout("insets 0, gapy 0, gapx 0, center center"));
		progressBarPanel.setBackground(Color.decode("#36B214"));

		startProgressBarPart.setBorder(
				BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		startProgressBarPart.setBackground(Color.WHITE);

		middleProgressBarPart.setBorder(
				BorderFactory.createMatteBorder(3, 0, 3, 3, Color.BLACK));
		middleProgressBarPart.setBackground(Color.WHITE);

		endProgressBarPart.setBorder(
				BorderFactory.createMatteBorder(3, 0, 3, 3, Color.BLACK));
		endProgressBarPart.setBackground(Color.WHITE);

		progressBarPanel.add(startProgressBarPart, "h 100%, w 31.5%, grow");
		progressBarPanel.add(middleProgressBarPart, "h 100%, w 31.5%, grow");
		progressBarPanel.add(endProgressBarPart, "h 100%, w 31.5%, grow");

		start();

		panel.add(progressLabel, "h 12%, w 100%, grow, wrap");
		panel.add(progressBarPanel, "h 14%, w 100%, center, grow, wrap");
		panel.add(guideLabel, "h 12%, w 95%, wrap");
		panel.add(viewPanel, "h 62%, w 100%, grow, wrap");
		panel.add(nextButton, "h 12%, w 100%, grow, wrap");

		add(panel, BorderLayout.CENTER);

	}

	private void addDescriptions() {

		character.addDescription("Character",
				"The beginning of the story must have at least 1 character.\n\nExample: There is a boy named John.");
		location.addDescription("Location",
				"The beginning of the story must have at least 1 location.\n\nExample: John went to the park.");

	}

	@Override
	protected void addUIEffects() {

		if (character == null)
			return;

		character.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) character
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

		location.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) location.getBorder();

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

		conflict.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) conflict.getBorder();

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

		seriesOfActions.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) seriesOfActions
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

		resolution.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) resolution
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

		nextButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) nextButton
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

	public void start() {
		cardLayout.show(checklistCardPanel, ChecklistPanel.START);
	}

	public void middle() {
		cardLayout.show(checklistCardPanel, ChecklistPanel.MIDDLE);
	}

	public void end() {
		cardLayout.show(checklistCardPanel, ChecklistPanel.END);
	}

	public void updateChecklist(AbstractStoryRepresentation asr,
			Checklist checklist) {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanel.START :
				noOfProgress = 0;
				noOfProgress = (checklist.isCharacterExist() ? 1 : 0)
						+ (checklist.isLocationExist() ? 1 : 0)
						+ (checklist.isConflictExist() ? 1 : 0);
				break;
			case ChecklistPanel.MIDDLE :
				noOfProgress = 3;
				noOfProgress += (checklist.isSeriesActionExist() ? 1 : 0);
				break;
			case ChecklistPanel.END :
				noOfProgress = 4;
				noOfProgress += (checklist.isResolutionExist() ? 1 : 0);
				break;
		}

		progressLabel.setText(String.format("Progress %.2f%%",
				(noOfProgress / noOfItems) * 100));

		switch (partOfStory) {
			case ChecklistPanel.START :
				character.setChecked(checklist.isCharacterExist());
				location.setChecked(checklist.isLocationExist());
				conflict.setChecked(checklist.isConflictExist());
				if (checklist.isBeginningComplete()) {
					startProgressBarPart.setBackground(Color.GREEN);
				}
				break;
			case ChecklistPanel.MIDDLE :
				log.debug(checklist.isSeriesActionExist());
				seriesOfActions.setChecked(checklist.isSeriesActionExist());
				if (checklist.isMiddleComplete()) {
					middleProgressBarPart.setBackground(Color.GREEN);
				}
				break;
			case ChecklistPanel.END :
				resolution.setChecked(checklist.isResolutionExist());
				if (checklist.isEndingComplete()) {
					endProgressBarPart.setBackground(Color.GREEN);
				}
				break;
		}

		repaint();

		switch (partOfStory) {
			case ChecklistPanel.START :
				log.debug("Character exist? " + character.isChecked());
				log.debug("Location exist? " + location.isChecked());
				log.debug("Conflict exist? " + conflict.isChecked());
				log.debug("Start complete? " + checklist.isBeginningComplete());
				break;
			case ChecklistPanel.MIDDLE :
				log.debug("Series event exist? " + seriesOfActions.isChecked());
				log.debug("Middle complete? " + checklist.isMiddleComplete());
				break;
			case ChecklistPanel.END :
				log.debug("Resolution exist? " + resolution.isChecked());
				log.debug("End complete? " + checklist.isEndingComplete());
				break;
		}

	}

	@Override
	protected void initializeUI() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

	public void addCheckListActionListener(ActionListener actionListener) {
		nextButton.addActionListener(actionListener);
	}

}
