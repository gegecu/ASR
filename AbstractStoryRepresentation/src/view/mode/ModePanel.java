package view.mode;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import view.MainFrame;
import view.TemplatePanel;

public abstract class ModePanel extends TemplatePanel {

	private static final long serialVersionUID = 1L;

	protected JButton cancelButton;
	protected JButton saveButton;

	public void addCancelButtonActionListener(ActionListener actionListener) {
		cancelButton.addActionListener(actionListener);
	}

	public void addSaveButtonActionListener(ActionListener actionListener) {
		saveButton.addActionListener(actionListener);
	}

}
