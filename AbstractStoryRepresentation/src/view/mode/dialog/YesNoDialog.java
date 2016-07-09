package view.mode.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import view.utilities.AutoResizingButton;
import view.utilities.AutoResizingLabel;
import view.utilities.RoundedBorder;

public class YesNoDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JLabel okLabel;
	private JPanel panel;
	private FontMetrics fontMetrics;

	private JButton okButton;
	private JButton cancelButton;

	private boolean result = false;

	private static final Border border3 = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK, 3),
			BorderFactory.createEmptyBorder(5, 15, 5, 15));
	private static final Border border5 = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK, 5),
			BorderFactory.createEmptyBorder(3, 13, 3, 13));

	public YesNoDialog(String dialogTitle, String dialogText) {

		panel = new JPanel();
		okLabel = new AutoResizingLabel();
		okButton = new AutoResizingButton();
		cancelButton = new AutoResizingButton();

		setLayout(new BorderLayout());

		panel.setLayout(new MigLayout("fillx, center center"));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0));

		okLabel.setText(dialogText);
		okLabel.setFont(new Font("Arial", Font.BOLD, 25));

		okButton.setText("Ok");
		okButton.setFocusPainted(false);
		okButton.setBackground(Color.WHITE);
		okButton.setFont(new Font("Arial", Font.BOLD, 25));
		okButton.setBorder(border3);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				result = true;
			}
		});

		cancelButton.setText("Cancel");
		cancelButton.setFocusPainted(false);
		cancelButton.setBackground(Color.WHITE);
		cancelButton.setFont(new Font("Arial", Font.BOLD, 25));
		cancelButton.setBorder(border3);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				result = false;
			}
		});

		fontMetrics = okLabel.getFontMetrics(okLabel.getFont());

		int width = fontMetrics.stringWidth(dialogText);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new MigLayout("insets 0, fillx, center center"));
		panel2.setBackground(Color.WHITE);

		panel.add(okLabel, "h 100%, center, wrap");

		JPanel panel3 = new JPanel();
		panel3.setLayout(new MigLayout("insets 0, fillx, center center"));
		panel3.setBackground(Color.WHITE);

		panel3.add(cancelButton, "h 100%, center");
		panel3.add(okButton, "h 100%, center, wrap");

		panel2.add(panel, "h 60%, w 100%, center, gapy 0 5, wrap");
		panel2.add(panel3, "h 40%, w 100%, center, wrap");

		addUIEffects();

		add(panel2, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setPreferredSize(new Dimension(Math.max(200, width + 100), 170));
		pack();
		setTitle(dialogTitle);
		setLocationRelativeTo(null);
		setModal(true);

	}

	private void addUIEffects() {

		okButton.getModel().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					okButton.setBorder(border5);
				} else if (false == model.isRollover()) {
					okButton.setBorder(border3);
				}
			}

		});

		cancelButton.getModel().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					cancelButton.setBorder(border5);
				} else if (false == model.isRollover()) {
					cancelButton.setBorder(border3);
				}
			}

		});

	}

	public boolean showDialog() {
		setVisible(true);
		return result;
	}

}
