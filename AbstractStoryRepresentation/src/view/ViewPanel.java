/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.MouseInputAdapter;

import net.miginfocom.swing.MigLayout;
import view.utilities.CustomScrollBarUI;
import view.utilities.RoundedBorder;

/**
 * @author Alice
 * @since December 30, 2015
 */
@SuppressWarnings("serial")
public abstract class ViewPanel extends TemplatePanel {

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
	public ViewPanel(int panelWidthPercentage,
			int verticalScrollBarWidthPercentage) {
		MigLayout layout = ((MigLayout) getLayout());
		layout.setComponentConstraints(verticalScrollBar,
				layout.getComponentConstraints(verticalScrollBar) + ", w "
						+ verticalScrollBarWidthPercentage + "%");
		layout.setComponentConstraints(foregroundPanel,
				layout.getComponentConstraints(foregroundPanel) + ", w "
						+ panelWidthPercentage + "%");
	}

	/**
	 * @param panelWidthPercentage
	 * @param verticalScrollBarWidthPercentage
	 * @param paddingTop
	 *            top padding inside the panel
	 * @param paddingLeft
	 *            left padding inside the panel
	 * @param paddingBottom
	 *            bottom padding inside the panel
	 * @param paddingRight
	 *            right padding inside the panel
	 */
	public ViewPanel(int panelWidthPercentage,
			int verticalScrollBarWidthPercentage, int paddingTop,
			int paddingLeft, int paddingBottom, int paddingRight) {

		MigLayout layout = ((MigLayout) getLayout());
		layout.setComponentConstraints(verticalScrollBar,
				layout.getComponentConstraints(verticalScrollBar) + ", w "
						+ verticalScrollBarWidthPercentage + "%");
		layout.setComponentConstraints(foregroundPanel,
				layout.getComponentConstraints(foregroundPanel) + ", w "
						+ panelWidthPercentage + "%");

		layout = ((MigLayout) foregroundPanel.getLayout());
		layout.setLayoutConstraints(layout.getLayoutConstraints() + ", "
				+ String.format("insets %d %d %d %d", paddingTop, paddingLeft,
						paddingBottom, paddingRight));

	}

	@Override
	protected void initializeUI() {

		foregroundPanel = new JPanel();
		scrollPanePanel = new JPanel();
		scrollPane = new JScrollPane();
		verticalScrollBar = scrollPane.getVerticalScrollBar();
		customScrollBarUI = new CustomScrollBarUI(Color.WHITE, Color.BLACK, 3,
				Color.WHITE, Color.BLACK, 3, 12);

		setLayout(new MigLayout("nocache, insets 0 0 5 0"));
		setBackground(Color.WHITE);

		scrollPanePanel.setBackground(Color.WHITE);

		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(scrollPanePanel);

		verticalScrollBar.setUI(customScrollBarUI);
		verticalScrollBar.setBackground(Color.white);
		verticalScrollBar.setUnitIncrement(16);

		foregroundPanel.setLayout(new MigLayout());
		foregroundPanel.setBackground(Color.white);
		foregroundPanel.setBorder(new RoundedBorder(Color.BLACK, 3, 24));
		foregroundPanel.add(scrollPane, "w 100%, h 100%");

		add(foregroundPanel, "h 100%, grow");
		add(verticalScrollBar, "h 100%, grow");

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

		MouseInputAdapter verticalScrollBarMouseInputAdapter = new MouseInputAdapter() {

			private boolean pressed = false;
			private boolean inside = false;

			@Override
			public void mouseMoved(MouseEvent e) {
				inside = customScrollBarUI.getThumbBounds2()
						.contains(e.getPoint());
				if (true == inside) {
					customScrollBarUI.setThumbBorderThickness(5);
				} else if (false == pressed && false == inside) {
					customScrollBarUI.setThumbBorderThickness(3);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				inside = false;
				if (false == pressed) {
					customScrollBarUI.setThumbBorderThickness(3);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				pressed = false;
				if (false == inside) {
					customScrollBarUI.setThumbBorderThickness(3);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				pressed = true;
				customScrollBarUI.setThumbBorderThickness(5);
			}

		};

		verticalScrollBar.addMouseListener(verticalScrollBarMouseInputAdapter);
		verticalScrollBar
				.addMouseMotionListener(verticalScrollBarMouseInputAdapter);

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
