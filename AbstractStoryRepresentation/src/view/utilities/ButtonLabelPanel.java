/**
 * 
 */
package view.utilities;

import javax.swing.JButton;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;

/**
 * @author User
 *
 */
@SuppressWarnings("serial")
public class ButtonLabelPanel extends TemplatePanel {

	private JButton button;
	private String labelText;
	private JLabel buttonLabel;

	public ButtonLabelPanel(JButton button, String labelText) {
		this.button = button;
		this.labelText = labelText;
	}

	@Override
	protected void initializeUI() {
		setLayout(new MigLayout());
	}

	@Override
	protected void addUIEffects() {
	}

	@Override
	protected void addUXFeatures() {
	}

}
