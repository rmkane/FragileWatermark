import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import watermark.core.cipher.PublicKeyCipher;
import watermark.core.service.WatermarkService;
import watermark.core.service.WatermarkServiceImpl;
import watermark.gui.AppConfig;
import watermark.gui.AppIcons;
import watermark.gui.controller.MainViewContoller;
import watermark.gui.view.MainView;

/**
 * This class represents an entry point into the MainView. The Application wraps
 * the MainView in a JFrame window.
 *
 * @author Ryan M. Kane
 */
public class Application {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				MainView view = new MainView(AppConfig.APP_TITLE);
				MainViewContoller controller = new MainViewContoller();
				WatermarkService service = new WatermarkServiceImpl();

				controller.setWatermarkService(service);
				view.setController(controller);

				// Inject an RSA Public Key cipher into the view.
				view.setKeyCipher(new PublicKeyCipher("RSA"));

				frame.setIconImage(AppIcons.getAppImage());
				frame.setContentPane(view);
				frame.setTitle(AppConfig.APP_TITLE);
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
