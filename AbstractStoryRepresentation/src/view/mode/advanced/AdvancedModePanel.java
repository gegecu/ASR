package view.mode.advanced;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import org.apache.log4j.Logger;

import controller.peer.AskMeController;
import controller.peer.CancelController;
import controller.peer.SaveController;
import controller.peer.SubmitController;
import controller.peer.checklist.AdvancedChecklistController;
import controller.peer.checklist.ChecklistController;
import model.story_representation.AbstractStoryRepresentation;
import model.text_generation.StorySegmentGenerator;
import model.text_generation.prompts.PromptChooser;
import model.text_understanding.TextUnderstanding;
import net.miginfocom.swing.MigLayout;
import utility.EvaluationLog;
import view.MainFrame;
import view.mode.AlicePanel;
import view.mode.ModePanel;
import view.mode.StoryInputPanel;
import view.mode.StoryViewPanel;
import view.utility.AutoResizingButton;
import view.utility.AutoResizingTextFieldWithPlaceHolder;
import view.utility.CancelButton;
import view.utility.RoundedBorder;

/**
 * @author Alice
 * @since December 23, 2015
 */
@SuppressWarnings("serial")
public class AdvancedModePanel extends ModePanel {

	private static Logger log = Logger
			.getLogger(AdvancedModePanel.class.getName());

	private JButton helpButton;
	private JButton askMeButton;

	private AutoResizingTextFieldWithPlaceHolder titleField;

	private StoryViewPanel storyViewPanel;

	private StoryInputPanel storyInputPanel;

	private boolean titleFieldFocused = false;

	private AbstractStoryRepresentation asr;
	private TextUnderstanding textUnderstanding;
	private StorySegmentGenerator storySegmentGenerator;
	private PromptChooser promptChooser;

	private AskMeController askMeController;
	private ChecklistController checklistController;
	private SubmitController submitController;

	private SaveController saveController;
	private CancelController cancelController;

	public AdvancedModePanel() {

		//asr = new AbstractStoryRepresentation();
		//textUnderstanding = new TextUnderstanding(asr);
		//storySegmentGenerator = new StorySegmentGenerator(asr);
		//promptChooser = new PromptChooser(asr);

		log.debug("========== New Advanced Story ==========");
		EvaluationLog.log("========== New Advanced Story ==========");

		saveController = new SaveController(titleField,
				storyViewPanel.getStoryViewArea());
		cancelController = new CancelController(titleField,
				storyViewPanel.getStoryViewArea());
		checklistController = new AdvancedChecklistController(asr, null,
				saveController);
		submitController = new SubmitController(asr, textUnderstanding,
				promptChooser, checklistController);
		askMeController = new AskMeController(storySegmentGenerator,
				promptChooser, submitController);

		addAskMeController(askMeController);
		addSubmitController(submitController);
		addCancelButtonActionListener(cancelController);
		addSaveButtonActionListener(saveController);

	}

	@Override
	protected void initializeUI() {

		backButton = new CancelButton();
		helpButton = new AutoResizingButton();
		saveButton = new AutoResizingButton();
		askMeButton = new AutoResizingButton();
		titleField = new AutoResizingTextFieldWithPlaceHolder();
		storyViewPanel = new StoryViewPanel(94, 6);

		backButton.setText("");
		backButton.setFocusPainted(false);
		backButton.setBackground(Color.RED);
		backButton.setForeground(Color.BLACK);
		backButton.setFont(new Font("Arial", Font.BOLD, 40));
		backButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		helpButton.setText("?");
		helpButton.setFocusPainted(false);
		helpButton.setBackground(Color.WHITE);
		helpButton.setForeground(Color.BLACK);
		helpButton.setFont(new Font("Arial", Font.BOLD, 40));
		helpButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		saveButton.setIcon(new ImageIcon("res/save.png"));
		saveButton.setFocusPainted(false);
		saveButton.setBackground(Color.decode("#36B214"));
		saveButton.setForeground(Color.BLACK);
		saveButton.setFont(new Font("Arial", Font.BOLD, 40));
		saveButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		askMeButton.setText("Ask Me");
		askMeButton.setFocusPainted(false);
		askMeButton.setBackground(Color.WHITE);
		askMeButton.setForeground(Color.BLACK);
		askMeButton.setHorizontalAlignment(SwingConstants.CENTER);
		askMeButton.setFont(new Font("Arial", Font.BOLD, 30));
		askMeButton.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 15, 10, 15, 10));

		titleField.setPlaceHolder("Enter Title Here");
		titleField.setCharacterLimit(100);
		titleField.setFont(new Font("Arial", Font.BOLD, 40));
		titleField.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 20, 10, 20, 10));
		titleField.setBackground(Color.decode("#F0F0F0"));
		titleField.setForeground(Color.BLACK);
		titleField.setHorizontalAlignment(SwingConstants.CENTER);

		storyViewPanel.setBackground(Color.decode("#36B214"));

		setLayout(new MigLayout("insets 5 10 10 10"));
		setBackground(Color.decode("#5B9CD2"));

		JPanel panel1 = new JPanel(new MigLayout());
		panel1.setBackground(Color.decode("#5B9CD2"));
		panel1.add(backButton, "h 100%, w 12%, grow");
		panel1.add(titleField, "h 100%, w 64%, grow");
		panel1.add(helpButton, "h 100%, w 12%, grow");
		panel1.add(saveButton, "h 100%, w 12%, grow");

		JPanel panel3 = new JPanel(new MigLayout("insets 0"));
		panel3.setBackground(Color.decode("#36B214"));
		panel3.add(askMeButton, "h 15%, w 100%, wrap");
		panel3.add(new AlicePanel(), "h 85%, w 100%, wrap");

		JPanel panel4 = new JPanel(new MigLayout("insets 0 10 0 10"));
		panel4.setBackground(Color.decode("#36B214"));
		panel4.add(storyViewPanel, "h 100%, w 75%");
		panel4.add(panel3, "h 100%, w 25%");

		storyInputPanel = new StoryInputPanel();

		JPanel panel6 = new JPanel(new MigLayout("insets 15 5 5 5"));
		panel6.setBackground(Color.decode("#36B214"));
		panel6.setBorder(new RoundedBorder(Color.BLACK, 3, 12));
		panel6.add(panel4, "h 80%, w 100%, wrap");
		panel6.add(storyInputPanel, "h 20%, w 100%, wrap");

		add(panel1, "h 10%, w 100%, grow, wrap");
		add(panel6, "h 90%, w 100%, grow");

	}

	@Override
	protected void addUIEffects() {

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

		helpButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) helpButton
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

		saveButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) saveButton
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

		askMeButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) askMeButton
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

		titleField.addFocusListener(new FocusListener() {

			private RoundedBorder border = (RoundedBorder) titleField
					.getBorder();

			@Override
			public void focusLost(FocusEvent e) {
				titleFieldFocused = false;
				border.setThickness(3);
				titleField.repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				titleFieldFocused = true;
				border.setThickness(5);
				titleField.repaint();
			}

		});

		titleField.addMouseListener(new MouseInputAdapter() {

			private RoundedBorder border = (RoundedBorder) titleField
					.getBorder();

			@Override
			public void mouseExited(MouseEvent e) {
				if (false == titleFieldFocused) {
					border.setThickness(3);
					titleField.repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (false == titleFieldFocused) {
					border.setThickness(5);
					titleField.repaint();
				}
			}

		});

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

	public void addAskMeController(AskMeController askMeController) {
		askMeButton.addActionListener(askMeController);
	}

	public void addSubmitController(SubmitController submitController) {
		submitController.setStoryInputPanel(storyInputPanel);
		submitController.setStoryViewPanel(storyViewPanel);
		storyInputPanel.addSubmitButtonController(submitController);
	}

	public StoryViewPanel getStoryViewPanel() {
		return storyViewPanel;
	}

	public void setMainFrame(MainFrame mainFrame) {
		cancelController.setMainFrame(mainFrame);
		saveController.setMainFrame(mainFrame);
	}

}
