package gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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

				frame.setTitle(APP_TITLE);
				frame.setContentPane(view);
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
