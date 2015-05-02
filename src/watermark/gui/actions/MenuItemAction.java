package watermark.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

public abstract class MenuItemAction extends MenuItemAbstractAction implements ActionListener {
	private static final long serialVersionUID = 2454683093410580469L;

	public MenuItemAction(String text, ImageIcon icon, Integer mnemonic, String tooltip) {
		super(text, icon, mnemonic, tooltip);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
	}
}