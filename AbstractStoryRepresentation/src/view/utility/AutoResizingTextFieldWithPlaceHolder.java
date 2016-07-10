package view.utility;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * @author Alice
 * @since December 30, 2015
 */
@SuppressWarnings("serial")
public class AutoResizingTextFieldWithPlaceHolder extends JTextField {

	private final String HelloWorld = "HELLO WORLD";
	private String placeHolder = "";
	private int width;
	private int height;
	private int stringWidth;
	private double ratio;
	private Color placeHolderColor = Color.GRAY;

	private Font fieldFont;
	private int baseWidth = Integer.MIN_VALUE;
	private int baseHeight = Integer.MIN_VALUE;

	private boolean fontChanged = true;

	private UndoManager undo;

	public AutoResizingTextFieldWithPlaceHolder() {
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

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public void setPlaceHolderColor(Color placeHolderColor) {
		this.placeHolderColor = placeHolderColor;
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
			fieldFont = getFont();

			height = getHeight();
			width = getWidth();

			stringWidth = getFontMetrics(fieldFont).stringWidth(HelloWorld);

			ratio = (double) baseWidth / (double) stringWidth;

		} else if ((getWidth() != width || getHeight() != height)) {

			height = getHeight();
			width = getWidth();

			double widthRatio = (double) width / (double) stringWidth / ratio;
			int newFontSize = (int) (fieldFont.getSize() * widthRatio);
			int fontSizeToUse = Math.min(newFontSize, height);

			super.setFont(
					fieldFont.deriveFont(fieldFont.getStyle(), fontSizeToUse));

		}

		if (0 < placeHolder.length() && true == getText().isEmpty()) {

			FontMetrics fontMetrics = g.getFontMetrics();
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(placeHolderColor);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int x = getPlaceHolderPosX(fontMetrics);
			int y = getPlaceHolderPosY(fontMetrics);
			g2.drawString(placeHolder, x, y);
			g2.dispose();

		}

	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		fontChanged = true;
	}

	private int getPlaceHolderPosX(FontMetrics fontMetrics) {

		int x = 0;

		switch (getHorizontalAlignment()) {
			case SwingConstants.LEFT :
			case SwingConstants.LEADING :
				x = getBorder().getBorderInsets(getParent()).left;
				break;
			case SwingConstants.TRAILING :

			case SwingConstants.RIGHT :
				x = getWidth() - getBorder().getBorderInsets(getParent()).right
						- (fontMetrics.stringWidth(placeHolder));
				break;
			case SwingConstants.CENTER :
				x = (this.getWidth()) / 2
						- (fontMetrics.stringWidth(placeHolder) / 2);
				break;
		}

		return x;

	}

	private int getPlaceHolderPosY(FontMetrics fontMetrics) {

		int y = ((getHeight() - fontMetrics.getHeight()) / 2)
				+ fontMetrics.getAscent();
		return y;

	}

	public void setCharacterLimit(int limit) {
		this.setDocument(new JTextFieldLimit(limit));
	}

	private class JTextFieldLimit extends PlainDocument {

		private int limit;

		public JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr)
				throws BadLocationException {

			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}

		}

	}

}
