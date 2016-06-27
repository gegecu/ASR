/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;
import view.utilities.CustomScrollBarUI;
import view.utilities.RoundedBorder;

/**
 * @author Alice
 * @since December 30, 2015
 */
@SuppressWarnings("serial")
public abstract class ViewPanelv2 extends TemplatePanel {

	protected JScrollPane scrollPane;
	protected JScrollBar verticalScrollBar;
	protected JPanel foregroundPanel; // the foreground panel with border
	protected JPanel scrollPanePanel; // the background panel for data
	protected CustomScrollBarUI customScrollBarUI;
	protected double scrollPanePanelWidthRatio = 0;

	/**
	 * @param panelWidthPercentage
	 * @param verticalScrollBarWidthPercentage
	 */
	public ViewPanelv2(int panelWidthPercentage,
			int verticalScrollBarWidthPercentage) {
		MigLayout layout = ((MigLayout) getLayout());
		layout.setComponentConstraints(foregroundPanel,
				layout.getComponentConstraints(foregroundPanel) + ", w "
						+ panelWidthPercentage + "%");
	}

	@Override
	protected void initializeUI() {

		foregroundPanel = new JPanel();
		scrollPanePanel = new JPanel();
		scrollPane = new JScrollPane();
		verticalScrollBar = scrollPane.getVerticalScrollBar();
		customScrollBarUI = new CustomScrollBarUI(Color.WHITE, Color.BLACK, 3,
				Color.WHITE, Color.BLACK, 3, 12);

		setLayout(new MigLayout("nocache, insets 0 0 0 0"));
		setBackground(Color.WHITE);

		scrollPanePanel.setBackground(Color.WHITE);

		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(scrollPanePanel);

		//		verticalScrollBar.setUI(customScrollBarUI);
		verticalScrollBar.setBackground(Color.white);
		verticalScrollBar.setUnitIncrement(16);

		JPanel scrollPanePanel = new JPanel();
		scrollPanePanel.setLayout(new MigLayout("insets 10 10 10 5"));
		scrollPanePanel.setOpaque(false);
		scrollPanePanel.add(scrollPane, "h 100%, w 100%, grow");

		foregroundPanel.setLayout(new MigLayout("insets 0, gapx 0"));
		foregroundPanel.setBackground(Color.white);
		foregroundPanel
				.setBorder(new RoundedBorder(Color.decode("#7E7E7E"), 3, 24));
		foregroundPanel.add(scrollPanePanel, "h 100%, w 94%");

		JPanel scrollBarPanel = new JPanel();
		scrollBarPanel.setLayout(new MigLayout());
		scrollBarPanel.setOpaque(false);
		scrollBarPanel.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0,
				Color.decode("#7F7F7F")));
		scrollBarPanel.add(verticalScrollBar, "h 100%, w 100%, grow");

		foregroundPanel.add(scrollBarPanel, "h 100%, w 6%");

		add(foregroundPanel, "h 100%, grow");

	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		if (0 == scrollPanePanelWidthRatio) {
			scrollPanePanelWidthRatio = (double) scrollPanePanel.getWidth()
					/ foregroundPanel.getWidth();
		}

		// Resize the width of scrollPanePanel because it doesn't resize
		// automatically
		Dimension w = scrollPanePanel.getPreferredSize();
		w.width = (int) (foregroundPanel.getWidth()
				* scrollPanePanelWidthRatio);
		scrollPanePanel.setPreferredSize(w);

	}

	protected void addUIEffects() {

	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

}
