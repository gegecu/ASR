package view.utility;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.miginfocom.swing.MigLayout;

public class ProgressBarAddOn extends JPanel {

	private JPanel panel;
	private JProgressBar progressBar;

	private int totalValue;
	private int currentValue;

	public ProgressBarAddOn(JComponent component) {

		this.panel = new JPanel();
		this.progressBar = new JProgressBar();

		this.totalValue = 0;
		this.currentValue = 0;

		this.setLayout(new MigLayout("insets 0, gapy 0"));

		this.progressBar.setString("0 / 0");
		this.progressBar.setStringPainted(true);
		this.progressBar.setBorder(
				BorderFactory.createMatteBorder(0, 3, 3, 3, Color.BLACK));
		this.progressBar.setUI(new BasicProgressBarUI() {
			protected Color getSelectionBackground() {
				return Color.BLACK;
			}
			protected Color getSelectionForeground() {
				return Color.BLACK;
			}
		});

		this.add(component, "w 100%, wrap");
		this.add(progressBar, "w 100%");

	}

	public void setTotalValue(int totalValue) {
		this.totalValue = totalValue;
		this.progressBar.setMaximum(totalValue);
		this.progressBar.setString(currentValue + " / " + totalValue);
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
		this.progressBar.setValue(currentValue);
		this.progressBar.setString(currentValue + " / " + totalValue);
	}

}
