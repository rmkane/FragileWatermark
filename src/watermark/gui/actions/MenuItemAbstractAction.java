package watermark.gui.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

public abstract class MenuItemAbstractAction extends AbstractAction {
	private static final long serialVersionUID = 2454683093410580469L;

	public MenuItemAbstractAction(String text, ImageIcon icon, Integer mnemonic, String tooltip) {
		super(text);

		putValue(Action.SMALL_ICON, icon);
		putValue(Action.MNEMONIC_KEY, mnemonic);
		putValue(Action.SHORT_DESCRIPTION, tooltip);
	}
}