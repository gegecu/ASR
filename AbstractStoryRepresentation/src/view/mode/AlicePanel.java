package view.mode;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;

public class AlicePanel extends TemplatePanel {

	private static final long serialVersionUID = 1L;

	private JLabel test;
	private JPanel panel1;

	@Override
	protected void initializeUI() {

		setLayout(new MigLayout("insets 0"));
		setBackground(Color.decode("#36B214"));

		test = new JLabel(new ImageIcon("res/Alice.png"));
		test.setBackground(Color.decode("#36B214"));

		panel1 = new JPanel(new MigLayout("insets 0"));
		panel1.setBackground(Color.decode("#36B214"));
		panel1.add(test, "h 100%, w 100%, grow, wrap");

		add(panel1, "h 100%, w 100%, grow, wrap");

	}

	@Override
	protected void addUIEffects() {

	}

	@Override
	protected void addUXFeatures() {

	}

	public void setBackgroundColor(Color color) {
		setBackground(color);
		test.setBackground(color);
		panel1.setBackground(color);
	}

}
