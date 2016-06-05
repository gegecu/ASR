/**
 * 
 */
package view.utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * @author User
 *
 */
@SuppressWarnings("serial")
public class AutoResizingTextAreaWithPlaceHolder extends JTextArea {

	private final String HelloWorld = "HELLO WORLD";
	private String placeHolder = "";
	private int width;
	private int height;
	private int stringWidth;
	private double ratio;
	private Color placeHolderColor = Color.GRAY;

	private Font areaFont;
	private int baseWidth = Integer.MIN_VALUE;
	private int baseHeight = Integer.MIN_VALUE;

	private boolean fontChanged = true;

	private UndoManager undo;

	public AutoResizingTextAreaWithPlaceHolder() {

		setForeground(Color.BLACK);

		undo = new UndoManager();
		Document doc = getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent evt) {
				undo.addEdit(evt.getEdit());
			}
		});
		getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canUndo()) {
						undo.undo();
					}
				} catch (CannotUndoException e) {
				}
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
		getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canRedo()) {
						undo.redo();
					}
				} catch (CannotRedoException e) {
				}
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (Integer.MIN_VALUE == baseWidth && Integer.MIN_VALUE == baseHeight) {

			baseHeight = getHeight();
			baseWidth = getWidth();

		}

		if (true == fontChanged) {

			fontChanged = false;
			areaFont = getFont();

			height = getHeight();
			width = getWidth();

			stringWidth = getFontMetrics(areaFont).stringWidth(HelloWorld);

			ratio = (double) baseWidth / (double) stringWidth;

		} else if ((getWidth() != width || getHeight() != height)) {

			height = getHeight();
			width = getWidth();

			double widthRatio = (double) width / (double) stringWidth / ratio;
			int newFontSize = (int) (areaFont.getSize() * widthRatio);
			int fontSizeToUse = Math.min(newFontSize, height);

			super.setFont(
					areaFont.deriveFont(areaFont.getStyle(), fontSizeToUse));

		}

		if (0 < placeHolder.length() && true == getText().isEmpty()) {

			FontMetrics fm = g.getFontMetrics();
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(placeHolderColor);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int x = 0;
			int y = fm.getAscent();
			g2.drawString(placeHolder, x, y);
			g2.dispose();

		}

	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		fontChanged = true;
	}

}
