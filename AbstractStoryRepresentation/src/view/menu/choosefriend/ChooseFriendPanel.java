/**
 * 
 */
package view.menu.choosefriend;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.TemplatePanel;
import view.menu.AliceHeaderPanel;
import view.utilities.AutoResizingButton;
import view.utilities.CancelButton;
import view.utilities.RoundedBorder;

/**
 * @author Alice
 * @since December 28, 2015
 */
@SuppressWarnings("serial")
public class ChooseFriendPanel extends TemplatePanel {

	private JButton cancelButton;
	private JButton helpButton;

	private AliceHeaderPanel aliceHeaderPanel;

	@Override
	protected void initializeUI() {

		cancelButton = new CancelButton();
		helpButton = new AutoResizingButton();

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

		JPanel panel1 = new JPanel(new MigLayout("insets 10 10 5 10"));
		panel1.setBackground(Color.decode("#609AD1"));
		panel1.add(cancelButton, "w 15%, growy");
		panel1.add(aliceHeaderPanel, "w 70%, growy");
		panel1.add(helpButton, "w 15%, growy, wrap");

		JPanel panel2 = new JPanel(new MigLayout("insets 0"));
		panel2.setBackground(Color.decode("#36B214"));
		panel2.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		setLayout(new MigLayout());
		setBackground(Color.decode("#609AD1"));

		add(panel1, "w 100%, h 10%, wrap");
		add(panel2, "w 100%, h 90%, center, wrap");

	}

	protected void addUIEffects() {

		cancelButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) cancelButton
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

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

	public void setMainFrame(MainFrame mainFrame) {

	}

}
