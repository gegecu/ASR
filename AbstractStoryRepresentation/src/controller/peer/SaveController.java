package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import view.MainFrame;

public class SaveController implements ActionListener {

	private MainFrame mainFrame;
	private JTextField titleField;
	private JTextArea storyInputArea;

	public SaveController(JTextField titleField, JTextArea textArea) {
		this.titleField = titleField;
		this.storyInputArea = textArea;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (titleField.getText().isEmpty()) {
			return;
		}

		if (storyInputArea.getText().isEmpty()) {
			return;
		}

		// if no title ask for title.

		// ask if save cannot be edited.

		// save to db

	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
