/**
 * 
 */
package view.mode.beginner;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import org.apache.log4j.Logger;

import controller.peer.AskMeController;
import controller.peer.CancelController;
import controller.peer.ChecklistController;
import controller.peer.SaveController;
import controller.peer.SubmitController;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import model.text_generation.StorySegmentGenerator;
import model.text_understanding.TextUnderstanding;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.mode.AlicePanelv2;
import view.mode.ModePanel;
import view.mode.StoryInputPanelv2;
import view.mode.StoryViewPanelv2;
import view.utilities.AutoResizingButton;
import view.utilities.AutoResizingLabel;
import view.utilities.AutoResizingTextFieldWithPlaceHolder;
import view.utilities.RoundedBorder;

/**
 * @author Alice
 * @since January 1, 2016
 */
@SuppressWarnings("serial")
public class BeginnerModePanelv2 extends ModePanel {

	private static Logger log = Logger
			.getLogger(BeginnerModePanelv2.class.getName());

	private JButton helpButton;
	private JButton askMeButton;
	private JButton nextButton;

	private JPanel storyViewLabelPanel;
	private AutoResizingLabel storyViewLabel;
	private StoryViewPanelv2 storyViewPanel;

	private JPanel titlePanel;
	private AutoResizingLabel titleLabel;
	private AutoResizingTextFieldWithPlaceHolder titleField;

	private AlicePanelv2 alicePanel;

	private JLabel guideLabel;
	private ChecklistPanelv2 checkListPanel;

	private StoryInputPanelv2 storyInputPanel;

	private boolean titleFieldFocused = false;

	private AbstractStoryRepresentation asr;
	private Checklist cl;
	private TextUnderstanding tu;
	private StorySegmentGenerator ssg;

	private AskMeController askMeController;
	private ChecklistController checklistController;
	private SubmitController submitController;
	private SaveController saveController;
	private CancelController cancelController;

	public BeginnerModePanelv2() {

		//		asr = new AbstractStoryRepresentation();
		//		cl = new Checklist(asr);
		//		tu = new TextUnderstanding(asr);
		//		dg = new DirectivesGenerator(asr);
		//		ssg = new StorySegmentGenerator(asr);
		//
		//		log.debug("========== New Story ==========");
		//
		//		checklistController = new ChecklistController(asr, cl, checkListPanel);
		//		submitController = new SubmitController(asr, tu, checklistController);
		//		askMeController = new AskMeController(dg, ssg, submitController);
		saveController = new SaveController(titleField,
				storyInputPanel.getInputArea());
		cancelController = new CancelController();
		//
		//		addAskMeActionListener(askMeController);
		//		addSubmitActionListener(submitController);
		//		addCheckListActionListener(checklistController);
		addSaveButtonActionListener(saveController);
		addCancelButtonActionListener(cancelController);

	}

	@Override
	public void reinitialize() {
		// TODO Auto-generated method stub
		super.reinitialize();
	}

	@Override
	protected void initializeUI() {

		backButton = new AutoResizingButton();
		helpButton = new AutoResizingButton();
		saveButton = new AutoResizingButton();
		askMeButton = new AutoResizingButton();
		nextButton = new AutoResizingButton();
		titlePanel = new JPanel();
		titleLabel = new AutoResizingLabel();
		titleField = new AutoResizingTextFieldWithPlaceHolder();
		storyViewLabelPanel = new JPanel();
		storyViewLabel = new AutoResizingLabel();
		storyViewPanel = new StoryViewPanelv2();
		guideLabel = new AutoResizingLabel();
		checkListPanel = new ChecklistPanelv2();
		alicePanel = new AlicePanelv2();

		backButton.setText("<");
		backButton.setFocusPainted(false);
		backButton.setBackground(Color.decode("#DCB3DD"));
		backButton.setForeground(Color.decode("#9D489C"));
		backButton.setFont(new Font("Arial", Font.PLAIN, 60));
		backButton.setBorder(
				new RoundedBorder(Color.decode("#9E4D9E"), 3, 12, 0, 0, 0, 0));

		helpButton.setText("?");
		helpButton.setFocusPainted(false);
		helpButton.setBackground(Color.decode("#F8A8AB"));
		helpButton.setForeground(Color.decode("#E4211B"));
		helpButton.setFont(new Font("Arial", Font.PLAIN, 60));
		helpButton.setBorder(
				new RoundedBorder(Color.decode("#E4211B"), 3, 12, 0, 0, 0, 0));

		saveButton.setIcon(new ImageIcon("resv2/save.png"));
		saveButton.setFocusPainted(false);
		saveButton.setBackground(Color.decode("#B5E61B"));
		saveButton.setForeground(Color.decode("#28B149"));
		saveButton.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 60));
		saveButton.setBorder(
				new RoundedBorder(Color.decode("#28B149"), 3, 12, 5, 0, 5, 0));

		askMeButton.setText("NO MORE IDEAS?");
		askMeButton.setFocusPainted(false);
		askMeButton.setBackground(Color.decode("#25B14F"));
		askMeButton.setForeground(Color.WHITE);
		askMeButton.setHorizontalAlignment(SwingConstants.CENTER);
		askMeButton.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 15));
		askMeButton.setBorder(new RoundedBorder(Color.decode("#1EB14B"), 3, 12,
				15, 10, 15, 10));

		alicePanel.setBackgroundColor(Color.WHITE);

		guideLabel.setText("To Do List");
		guideLabel.setForeground(Color.BLACK);
		guideLabel.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 36));
		guideLabel.setHorizontalAlignment(SwingConstants.CENTER);

		checkListPanel.setBackground(Color.WHITE);

		nextButton.setText("Next Part");
		nextButton.setFocusPainted(false);
		nextButton.setBackground(Color.GREEN);
		nextButton.setForeground(Color.BLACK);
		nextButton.setHorizontalAlignment(SwingConstants.CENTER);
		nextButton.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 25));
		nextButton.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 15, 10, 15, 10));

		titleLabel.setText("TITLE");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 30));
		titleLabel.setForeground(Color.decode("#7E7E77"));

		titleField.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 40));
		titleField.setBorder(new RoundedBorder(Color.decode("#7E7C86"), 3, 12,
				11, 10, 12, 10));
		titleField.setBackground(Color.decode("#F0F0F0"));
		titleField.setForeground(Color.BLACK);
		titleField.setHorizontalAlignment(SwingConstants.CENTER);

		titlePanel.setLayout(new MigLayout("insets 0"));
		titlePanel.setBackground(Color.decode("#FFD237"));
		titlePanel.add(titleLabel, "h 10%, w 100%, growx, bottom, wrap");
		titlePanel.add(titleField, "h 90%, w 100%, growx, bottom");

		storyViewLabelPanel.setLayout(new MigLayout("insets 0"));
		storyViewLabelPanel.setBackground(Color.WHITE);

		storyViewLabel.setText("YOUR STORY :");
		storyViewLabel.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 20));
		storyViewLabel.setForeground(Color.decode("#26AE51"));

		storyViewPanel.setBackground(Color.WHITE);

		storyViewLabelPanel.add(storyViewLabel, "h 5%, w 100%, grow, wrap");
		storyViewLabelPanel.add(storyViewPanel, "h 95%, w 100%, grow");

		setLayout(new MigLayout("insets 0, gapy 0"));
		setBackground(Color.decode("#FFD237"));

		JPanel panel1 = new JPanel(new MigLayout(""));
		panel1.setBackground(Color.decode("#FFCF3D"));
		panel1.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0,
				Color.decode("#8B7F60")));
		panel1.add(backButton, "h 40%, w 9%, bottom, gapx 1%");
		panel1.add(helpButton, "h 40%, w 9%, bottom");
		panel1.add(titlePanel, "h 100%, w 70%, grow");
		panel1.add(saveButton, "h 40%, w 9%, bottom, gapx 0% 1%");

		JPanel panel2 = new JPanel(new MigLayout("insets 0, gapy 0"));
		panel2.setBackground(Color.WHITE);
		//panel2.add(guideLabel, "h 15%, w 95%, wrap");
		panel2.add(checkListPanel, "h 100%, w 100%, wrap");
		//panel2.add(nextButton, "h 15%, w 100%, wrap");

		JPanel panel3 = new JPanel(
				new MigLayout("insets 0, fillx, al center center, gapy 0"));
		panel3.setBackground(Color.WHITE);
		panel3.add(askMeButton, "h 15%, w 70%, center, wrap");
		panel3.add(alicePanel, "h 85%, w 100%, grow, wrap");

		JPanel panel4 = new JPanel(new MigLayout("insets 0"));
		panel4.setBackground(Color.WHITE);
		panel4.add(storyViewLabelPanel, "h 100%, w 55%, gapx 1% 1%");
		panel4.add(panel2, "h 91%, w 25%, bottom");
		panel4.add(panel3, "h 91%, w 18%, bottom");

		storyInputPanel = new StoryInputPanelv2();
		storyInputPanel.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0,
				Color.decode("#8B7F60")));

		JPanel panel6 = new JPanel(new MigLayout("insets 10 10 10 10"));
		panel6.setBackground(Color.WHITE);
		panel6.add(panel4, "h 100%, w 100%, grow, wrap");

		add(panel1, "h 10%, w 100%, grow, wrap");
		add(panel6, "h 60%, w 100%, grow, wrap");
		add(storyInputPanel, "h 30%, w 100%, grow, wrap");

		addCancelButtonActionListener(cancelController);
		addSaveButtonActionListener(saveController);

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

	public void addAskMeActionListener(AskMeController askMeController) {
		askMeButton.addActionListener(askMeController);
	}

	public void addSubmitActionListener(SubmitController submitController) {
		//submitController.setStoryInputPanel(storyInputPanel);
		//submitController.setStoryViewPanel(storyViewPanel);
		storyInputPanel.addSubmitButtonController(submitController);
	}

	public StoryViewPanelv2 getStoryViewPanel() {
		return storyViewPanel;
	}

	public ChecklistPanelv2 getCheckListPanel() {
		return checkListPanel;
	}

	public void addCheckListActionListener(
			ChecklistController checklistController) {
		nextButton.addActionListener(checklistController);
	}

	public void setMainFrame(MainFrame mainFrame) {
		cancelController.setMainFrame(mainFrame);
		saveController.setMainFrame(mainFrame);
	}

}
