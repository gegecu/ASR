package view.mode.beginner;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import net.miginfocom.swing.MigLayout;
import view.ViewPanel;
import view.utilities.AutoResizingButton;
import view.utilities.RoundedBorder;

public class ChecklistPanel extends ViewPanel {

	private static Logger log = Logger
			.getLogger(ChecklistPanel.class.getName());

	private static final long serialVersionUID = 1L;

	public static final String START = AbstractStoryRepresentation.start;
	public static final String MIDDLE = AbstractStoryRepresentation.middle;
	public static final String END = AbstractStoryRepresentation.end;

	private JPanel panel;

	/* start */
	private JPanel startPanel;
	private AutoResizingButton character;
	private AutoResizingButton location;
	private AutoResizingButton conflict;

	/* middle */
	private JPanel middlePanel;
	private AutoResizingButton seriesOfActions;

	/* end */
	private JPanel endPanel;
	private AutoResizingButton resolution;

	private CardLayout cardLayout;

	public ChecklistPanel(int panelWidthPercentage,
			int verticalScrollBarWidthPercentage) {
		super(panelWidthPercentage, verticalScrollBarWidthPercentage);
		initialize();
		addDescriptions();
		addUIEffects();
	}

	private void initialize() {

		panel = new JPanel();
		startPanel = new JPanel();
		character = new AutoResizingButton();
		location = new AutoResizingButton();
		conflict = new AutoResizingButton();

		middlePanel = new JPanel();
		seriesOfActions = new AutoResizingButton();

		endPanel = new JPanel();
		resolution = new AutoResizingButton();

		cardLayout = new CardLayout();
		panel.setLayout(cardLayout);

		startPanel.setLayout(new MigLayout("insets 10 0 0 0, gapy 10"));
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

		panel.add(startPanel, ChecklistPanel.START);
		panel.add(middlePanel, ChecklistPanel.MIDDLE);
		panel.add(endPanel, ChecklistPanel.END);

		scrollPanePanel.add(panel);

		start();

	}

	private void addDescriptions() {

		character.addDescription("Character",
				"The beginning of the story must have atleast 1 character.\n\nExample: There is a boy named John.");
		location.addDescription("Location",
				"The beginning of the story must have atleast 1 location.\n\nExample: John went to the park.");

	}

	@Override
	protected void addUIEffects() {
		super.addUIEffects();

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

	public void start() {
		cardLayout.show(panel, ChecklistPanel.START);
	}

	public void middle() {
		cardLayout.show(panel, ChecklistPanel.MIDDLE);
	}

	public void end() {
		cardLayout.show(panel, ChecklistPanel.END);
	}

	public void updateChecklist(AbstractStoryRepresentation asr,
			Checklist checklist) {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanel.START :
				character.setChecked(checklist.isCharacterExist());
				location.setChecked(checklist.isLocationExist());
				conflict.setChecked(checklist.isConflictExist());
				break;
			case ChecklistPanel.MIDDLE :
				log.debug(checklist.isSeriesActionExist());
				seriesOfActions.setChecked(checklist.isSeriesActionExist());
				break;
			case ChecklistPanel.END :
				resolution.setChecked(checklist.isResolutionExist());
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

}
