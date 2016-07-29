/**
 * 
 */
package view.utility;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import view.mode.dialog.DescriptionDialog;

/**
 * @author Alice
 * @since December 28, 2015
 */
@SuppressWarnings("serial")
public class AutoResizingButton extends JButton {

	private int width;
	private int height;
	private int stringWidth;
	private double ratio;
	private String buttonText;

	private Font buttonFont;
	private int baseWidth = Integer.MIN_VALUE;
	private int baseHeight = Integer.MIN_VALUE;

	private boolean fontChanged = true;
	private boolean checked = false;
	private List<DescriptionDialog> descriptionDialogs;

	private static float checkedStroke = 5f;
	private static float checkedStroke_2 = checkedStroke / 2;

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (Integer.MIN_VALUE == baseWidth && Integer.MIN_VALUE == baseHeight) {

			baseHeight = getHeight();
			baseWidth = getWidth();

		}

		if (true == fontChanged) {

			fontChanged = false;
			buttonFont = getFont();
			buttonText = getText();

			height = getHeight();
			width = getWidth();

			stringWidth = getFontMetrics(buttonFont).stringWidth(buttonText);

			ratio = (double) baseWidth / (double) stringWidth;

		} else if (getWidth() != width || getHeight() != height) {

			height = getHeight();
			width = getWidth();

			double widthRatio = (double) width / (double) stringWidth / ratio;
			int newFontSize = (int) (buttonFont.getSize() * widthRatio);
			int fontSizeToUse = Math.min(newFontSize, height);

			super.setFont(buttonFont.deriveFont(buttonFont.getStyle(),
					fontSizeToUse));

		}

		if (checked) {

			Graphics2D g2 = ((Graphics2D) g);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setStroke(new BasicStroke(checkedStroke));
			Line2D line = new Line2D.Float(0, checkedStroke_2, width, height - checkedStroke_2);
			g2.draw(line);
			line = new Line2D.Float(0, height - checkedStroke_2, width, checkedStroke_2);
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

	public boolean isChecked() {
		return checked;
	}

	public void addDescription(String title, String description) {

		if (descriptionDialogs == null) {

			descriptionDialogs = new ArrayList<>();

			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (DescriptionDialog dialogs : descriptionDialogs) {
						dialogs.setVisible(true);
					}
				}

			});

		}

		descriptionDialogs.add(new DescriptionDialog(title, description));

	}

}
