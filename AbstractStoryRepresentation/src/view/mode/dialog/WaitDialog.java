package view.mode.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import view.utilities.AutoResizingLabel;
import view.utilities.RoundedBorder;

public class WaitDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JLabel waitLabel;
	private JPanel panel;
	private WaitDialog waitDialog;

	public WaitDialog(String dialogText) {

		waitDialog = this;
		panel = new JPanel();
		waitLabel = new AutoResizingLabel();

		setLayout(new BorderLayout());

		panel.setLayout(new MigLayout("fillx"));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new RoundedBorder(Color.BLACK, 3, 0));

		waitLabel.setText(dialogText);
		waitLabel.setFont(new Font("Arial", Font.BOLD, 25));

		panel.add(waitLabel, "h 100%, center");

		add(panel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(500, 120));
		pack();
		setTitle("Please Wait");
		setLocationRelativeTo(null);
		setModal(true);

	}

	public void showDialog() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				waitDialog.setVisible(true);
				return null;
			}
		}.execute();
	}

}
