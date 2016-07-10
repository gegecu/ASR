package view.mode.dialog;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import view.utility.RoundedBorder;

@SuppressWarnings("serial")
public class AskMeDialog extends JDialog {

	public static enum TypeOfHelp {
		IDEAS, SUGGESTIONS, CANCEL, QUESTION_ANSWER;
	};

	private JPanel panel;
	private JLabel introductionLabel;
	private JButton ideasButton;
	private JButton suggestionsButton;
	private JButton gotItButton;
	private TypeOfHelp result = TypeOfHelp.CANCEL;

	public AskMeDialog() {

		panel = new JPanel();
		introductionLabel = new JLabel();
		ideasButton = new JButton();
		suggestionsButton = new JButton();
		gotItButton = new JButton();

		panel.setLayout(new MigLayout("insets 0"));
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 10, 10, 5, 10));
		panel.setBackground(Color.WHITE);

		introductionLabel.setText("Hi! I am Alice. How can I help you?");
		introductionLabel.setForeground(Color.BLACK);
		introductionLabel.setFont(new Font("Arial", Font.BOLD, 28));
		introductionLabel.setHorizontalAlignment(SwingConstants.CENTER);

		gotItButton.setText("Got It");
		gotItButton.setFocusPainted(false);
		gotItButton.setBackground(Color.decode("#36B214"));
		gotItButton.setForeground(Color.BLACK);
		gotItButton.setFont(new Font("Arial", Font.BOLD, 30));
		gotItButton.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 10, 10, 10, 10));
		gotItButton.addActionListener((e) -> {
			result = TypeOfHelp.CANCEL;
			dispose();
		});

		ideasButton.setText("Ideas");
		ideasButton.setFocusPainted(false);
		ideasButton.setBackground(Color.decode("#36B214"));
		ideasButton.setForeground(Color.BLACK);
		ideasButton.setFont(new Font("Arial", Font.BOLD, 30));
		ideasButton.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 10, 10, 10, 10));
		ideasButton.addActionListener((e) -> {
			result = TypeOfHelp.QUESTION_ANSWER;
			dispose();
		});

		suggestionsButton.setText("Suggestions");
		suggestionsButton.setFocusPainted(false);
		suggestionsButton.setBackground(Color.decode("#36B214"));
		suggestionsButton.setForeground(Color.BLACK);
		suggestionsButton.setFont(new Font("Arial", Font.BOLD, 30));
		suggestionsButton.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 10, 10, 10, 10));
		suggestionsButton.addActionListener((e) -> {
			result = TypeOfHelp.SUGGESTIONS;
			dispose();
		});

		panel.add(introductionLabel, "h 50%, w 100%, align center, grow, wrap");

		JPanel panel1 = new JPanel();
		panel1.setLayout(new MigLayout("insets 0"));
		panel1.setBackground(Color.WHITE);
		panel1.add(gotItButton, "h 100%, w 33%, grow");
		panel1.add(ideasButton, "h 100%, w 32%, grow");
		panel1.add(suggestionsButton, "h 100%, w 32%, grow, wrap");

		panel.add(panel1, "h 50%, w 100%, align center, grow, wrap");

		addUIEffects();
		
		add(panel);
		pack();
		setResizable(false);
		setMinimumSize(getSize());
		setTitle("Choose Type Of Help");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);

	}

	private void addUIEffects() {

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

		ideasButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) ideasButton
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

		suggestionsButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) suggestionsButton
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

	public TypeOfHelp showDialog() {
		setVisible(true);
		dispose();
		return result;
	}

}
