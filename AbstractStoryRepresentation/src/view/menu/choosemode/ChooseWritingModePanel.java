/**
 * 
 */
package view.menu.choosemode;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;
import view.menu.AliceHeaderPanel;
import view.utilities.AutoResizingButton;
import view.utilities.AutoResizingLabel;
import view.utilities.CancelButton;
import view.utilities.RoundedBorder;

/**
 * @author Alice
 * @since December 28, 2015
 */
@SuppressWarnings("serial")
public class ChooseWritingModePanel extends TemplatePanel {

	private JButton cancelButton;
	private JButton helpButton;
	private JButton advancedModeButton;
	private JButton beginnerModeButton;
	private JLabel chooseWritingModeLabel;

	private AliceHeaderPanel aliceHeaderPanel;

	@Override
	protected void initializeUI() {

		cancelButton = new CancelButton();
		helpButton = new AutoResizingButton();
		advancedModeButton = new AutoResizingButton();
		beginnerModeButton = new AutoResizingButton();
		chooseWritingModeLabel = new AutoResizingLabel();

		aliceHeaderPanel = new AliceHeaderPanel();

		cancelButton.setFocusPainted(false);
		cancelButton.setBackground(Color.RED);
		cancelButton.setForeground(Color.BLACK);
		cancelButton.setFont(new Font("Arial", Font.BOLD, 40));
		cancelButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		helpButton.setText("?");
		helpButton.setFocusPainted(false);
		helpButton.setBackground(Color.WHITE);
		helpButton.setForeground(Color.BLACK);
		helpButton.setFont(new Font("Arial", Font.BOLD, 40));
		helpButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		advancedModeButton.setText("Advanced");
		advancedModeButton.setFocusPainted(false);
		advancedModeButton.setBackground(Color.WHITE);
		advancedModeButton.setForeground(Color.BLACK);
		advancedModeButton.setHorizontalAlignment(SwingConstants.CENTER);
		advancedModeButton.setFont(new Font("Arial", Font.BOLD, 40));
		advancedModeButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12, 20, 20, 20, 20));

		beginnerModeButton.setText("Beginner");
		beginnerModeButton.setFocusPainted(false);
		beginnerModeButton.setBackground(Color.WHITE);
		beginnerModeButton.setForeground(Color.BLACK);
		beginnerModeButton.setHorizontalAlignment(SwingConstants.CENTER);
		beginnerModeButton.setFont(new Font("Arial", Font.BOLD, 40));
		beginnerModeButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12, 20, 20, 20, 20));

		chooseWritingModeLabel.setText("Choose Writing Mode.");
		chooseWritingModeLabel.setForeground(Color.BLACK);
		chooseWritingModeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		chooseWritingModeLabel.setFont(new Font("Arial", Font.BOLD, 50));

		JPanel panel1 = new JPanel(new MigLayout("insets 10 10 5 10"));
		panel1.setBackground(Color.decode("#609AD1"));
		panel1.add(cancelButton, "w 15%, growy");
		panel1.add(aliceHeaderPanel, "w 70%, growy");
		panel1.add(helpButton, "w 15%, growy, wrap");

		JPanel panel2 = new JPanel(new MigLayout("center, insets 10 0 0 0", "[]20[]", ""));
		panel2.setBackground(Color.decode("#36B214"));
		panel2.add(beginnerModeButton, "h 100%, w 25%, growy");
		panel2.add(advancedModeButton, "h 100%, w 25%, growy");

		JPanel panel3 = new JPanel(new MigLayout("al center center"));
		panel3.setBackground(Color.decode("#36B214"));
		panel3.setBorder(new RoundedBorder(Color.BLACK, 3, 12));
		panel3.add(chooseWritingModeLabel, "w 100%, wrap");
		panel3.add(panel2, "center, h 15%, w 100%, wrap");

		setLayout(new MigLayout());
		setBackground(Color.decode("#609AD1"));

		add(panel1, "w 100%, h 10%, wrap");
		add(panel3, "w 100%, h 90%, center, growy, wrap");

	}

	@Override
	protected void addUIEffects() {

		cancelButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) cancelButton.getBorder();

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

			private RoundedBorder border = (RoundedBorder) helpButton.getBorder();

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

		advancedModeButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) advancedModeButton.getBorder();

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

		beginnerModeButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) beginnerModeButton.getBorder();

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
