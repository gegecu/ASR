/**
 * 
 */
package view.utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * @author Alice
 * @since December 23, 2015
 */
public class CustomScrollBarUI extends BasicScrollBarUI {

	private Color thumbBorderColor;
	private Color thumbInsideColor;
	private Stroke thumbStroke;
	private int thumbStrokePad;
	private int thumbBorderThickness;

	private Color trackBorderColor;
	private Color trackInsideColor;
	private Stroke trackStroke;
	private int trackStrokePad;
	private int trackBorderThickness;

	private RenderingHints hints;
	private int radii;

	/**
	 * @param trackInsideColor
	 *            defines the color of the area inside the border of the track.
	 * @param trackBorderColor
	 *            defines the color of the border of the track.
	 * @param trackBorderThickness
	 *            defines the thickness of the borders of the thumb.
	 * @param thumbInsideColor
	 *            defines the color of the area inside the border of thumb.
	 * @param thumbBorderColor
	 *            defines the color of the border of the thumb.
	 * @param thumbBorderThickness
	 *            defines the thickness of the borders of the thumb.
	 * @param radii
	 *            defines the curveness of the 4 corners of the thumb and track.
	 */
	public CustomScrollBarUI(Color trackInsideColor, Color trackBorderColor, int trackBorderThickness,
			Color thumbInsideColor, Color thumbBorderColor, int thumbBorderThickness, int radii) {

		this.thumbBorderColor = thumbBorderColor;
		this.thumbInsideColor = thumbInsideColor;
		this.thumbBorderThickness = thumbBorderThickness;
		thumbStroke = new BasicStroke(thumbBorderThickness);
		thumbStrokePad = thumbBorderThickness / 2;

		this.trackBorderColor = thumbBorderColor;
		this.trackInsideColor = trackInsideColor;
		this.trackBorderThickness = trackBorderThickness;
		trackStroke = new BasicStroke(trackBorderThickness);
		trackStrokePad = thumbBorderThickness / 2;

		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.radii = radii;

	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

		Graphics2D g2 = (Graphics2D) g;

		int x = (int) trackBounds.getX();
		int y = (int) trackBounds.getY();
		int width = (int) trackBounds.getWidth();
		int height = (int) trackBounds.getHeight();

		int roundRectangleX = x + trackStrokePad + (int) (width * 0.1);
		int roundRectangleY = y + trackStrokePad + (int) (height * 0.015);
		int roundRectangleHeight = height - trackBorderThickness - (int) (height * 0.03);
		int roundRectangleWidth = width - trackBorderThickness - (int) (width * 0.2);

		RoundRectangle2D.Double roundRectangle = new RoundRectangle2D.Double(roundRectangleX, roundRectangleY,
				roundRectangleWidth, roundRectangleHeight, radii, radii);

		// the area of the border region
		Area roundRectangleArea = new Area(roundRectangle);

		roundRectangle.width -= trackBorderThickness;
		roundRectangle.height -= trackBorderThickness;
		roundRectangle.x += trackStrokePad;
		roundRectangle.y += trackStrokePad;

		g2.setRenderingHints(hints);

		Component parent = c.getParent();
		if (parent != null) {

			Color parentColor = parent.getBackground();
			Rectangle rectangle = new Rectangle(x, y, width, height);

			// the area outside the border region
			Area outerRectangleArea = new Area(rectangle);
			outerRectangleArea.subtract(roundRectangleArea);
			g2.setClip(outerRectangleArea);
			g2.setColor(parentColor);
			g2.fillRect(x, y, width, height);
			g2.setClip(null);

			// the area inside the border region
			Area innerRectangleArea = new Area(rectangle);
			innerRectangleArea.subtract(outerRectangleArea);
			g2.setClip(innerRectangleArea);
			g2.setColor(trackInsideColor);
			g2.fillRect(x, y, width, height);

		}

		g2.setColor(trackBorderColor);
		g2.setStroke(trackStroke);
		g2.draw(roundRectangle);

	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

		Graphics2D g2 = (Graphics2D) g.create();

		int x = (int) thumbBounds.getX();
		int y = (int) thumbBounds.getY();
		int width = (int) thumbBounds.getWidth();
		int height = (int) thumbBounds.getHeight();

		int roundRectangleX = x + thumbStrokePad;
		int roundRectangleY = y + thumbStrokePad;
		int roundRectangleHeight = height - thumbBorderThickness;
		int roundRectangleWidth = width - thumbBorderThickness;

		RoundRectangle2D.Double roundRectangle = new RoundRectangle2D.Double(roundRectangleX, roundRectangleY,
				roundRectangleWidth, roundRectangleHeight, radii, radii);

		// the area of the border region
		Area roundRectangleArea = new Area(roundRectangle);

		g2.setRenderingHints(hints);

		Component parent = c.getParent();
		if (parent != null) {

			Rectangle rectangle = new Rectangle(x, y, width, height);

			// the area outside the border region
			Area outerRectangleArea = new Area(rectangle);
			outerRectangleArea.subtract(roundRectangleArea);

			// the area inside the border region
			Area innerRectangleArea = new Area(rectangle);
			innerRectangleArea.subtract(outerRectangleArea);
			g2.setClip(innerRectangleArea);
			g2.setColor(thumbInsideColor);
			g2.fillRect(x, y, width, height);
			g2.setClip(null);

		}

		g2.setColor(thumbBorderColor);
		g2.setStroke(thumbStroke);
		g2.draw(roundRectangleArea);

	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return createZeroButton();
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return createZeroButton();
	}

	private JButton createZeroButton() {
		JButton jbutton = new JButton();
		jbutton.setPreferredSize(new Dimension(0, 0));
		jbutton.setMinimumSize(new Dimension(0, 0));
		jbutton.setMaximumSize(new Dimension(0, 0));
		return jbutton;
	}

	public void setThumbBorderThickness(int thumbThickness) {
		this.thumbBorderThickness = thumbThickness;
		thumbStroke = new BasicStroke(thumbThickness);
		thumbStrokePad = thumbThickness / 2;
	}

	public void setTrackBorderThickness(int trackThickness) {
		this.trackBorderThickness = trackThickness;
		trackStroke = new BasicStroke(trackThickness);
		trackStrokePad = trackThickness / 2;
	}

	/**
	 * method named getThumbBounds2 because getThumbBounds is already taken as
	 * protected.
	 * 
	 * @return clone of the bounds, not the actual bounds
	 */
	public Rectangle getThumbBounds2() {
		return (Rectangle) getThumbBounds().clone();
	}

	public Color getThumbBorderColor() {
		return thumbBorderColor;
	}

	public void setThumbBorderColor(Color thumbBorderColor) {
		this.thumbBorderColor = thumbBorderColor;
	}

	public Color getThumbInsideColor() {
		return thumbInsideColor;
	}

	public void setThumbInsideColor(Color thumbInsideColor) {
		this.thumbInsideColor = thumbInsideColor;
	}

	public Color getTrackBorderColor() {
		return trackBorderColor;
	}

	public void setTrackBorderColor(Color trackBorderColor) {
		this.trackBorderColor = trackBorderColor;
	}

	public Color getTrackInsideColor() {
		return trackInsideColor;
	}

	public void setTrackInsideColor(Color trackInsideColor) {
		this.trackInsideColor = trackInsideColor;
	}

	public int getRadii() {
		return radii;
	}

	public void setRadii(int radii) {
		this.radii = radii;
	}

}
