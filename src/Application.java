import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import watermark.core.cipher.PublicKeyCipher;
import watermark.core.service.WatermarkService;
import watermark.core.service.WatermarkServiceImpl;
import watermark.core.util.ImageUtil;
import watermark.gui.controller.MainViewContoller;
import watermark.gui.view.MainView;

/**
 * This class represents an entry point into the MainView. The Application wraps
 * the MainView in a JFrame window.
 *
 * @author Ryan M. Kane
 */
public class Application {
	private static final String APP_TITLE = "Fragile Watermark App";
	private static final String APP_VERSION = "1.0.0-beta2";
	private static final String APP_ICON = "resources/icon-64.png";

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				MainView view = new MainView();
				MainViewContoller controller = new MainViewContoller();
				WatermarkService service = new WatermarkServiceImpl();
				ImageIcon icon = ImageUtil.loadIcon(APP_ICON);

				controller.setWatermarkService(service);
				view.setApplicationTitle(APP_TITLE);
				view.setApplicationVersion(APP_VERSION);
				view.setController(controller);

				// Inject an RSA Public Key cipher into the view.
				view.setKeyCipher(new PublicKeyCipher("RSA"));

				frame.setIconImage(icon.getImage());
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
