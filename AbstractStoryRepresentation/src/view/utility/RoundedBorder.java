package view.utility;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

/**
 * @author Modified For Alice
 * @since December 23, 2015
 * @source http://stackoverflow.com/questions/15025092/border-with-rounded-
 *         corners-transparency
 */
@SuppressWarnings("serial")
public class RoundedBorder extends AbstractBorder {

	private int thickness;
	private int radii;
	private int strokePad;

	private Color borderColor;
	private Insets insets;
	private BasicStroke stroke;
	private RenderingHints hints;

	/**
	 * @param color
	 *            defines the color of the border.
	 * @param thickness
	 *            defines the thickness the border.
	 * @param radii
	 *            defines the curveness of the 4 corners of the border.
	 * @comment default padding is 0
	 */
	public RoundedBorder(Color color, int thickness, int radii) {
		this(color, thickness, radii, 0, 0, 0, 0);
	}

	/**
	 * @param color
	 *            defines the color of the border.
	 * @param thickness
	 *            defines the thickness the border.
	 * @param radii
	 *            defines the curveness of the 4 corners of the border.
	 * @param paddingTop
	 *            defines the top padding of the component from the border.
	 * @param paddingLeft
	 *            defines the left padding of the component from the border.
	 * @param paddingBottom
	 *            defines the bottom padding of the component from the border.
	 * @param paddingRight
	 *            defines the right padding of the component from the border.
	 */
	public RoundedBorder(Color color, int thickness, int radii, int paddingTop,
			int paddingLeft, int paddingBottom, int paddingRight) {

		this.thickness = thickness;
		this.radii = radii;
		this.borderColor = color;

		stroke = new BasicStroke(thickness);
		strokePad = (int) (thickness * 0.5);

		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		insets = new Insets(strokePad + paddingTop, strokePad + paddingLeft,
				strokePad + paddingBottom, strokePad + paddingRight);

	}

	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		return getBorderInsets(c);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {

		Graphics2D g2 = (Graphics2D) g;

		int lineY = height - thickness;
		int lineX = width - thickness;

		RoundRectangle2D.Double roundRectangle = new RoundRectangle2D.Double(
				0 + strokePad, 0 + strokePad, lineX, lineY, radii, radii);

		Area roundRectangleArea = new Area(roundRectangle);

		g2.setRenderingHints(hints);

		Component parent = c.getParent();
		if (parent != null) {

			Color parentColor = parent.getBackground();

			Rectangle rectangle = new Rectangle(0, 0, width, height);

			// the area outside the border region
			Area rectangleArea = new Area(rectangle);
			rectangleArea.subtract(roundRectangleArea);
			g2.setClip(rectangleArea);
			g2.setColor(parentColor);
			g2.fillRect(0, 0, width, height);
			g2.setClip(null);

		}

		g2.setColor(borderColor);
		g2.setStroke(stroke);
		g2.draw(roundRectangleArea);

	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
		stroke = new BasicStroke(thickness);
		strokePad = (int) (thickness * 0.5);
	}

	public void setColor(Color color) {
		this.borderColor = color;
	}

}