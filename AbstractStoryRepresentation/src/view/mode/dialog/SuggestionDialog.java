package view.mode.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import view.utilities.RoundedBorder;

@SuppressWarnings("serial")
public class SuggestionDialog extends HelpDialog {

	private JPanel panel;
	private JButton gotItButton;
	private JButton otherSuggestionButton;
	private JButton addToStoryButton;

	@Override
	protected void initializeUI() {

		super.initializeUI();

		panel = new JPanel();
		gotItButton = new JButton();
		otherSuggestionButton = new JButton();
		addToStoryButton = new JButton();

		panel.setLayout(new MigLayout("insets 0"));
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 10, 10, 5, 10));
		panel.setBackground(Color.WHITE);

		storyViewPanel.setForeground(Color.BLACK);
		//storyViewPanel.setFont(new Font("Arial", Font.BOLD, ));
		//helpTextPane.setEditable(false);

		gotItButton.setText("Got It");
		gotItButton.setFocusPainted(false);
		gotItButton.setBackground(Color.decode("#36B214"));
		gotItButton.setForeground(Color.BLACK);
		gotItButton.setFont(new Font("Arial", Font.BOLD, 24));
		gotItButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));//, 5, 10, 5, 10));
		gotItButton.addActionListener((e) -> {
			result = HelpAnswer.CANCEL;
			dispose();
		});

		addToStoryButton.setText("Add To Story");
		addToStoryButton.setFocusPainted(false);
		addToStoryButton.setBackground(Color.decode("#36B214"));
		addToStoryButton.setForeground(Color.BLACK);
		addToStoryButton.setFont(new Font("Arial", Font.BOLD, 24));
		addToStoryButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));//, 5, 10, 5, 10));
		addToStoryButton.addActionListener((e) -> {
			result = HelpAnswer.ACCEPT;
			dispose();
		});

		otherSuggestionButton.setText("Other Suggestion");
		otherSuggestionButton.setFocusPainted(false);
		otherSuggestionButton.setBackground(Color.decode("#36B214"));
		otherSuggestionButton.setForeground(Color.BLACK);
		otherSuggestionButton.setFont(new Font("Arial", Font.BOLD, 24));
		otherSuggestionButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));//, 5, 10, 5, 10));
		otherSuggestionButton.addActionListener((e) -> {
			result = HelpAnswer.REJECT;
			dispose();
		});

		JPanel panel1 = new JPanel();
		panel1.setBackground(Color.WHITE);
		panel1.setLayout(new MigLayout("insets 0"));
		//panel1.setBorder(new RoundedBorder(Color.BLACK, 3, 12, 5, 5, 5, 5));
		panel1.add(storyViewPanel, "w 100%, h 100%, grow");

		panel.add(panel1, "w 100%, h 65%, wrap");

		JPanel panel2 = new JPanel();
		panel2.setLayout(new MigLayout("insets 0"));
		panel2.setBackground(Color.WHITE);
		panel2.add(gotItButton, "h 100%, w 20%, grow");
		panel2.add(addToStoryButton, "h 100%, w 35%, grow");
		panel2.add(otherSuggestionButton, "h 100%, w 45%, grow, wrap");

		panel.add(panel2, "w 100%, h 35%, wrap");

		panel.setPreferredSize(new Dimension(600, 200));
		add(panel);
		setResizable(false);
		pack();
		setTitle("Suggestions");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);

	}

	@Override
	protected void addUIEffects() {

		addToStoryButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) addToStoryButton
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

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

}
