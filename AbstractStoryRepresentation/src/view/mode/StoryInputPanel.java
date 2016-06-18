package view.mode;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import controller.peer.SubmitController;
import net.miginfocom.swing.MigLayout;
import view.TemplatePanel;
import view.utilities.AutoResizingButton;
import view.utilities.AutoResizingTextAreaWithPlaceHolder;
import view.utilities.RoundedBorder;

/**
 * @author Alice
 * @since January 1, 2016
 */
@SuppressWarnings("serial")
public class StoryInputPanel extends TemplatePanel {

	private JButton submitButton;

	private AutoResizingTextAreaWithPlaceHolder storySegmentInputArea;
	private JScrollPane storySegmentInputScrollPane;
	private JPanel foregroundPanel;

	private boolean storySegmentInputAreaFocused = false;

	@Override
	protected void initializeUI() {

		submitButton = new AutoResizingButton();
		storySegmentInputArea = new AutoResizingTextAreaWithPlaceHolder();
		storySegmentInputScrollPane = new JScrollPane();
		foregroundPanel = new JPanel();

		submitButton.setText("Submit");
		submitButton.setFocusPainted(false);
		submitButton.setBackground(Color.WHITE);
		submitButton.setForeground(Color.BLACK);
		submitButton.setHorizontalAlignment(SwingConstants.CENTER);
		submitButton.setFont(new Font("Arial", Font.BOLD, 30));
		submitButton.setBorder(
				new RoundedBorder(Color.BLACK, 3, 12, 10, 10, 10, 10));

		storySegmentInputArea.setLineWrap(true);
		storySegmentInputArea.setWrapStyleWord(true);
		storySegmentInputArea.setFont(new Font("Arial", Font.BOLD, 20));
		storySegmentInputArea.setPlaceHolder("Input Your Story Here.");

		storySegmentInputScrollPane.setViewportView(storySegmentInputArea);
		storySegmentInputScrollPane
				.setBorder(BorderFactory.createEmptyBorder());
		storySegmentInputScrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		storySegmentInputScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		foregroundPanel.setLayout(new MigLayout());
		foregroundPanel.setBackground(Color.WHITE);
		foregroundPanel.add(storySegmentInputScrollPane,
				"h 100%, w 100%, grow");
		foregroundPanel.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		setLayout(new MigLayout("insets 0 10 0 10"));
		setBackground(Color.decode("#36B214"));

		add(foregroundPanel, "h 100%, w 75%, grow");
		add(submitButton, "h 100%, w 25%, grow, wrap");

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
