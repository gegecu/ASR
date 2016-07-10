/**
 * 
 */
package view.utility;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JLabel;

/**
 * @author Alice
 * @since December 27, 2015
 * @source http://stackoverflow.com/questions/2715118/how-to-change-the-size-of-
 *         the-font-of-a-jlabel-to-take-the-maximum-size/2715279#2715279
 */
@SuppressWarnings("serial")
public class AutoResizingLabel extends JLabel {

	private int width;
	private int height;
	private int stringWidth;
	private double ratio;
	private String labelText;

	private Font labelFont;
	private int baseWidth = Integer.MIN_VALUE;
	private int baseHeight = Integer.MIN_VALUE;

	private boolean fontChanged = true;
	private boolean checked = false;

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (Integer.MIN_VALUE == baseWidth && Integer.MIN_VALUE == baseHeight) {

			baseHeight = getHeight();
			baseWidth = getWidth();

		}

		if (true == fontChanged) {

			fontChanged = false;
			labelFont = getFont();
			labelText = getText();

			height = getHeight();
			width = getWidth();

			stringWidth = getFontMetrics(labelFont).stringWidth(labelText);

			ratio = (double) baseWidth / (double) stringWidth;

		} else if (getWidth() != width || getHeight() != height) {

			height = getHeight();
			width = getWidth();

			double widthRatio = (double) width / (double) stringWidth / ratio;
			int newFontSize = (int) (labelFont.getSize() * widthRatio);
			int fontSizeToUse = Math.min(newFontSize, height);

			super.setFont(
					labelFont.deriveFont(labelFont.getStyle(), fontSizeToUse));

		}

		if (checked) {

			Graphics2D g2 = ((Graphics2D) g);
			g2.setStroke(new BasicStroke(5));
			Line2D line = new Line2D.Float(0, height / 2, width, height / 2);
			g2.draw(line);

		}

	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		fontChanged = true;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
