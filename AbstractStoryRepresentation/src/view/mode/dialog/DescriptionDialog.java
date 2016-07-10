package view.mode.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import view.mode.StoryViewPanel;
import view.utility.RoundedBorder;

public class DescriptionDialog extends JDialog {

	private JPanel panel;
	private StoryViewPanel storyViewPanel;

	public DescriptionDialog(String title, String description) {

		panel = new JPanel();
		storyViewPanel = new StoryViewPanel(95, 5);

		setLayout(new BorderLayout());

		panel.setLayout(new MigLayout());
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0));

		storyViewPanel.setText(description);
		storyViewPanel.setFont(new Font("Arial", Font.BOLD, 25));

		panel.add(storyViewPanel, "w 100%, h 100%, grow");

		add(panel, BorderLayout.CENTER);

		setTitle(title);
		setMinimumSize(new Dimension(550, 200));
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setModal(true);

	}

}
