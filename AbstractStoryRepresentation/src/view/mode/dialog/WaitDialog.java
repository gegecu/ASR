package view.mode.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import view.utility.AutoResizingLabel;
import view.utility.RoundedBorder;

public class WaitDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JLabel waitLabel;
	private JPanel panel;
	private FontMetrics fontMetrics;

	public WaitDialog(String dialogText) {

		panel = new JPanel();
		waitLabel = new AutoResizingLabel();

		setLayout(new BorderLayout());

		panel.setLayout(new MigLayout("fillx"));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0));

		waitLabel.setText(dialogText);
		waitLabel.setFont(new Font("Arial", Font.BOLD, 25));

		fontMetrics = waitLabel.getFontMetrics(waitLabel.getFont());

		int width = fontMetrics.stringWidth(dialogText);

		panel.add(waitLabel, "h 100%, center");

		add(panel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(width + 100, 120));
		pack();
		setTitle("Please Wait");
		setLocationRelativeTo(null);
		setModal(true);

	}

}
