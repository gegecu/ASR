/**
 * 
 */
package view.menu;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;
import view.utility.AutoResizingLabel;
import view.utility.RoundedBorder;

/**
 * @author Alice
 * @since December 28, 2015
 */
@SuppressWarnings("serial")
public class AliceHeaderPanel extends TemplatePanel {

	private JLabel titleLabel;
	private JLabel titleDescriptionLabel;

	@Override
	protected void initializeUI() {

		titleLabel = new AutoResizingLabel();
		titleDescriptionLabel = new AutoResizingLabel();

		titleLabel.setText("Alice");
		titleLabel.setForeground(Color.BLACK);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 36));

		titleDescriptionLabel.setText("Write Your Stories");
		titleDescriptionLabel.setForeground(Color.BLACK);
		titleDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 24));

		setLayout(new MigLayout("al center center", "", ""));
		setBackground(Color.WHITE);
		setBorder(new RoundedBorder(Color.BLACK, 3, 15));

		add(titleLabel, "w 100%, center, wrap");
		add(titleDescriptionLabel, "w 100%, center, wrap");

	}

	@Override
	protected void addUIEffects() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

}
