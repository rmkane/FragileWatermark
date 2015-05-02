package watermark.gui.components;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class TextAreaUndoManager {
	private UndoManager undoManager;

	public TextAreaUndoManager() {
		this.undoManager = new UndoManager();
	}

	public TextAreaUndoManager(JTextArea textArea) {
		this();

		this.attach(textArea);
	}

	public TextAreaUndoManager(int editLimit) {
		this();

		this.setEditLimit(editLimit);
	}

	public TextAreaUndoManager(JTextArea textArea, int editLimit) {
		this();

		this.attach(textArea);
		this.setEditLimit(editLimit);
	}

	/**
	 * Attach an undo/redo listener to a JTextField.
	 *
	 * @param textArea - the text area to listen to.
	 */
	public void attach(JTextArea textArea) {
		Document doc = textArea.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		});

		InputMap im = textArea.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = textArea.getActionMap();
		int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		// Undo action
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, keyMask), "Undo");
		am.put("Undo", new UndoAction(undoManager));

		// Redo Action
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, keyMask), "Redo");
		am.put("Redo", new RedoAction(undoManager));
	}

	public void resetEdits() {
		this.undoManager.discardAllEdits();
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public int getEditLimit() {
		return undoManager.getLimit();
	}

	public void setEditLimit(int limit) {
		undoManager.setLimit(limit);
	}

	private class UndoAction extends UndoManagerAction {
		private static final long serialVersionUID = 2097548927662294618L;

		public UndoAction(UndoManager undoManager) {
			super(undoManager);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (this.getUndoManager().canUndo()) {
					this.getUndoManager().undo();
				}
			} catch (CannotUndoException exp) {
				exp.printStackTrace();
			}
		}
	}

	private class RedoAction extends UndoManagerAction {
		private static final long serialVersionUID = 6245892372905233379L;

		public RedoAction(UndoManager undoManager) {
			super(undoManager);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (this.getUndoManager().canRedo()) {
					this.getUndoManager().redo();
				}
			} catch (CannotUndoException exp) {
				exp.printStackTrace();
			}
		}
	}

	private abstract class UndoManagerAction extends AbstractAction {
		private static final long serialVersionUID = 793721923973447544L;

		private UndoManager undoManager;

		public UndoManagerAction(UndoManager undoManager) {
			super();

			this.undoManager = undoManager;
		}

		public UndoManager getUndoManager() {
			return this.undoManager;
		}
	}
}
