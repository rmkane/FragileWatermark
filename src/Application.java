import gui.MainView;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import cipher.PublicKeyCipher;
import controller.MainViewContoller;

/**
 * This class represents an entry point into the MainView. The Application wraps
 * the MainView in a JFrame window.
 *
 * @author Ryan M. Kane
 */
public class Application {
	private static final String APP_TITLE = "Fragile Watermark App";

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				MainView view = new MainView();
				MainViewContoller controller = new MainViewContoller();

				view.setController(controller);

				// Inject an RSA Public Key cipher into the view.
				view.setKeyCipher(new PublicKeyCipher("RSA"));

				frame.setContentPane(view);
				frame.setTitle(APP_TITLE);
				frame.pack();
				frame.setLocationRelativeTo(null);
				//frame.setResizable(false);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setJMenuBar(view.getMenu());
			}
		});
	}

}
