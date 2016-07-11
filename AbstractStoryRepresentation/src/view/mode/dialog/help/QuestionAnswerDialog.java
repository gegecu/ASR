package view.mode.dialog.help;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import org.languagetool.gui.GrammarChecker;

import net.miginfocom.swing.MigLayout;
import view.utility.AutoResizingTextAreaWithPlaceHolder;
import view.utility.RoundedBorder;

@SuppressWarnings("serial")
public class QuestionAnswerDialog extends HelpDialog {

	private JPanel panel;
	private JButton gotItButton;
	private JButton otherSuggestionButton;
	private JButton submitButton;
	private AutoResizingTextAreaWithPlaceHolder storyInputArea;
	private JPanel storyInputPanel;
	private GrammarChecker grammarChecker;

	private boolean storyInputAreaFocused = false;

	public QuestionAnswerDialog() {
	}

	@Override
	protected void initializeUI() {

		super.initializeUI();

		panel = new JPanel();
		gotItButton = new JButton();
		otherSuggestionButton = new JButton();
		submitButton = new JButton();

		storyInputArea = new AutoResizingTextAreaWithPlaceHolder();
		storyInputPanel = new JPanel();

		grammarChecker = new GrammarChecker(storyInputArea);

		panel.setLayout(new MigLayout("insets 0, gapy 5"));
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 10, 10, 5, 10));
		panel.setBackground(Color.WHITE);

		storyViewPanel.setForeground(Color.BLACK);
		storyViewPanel.setFont(new Font("Arial", Font.BOLD, 28));
		//helpTextPane.setEditable(false);

		storyInputArea.setFont(new Font("Aria", Font.BOLD, 20));
		storyInputArea.setPlaceHolder("Enter Answer Here");

		storyInputPanel.setLayout(new MigLayout(""));
		storyInputPanel.setBackground(Color.WHITE);
		storyInputPanel.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		grammarChecker.setMillisecondDelay(300);

		submitButton.setText("Submit");
		submitButton.setFocusPainted(false);
		submitButton.setBackground(Color.decode("#36B214"));
		submitButton.setForeground(Color.BLACK);
		submitButton.setFont(new Font("Arial", Font.BOLD, 24));
		submitButton
				.setBorder(new RoundedBorder(Color.BLACK, 3, 12, 5, 10, 5, 10));
		submitButton.addActionListener((e) -> {
			// check if empty
			if (!storyInputArea.getText().isEmpty()) {
				result = HelpAnswer.ACCEPT;
				dispose();
			}
			//	else {
			//		RoundedBorder border = (RoundedBorder) storyInputPanel
			//				.getBorder();
			//		border.setColor(Color.RED);
			//		storyInputPanel.repaint();
			//	}
		});

		gotItButton.setText("Got It");
		gotItButton.setFocusPainted(false);
		gotItButton.setBackground(Color.decode("#36B214"));
		gotItButton.setForeground(Color.BLACK);
		gotItButton.setFont(new Font("Arial", Font.BOLD, 24));
		gotItButton
				.setBorder(new RoundedBorder(Color.BLACK, 3, 12, 5, 10, 5, 10));
		gotItButton.addActionListener((e) -> {
			result = HelpAnswer.CANCEL;
			dispose();
		});

		otherSuggestionButton.setText("Other Suggestion");
		otherSuggestionButton.setFocusPainted(false);
		otherSuggestionButton.setBackground(Color.decode("#36B214"));
		otherSuggestionButton.setForeground(Color.BLACK);
		otherSuggestionButton.setFont(new Font("Arial", Font.BOLD, 24));
		otherSuggestionButton
				.setBorder(new RoundedBorder(Color.BLACK, 3, 12, 5, 10, 5, 10));
		otherSuggestionButton.addActionListener((e) -> {
			result = HelpAnswer.REJECT;
			dispose();
		});

		storyInputPanel.add(storyInputArea, "h 100%, w 100%, grow");

		JPanel panel1 = new JPanel();
		panel1.setBackground(Color.WHITE);
		panel1.setLayout(new MigLayout("insets 0"));
		//panel1.setBorder(new RoundedBorder(Color.BLACK, 3, 12, 5, 5, 5, 5));
		panel1.add(storyViewPanel, "h 55%, w 100%, grow, wrap");

		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.WHITE);
		panel2.setLayout(new MigLayout("insets 0, gapy 0"));
		panel2.add(storyInputPanel, "h 100%, w 75%, grow");
		panel2.add(submitButton, "h 100%, w 25%, grow,");

		panel1.add(panel2, "h 45%, w 100%, grow, wrap");

		JPanel panel3 = new JPanel();
		panel3.setLayout(new MigLayout("insets 0"));
		panel3.setBackground(Color.WHITE);
		panel3.add(gotItButton, "h 100%, w 20%, grow");
		panel3.add(otherSuggestionButton, "h 100%, w 40%, grow, wrap");

		panel.add(panel1, "h 70%, w 100%, wrap");
		panel.add(panel3, "h 30%, w 100%, align center, wrap");

		add(panel);
		setResizable(false);
		panel.setPreferredSize(new Dimension(600, 250));
		pack();
		setTitle("Ideas");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);

	}

	@Override
	protected void addUIEffects() {

		submitButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) submitButton
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

		gotItButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) gotItButton
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

		otherSuggestionButton.getModel()
				.addChangeListener(new ChangeListener() {

					private RoundedBorder border = (RoundedBorder) otherSuggestionButton
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

		storyInputArea.addMouseListener(new MouseInputAdapter() {

			private RoundedBorder border = (RoundedBorder) storyInputPanel
					.getBorder();

			@Override
			public void mouseExited(MouseEvent e) {
				if (false == storyInputAreaFocused) {
					border.setThickness(3);
					storyInputPanel.repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (false == storyInputAreaFocused) {
					border.setThickness(5);
					storyInputPanel.repaint();
				}
			}

		});

		storyInputArea.addFocusListener(new FocusListener() {

			private RoundedBorder border = (RoundedBorder) storyInputPanel
					.getBorder();

			@Override
			public void focusLost(FocusEvent e) {
				storyInputAreaFocused = false;
				border.setThickness(3);
				storyInputPanel.repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				storyInputAreaFocused = true;
				border.setThickness(5);
				storyInputPanel.repaint();
			}

		});

	}

	@Override
	protected void addUXFeatures() {

	}

	public String getInputText() {
		return storyInputArea.getText();
	}

	public void clearInputText() {
		storyInputArea.setText("");
	}

}
