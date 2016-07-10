/**
 * 
 */
package view.utility;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;

/**
 * @author Alice
 * @sources https://community.oracle.com/thread/2137782?tstart=0
 * @sources http://stackoverflow.com/questions/8807717/java-rotate-rectangle-
 *          around-the-center
 */
@SuppressWarnings("serial")
public class CancelButton extends JButton {

	private RenderingHints hints;
	private double ratio;

	public CancelButton() {
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ratio = Integer.MIN_VALUE;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int height = getHeight();
		int width = getWidth();

		if (Integer.MIN_VALUE == ratio) {
			ratio = (double) Math.max(height, width) / (double) getFont().getSize();
		}

		double sizeRatio = (double) Math.max(height, width) / (double) getFont().getSize() / ratio;
		int size = (int) (getFont().getSize() * sizeRatio);

		int thickness = size / 3;
		int thickness_2 = thickness / 2;

		int donutWidth = size;
		int donutHeight = size;
		int donutX = width / 2 - size / 2;
		int donutY = height / 2 - size / 2;

		int holeWidth = size - thickness;
		int holeHeight = size - thickness;
		int holeX = donutX + thickness_2;
		int holeY = donutY + thickness_2;

		int rectWidth = size - thickness_2;
		int rectWidth_2 = rectWidth / 2;
		int rectHeight = thickness_2;
		int rectHeight_2 = rectHeight / 2;
		int rectX = donutX + (donutWidth / 2) - rectWidth_2;
		int rectY = donutY + (donutHeight / 2) - rectHeight_2;
		int rectAngleDeg = 45;
		int rectCenterX = rectX + rectWidth_2;
		int rectCenterY = rectY + rectHeight_2;

		Area donut = new Area(new Ellipse2D.Double(donutX, donutY, donutWidth, donutHeight));
		Area hole = new Area(new Ellipse2D.Double(holeX, holeY, holeWidth, holeHeight));
		donut.subtract(hole);

		Rectangle rect = new Rectangle(rectX, rectY, rectWidth, rectHeight);
		AffineTransform transform = new AffineTransform();
		transform.rotate(Math.toRadians(rectAngleDeg), rectCenterX, rectCenterY);
		Shape diag = transform.createTransformedShape(rect);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHints(hints);
		g2d.setColor(getForeground());
		g2d.fill(donut);
		g2d.fill(diag);

	}

}
