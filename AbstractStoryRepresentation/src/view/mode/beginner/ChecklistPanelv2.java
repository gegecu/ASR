package view.mode.beginner;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;
import view.utilities.AutoResizingButton;
import view.utilities.AutoResizingLabel;
import view.utilities.RoundedBorder;

public class ChecklistPanelv2 extends TemplatePanel {

	private static Logger log = Logger
			.getLogger(ChecklistPanelv2.class.getName());

	private static final long serialVersionUID = 1L;

	public static final String START = AbstractStoryRepresentation.start;
	public static final String MIDDLE = AbstractStoryRepresentation.middle;
	public static final String END = AbstractStoryRepresentation.end;

	/* main panel */
	private JPanel borderPanel;
	private JPanel insidePanel;

	/**
	 * the big panel, contains the toDoListPanel
	 */
	private JPanel toDoPanel;
	private AutoResizingLabel currentPartLabel;
	private AutoResizingLabel toDoLabel;
	/**
	 * the smaller panel, contains the list of things to do
	 */
	private JPanel toDoListPanel;
	private AutoResizingButton goToButton;

	private JPanel progressBarPanel;

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

	private CardLayout cardLayout;

	@Override
	protected void initializeUI() {

		borderPanel = new JPanel();
		insidePanel = new JPanel();

		toDoPanel = new JPanel();
		currentPartLabel = new AutoResizingLabel();
		toDoLabel = new AutoResizingLabel();
		toDoListPanel = new JPanel();
		goToButton = new AutoResizingButton();

		progressBarPanel = new JPanel();

		startPanel = new JPanel();
		startProgressBarPart = new JPanel();
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

		setLayout(new BorderLayout());

		borderPanel.setLayout(new MigLayout("nocache"));
		borderPanel
				.setBorder(new RoundedBorder(Color.decode("#D3292A"), 3, 24));
		borderPanel.setBackground(Color.decode("#F8A8AB"));

		insidePanel.setLayout(new MigLayout(""));
		insidePanel.setBackground(Color.decode("#F8A8AB"));

		toDoPanel.setLayout(new MigLayout("insets 0, gapy 0"));
		toDoPanel.setBackground(Color.decode("#F8A8AB"));

		currentPartLabel.setText("BEGINNING");
		currentPartLabel.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 25));
		currentPartLabel.setForeground(Color.decode("#3F47CC"));

		toDoLabel.setText("TO-DO LIST");
		toDoLabel.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 15));
		toDoLabel.setForeground(Color.decode("#3F47CC"));

		toDoListPanel.setLayout(cardLayout);
		toDoListPanel.setBackground(Color.decode("#F8A8AB"));

		goToButton.setText("GO TO MIDDLE");
		goToButton.setFocusPainted(false);
		goToButton.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 22));
		goToButton.setBackground(Color.decode("#ED1B24"));
		goToButton.setForeground(Color.WHITE);
		goToButton.setBorder(new RoundedBorder(Color.decode("#F21625"), 5, 65));

		startPanel.setLayout(new MigLayout("insets 0 0 0 0, gapy 5"));
		startPanel.setBackground(Color.decode("#F8A8AB"));

		character.setText("Character");
		character.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 25));
		character.setFocusPainted(false);
		character.setBackground(Color.decode("#F8A8AB"));
		character.setForeground(Color.decode("#4543C1"));
		character.setHorizontalAlignment(SwingConstants.CENTER);
		character.setBorder(new RoundedBorder(Color.WHITE, 3, 0, 5, 10, 5, 10));

		location.setText("Location");
		location.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 25));
		location.setFocusPainted(false);
		location.setBackground(Color.decode("#F8A8AB"));
		location.setForeground(Color.decode("#4543C1"));
		location.setHorizontalAlignment(SwingConstants.CENTER);
		location.setBorder(new RoundedBorder(Color.WHITE, 3, 0, 5, 10, 5, 10));

		conflict.setText("Conflict");
		conflict.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 25));
		conflict.setFocusPainted(false);
		conflict.setBackground(Color.decode("#F8A8AB"));
		conflict.setForeground(Color.decode("#4543C1"));
		conflict.setHorizontalAlignment(SwingConstants.CENTER);
		conflict.setBorder(new RoundedBorder(Color.WHITE, 3, 0, 5, 10, 5, 10));

		startPanel.add(character, "w 100%, grow, wrap");
		startPanel.add(location, "w 100%, grow, wrap");
		startPanel.add(conflict, "w 100%, grow, wrap");

		middlePanel.setLayout(new MigLayout("insets 10 0 0 0, gapy 10"));
		middlePanel.setBackground(Color.decode("#F8A8AB"));

		seriesOfActions.setText("2 Events");
		seriesOfActions.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 25));
		seriesOfActions.setFocusPainted(false);
		seriesOfActions.setBackground(Color.decode("#F8A8AB"));
		seriesOfActions.setForeground(Color.decode("#4543C1"));
		seriesOfActions.setHorizontalAlignment(SwingConstants.CENTER);
		seriesOfActions
				.setBorder(new RoundedBorder(Color.WHITE, 3, 0, 5, 10, 5, 10));

		middlePanel.add(seriesOfActions, "w 100%, grow, wrap");

		endPanel.setLayout(new MigLayout("insets 10 0 0 0, gapy 10"));
		endPanel.setBackground(Color.decode("#F8A8AB"));

		resolution.setText("Resolution");
		resolution.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 22));
		resolution.setFocusPainted(false);
		resolution.setBackground(Color.decode("#F8A8AB"));
		resolution.setForeground(Color.decode("#4543C1"));
		resolution.setHorizontalAlignment(SwingConstants.CENTER);
		resolution
				.setBorder(new RoundedBorder(Color.WHITE, 3, 0, 5, 10, 5, 10));

		endPanel.add(resolution, "w 100%, grow, wrap");

		toDoListPanel.add(startPanel, ChecklistPanelv2.START);
		toDoListPanel.add(middlePanel, ChecklistPanelv2.MIDDLE);
		toDoListPanel.add(endPanel, ChecklistPanelv2.END);

		toDoPanel.add(currentPartLabel, "wrap");
		toDoPanel.add(toDoLabel, "w 100%, growx, wrap");
		toDoPanel.add(toDoListPanel, "w 100%, growx, wrap");
		toDoPanel.add(goToButton, "h 100%, w 100%, grow, wrap");

		progressBarPanel.setLayout(new MigLayout("insets 0, gapy 0"));

		startProgressBarPart.setBorder(
				BorderFactory.createMatteBorder(3, 3, 3, 3, Color.WHITE));
		startProgressBarPart.setBackground(Color.decode("#3F48CB"));

		middleProgressBarPart.setBorder(
				BorderFactory.createMatteBorder(0, 3, 3, 3, Color.WHITE));
		middleProgressBarPart.setBackground(Color.decode("#F8A8AB"));

		endProgressBarPart.setBorder(
				BorderFactory.createMatteBorder(0, 3, 3, 3, Color.WHITE));
		endProgressBarPart.setBackground(Color.decode("#F8A8AB"));

		progressBarPanel.add(startProgressBarPart,
				"h 33.33%, w 100%, grow, wrap");
		progressBarPanel.add(middleProgressBarPart,
				"h 33.33%, w 100%, grow, wrap");
		progressBarPanel.add(endProgressBarPart,
				"h 33.33%, w 100%, grow, wrap");

		insidePanel.add(progressBarPanel, "h 100%, w 15%, grow, gapx 0 3%");
		insidePanel.add(toDoPanel, "h 60%, w 82%, grow");

		borderPanel.add(insidePanel, "h 100%, w 100%, grow");

		add(borderPanel, BorderLayout.CENTER);

		addDescriptions();
		start();

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

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

	private void addDescriptions() {

		character.addDescription("Character",
				"The beginning of the story must have atleast 1 character.\n\nExample: There is a boy named John.");
		location.addDescription("Location",
				"The beginning of the story must have atleast 1 location.\n\nExample: John went to the park.");

	}

	public void start() {
		cardLayout.show(toDoListPanel, ChecklistPanelv2.START);
	}

	public void middle() {
		cardLayout.show(toDoListPanel, ChecklistPanelv2.MIDDLE);
	}

	public void end() {
		cardLayout.show(toDoListPanel, ChecklistPanelv2.END);
	}

	public void updateChecklist(AbstractStoryRepresentation asr,
			Checklist checklist) {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanelv2.START :
				character.setChecked(checklist.isCharacterExist());
				location.setChecked(checklist.isLocationExist());
				conflict.setChecked(checklist.isConflictExist());
				break;
			case ChecklistPanelv2.MIDDLE :
				log.debug(checklist.isSeriesActionExist());
				seriesOfActions.setChecked(checklist.isSeriesActionExist());
				break;
			case ChecklistPanelv2.END :
				resolution.setChecked(checklist.isResolutionExist());
				break;
		}

		repaint();

		switch (partOfStory) {
			case ChecklistPanelv2.START :
				log.debug("Character exist? " + character.isChecked());
				log.debug("Location exist? " + location.isChecked());
				log.debug("Conflict exist? " + conflict.isChecked());
				log.debug("Start complete? " + checklist.isBeginningComplete());
				break;
			case ChecklistPanelv2.MIDDLE :
				log.debug("Series event exist? " + seriesOfActions.isChecked());
				log.debug("Middle complete? " + checklist.isMiddleComplete());
				break;
			case ChecklistPanelv2.END :
				log.debug("Resolution exist? " + resolution.isChecked());
				log.debug("End complete? " + checklist.isEndingComplete());
				break;
		}

	}

}
