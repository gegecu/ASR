package view.mode.dialog.help;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import view.utility.RoundedBorder;

@SuppressWarnings("serial")
public class NoIdeaDialog extends HelpDialog {

	private JPanel panel;
	private JButton gotItButton;

	public NoIdeaDialog() {
	}

	@Override
	protected void initializeUI() {

		super.initializeUI();

		panel = new JPanel();
		gotItButton = new JButton();

		panel.setLayout(new MigLayout("insets 0"));
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0, 10, 10, 5, 10));
		panel.setBackground(Color.WHITE);

		storyViewPanel.setForeground(Color.BLACK);
		storyViewPanel.setFont(new Font("Arial", Font.BOLD, 28));

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

		JPanel panel1 = new JPanel();
		panel1.setBackground(Color.WHITE);
		panel1.setLayout(new MigLayout("insets 0"));
		panel1.add(storyViewPanel, "w 100%, h 100%, grow");

		panel.add(panel1, "w 100%, h 65%, wrap");

		JPanel panel2 = new JPanel();
		panel2.setLayout(new MigLayout("insets 0"));
		panel2.setBackground(Color.WHITE);
		panel2.add(gotItButton, "h 100%, w 20%, grow");

		panel.add(panel2, "w 100%, h 35%, align center, wrap");

		add(panel);
		setResizable(false);
		panel.setPreferredSize(new Dimension(600, 200));
		pack();
		setTitle("Ideas");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);

	}

	@Override
	protected void addUIEffects() {

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

	}

	@Override
	protected void addUXFeatures() {

	}

}
