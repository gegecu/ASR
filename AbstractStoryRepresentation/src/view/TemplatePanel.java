/**
 * 
 */
package view;

import javax.swing.JPanel;

/**
 * @author Alice
 */
@SuppressWarnings("serial")
public abstract class TemplatePanel extends JPanel {

	public TemplatePanel() {
		initializeUI();
		addUIEffects();
		addUXFeatures();
	}

	protected abstract void initializeUI();

	protected abstract void addUIEffects();

	protected abstract void addUXFeatures();

	public void reinitialize() {
		// do nothing for some
	}

}
