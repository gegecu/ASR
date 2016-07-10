package view.mode;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import org.languagetool.gui.GrammarChecker;

import controller.peer.SubmitController;
import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;
import view.utility.AutoResizingButton;
import view.utility.AutoResizingTextAreaWithPlaceHolder;
import view.utility.RoundedBorder;

/**
 * @author Alice
 * @since January 1, 2016
 */
@SuppressWarnings("serial")
public class StoryInputPanelv2 extends TemplatePanel {

	private JButton submitButton;

	private AutoResizingTextAreaWithPlaceHolder storySegmentInputArea;
	private JScrollPane storySegmentInputScrollPane;
	private JPanel foregroundPanel;

	private GrammarChecker grammarChecker;

	private boolean storySegmentInputAreaFocused = false;

	@Override
	protected void initializeUI() {

		submitButton = new AutoResizingButton();
		storySegmentInputArea = new AutoResizingTextAreaWithPlaceHolder();
		storySegmentInputScrollPane = new JScrollPane();
		foregroundPanel = new JPanel();

		submitButton.setText("<html>ADD TO<br/> MY STORY</html>");
		submitButton.setFocusPainted(false);
		submitButton.setBackground(Color.decode("#00A2E8"));
		submitButton.setForeground(Color.WHITE);
		submitButton.setHorizontalAlignment(SwingConstants.CENTER);
		submitButton.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 30));
		submitButton.setBorder(
				new RoundedBorder(Color.WHITE, 3, 12, 10, 10, 10, 10));

		storySegmentInputArea.setLineWrap(true);
		storySegmentInputArea.setWrapStyleWord(true);
		storySegmentInputArea.setBackground(Color.decode("#9BD8EA"));
		storySegmentInputArea.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 20));
		storySegmentInputArea.setPlaceHolder("WRITE YOUR STORY HERE");
		storySegmentInputArea.setPlaceHolderColor(Color.BLACK);

		storySegmentInputScrollPane.setViewportView(storySegmentInputArea);
		storySegmentInputScrollPane
				.setBorder(BorderFactory.createEmptyBorder());
		storySegmentInputScrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		storySegmentInputScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		foregroundPanel.setLayout(new MigLayout());
		foregroundPanel.setBackground(Color.decode("#9BD8EA"));
		foregroundPanel.add(storySegmentInputScrollPane,
				"h 100%, w 100%, grow");
		foregroundPanel
				.setBorder(new RoundedBorder(Color.decode("#7791AB"), 3, 12));

		setLayout(new MigLayout("insets 0, al center center"));
		setBackground(Color.decode("#FFD237"));

		add(foregroundPanel, "h 80%, w 70%, grow");
		add(submitButton, "h 80%, w 25%, grow, wrap");

		grammarChecker = new GrammarChecker(storySegmentInputArea);
		grammarChecker.setMillisecondDelay(300);

	}

	@Override
	protected void addUIEffects() {

		submitButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) submitButton
					.getBorder();

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					border.setThickness(5);
				} else if (false == model.isRollover()) {
					border.setThickness(3);
				}
			}

		});

		storySegmentInputArea.addMouseListener(new MouseInputAdapter() {

			private RoundedBorder border = (RoundedBorder) foregroundPanel
					.getBorder();

			@Override
			public void mouseExited(MouseEvent e) {
				if (false == storySegmentInputAreaFocused) {
					border.setThickness(3);
					foregroundPanel.repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (false == storySegmentInputAreaFocused) {
					border.setThickness(5);
					foregroundPanel.repaint();
				}
			}

		});

		storySegmentInputArea.addFocusListener(new FocusListener() {

			private RoundedBorder border = (RoundedBorder) foregroundPanel
					.getBorder();

			@Override
			public void focusLost(FocusEvent e) {
				storySegmentInputAreaFocused = false;
				border.setThickness(3);
				foregroundPanel.repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				storySegmentInputAreaFocused = true;
				border.setThickness(5);
				foregroundPanel.repaint();
			}

		});

		// storySegmentInputArea.addKeyListener(new KeyListener() {
		//
		// @Override
		// public void keyTyped(KeyEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void keyReleased(KeyEvent e) {
		// Highlighter highlighter = storySegmentInputArea.getHighlighter();
		// HighlightPainter painter = new
		// DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
		// int p0 = 0;
		// int p1 = storySegmentInputArea.getText().length();
		// try {
		// highlighter.addHighlight(p0, p1, painter);
		// } catch (BadLocationException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void keyPressed(KeyEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

	public void addSubmitButtonController(SubmitController submitController) {
		submitButton.addActionListener(submitController);
	}

	public String getInputStorySegment() {
		String text = storySegmentInputArea.getText();
		storySegmentInputArea.setText("");
		return text;
	}

	public JTextArea getInputArea() {
		return storySegmentInputArea;
	}

}
