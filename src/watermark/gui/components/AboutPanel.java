package watermark.gui.components;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import watermark.gui.AppConfig;
import watermark.gui.AppIcons;

public class AboutPanel extends JPanel {
	private static final long serialVersionUID = -2875126868453799505L;

	private JLabel label;

	public AboutPanel() {
		String html = new StringBuffer("<html>").append("<h1>")
				.append(AppConfig.APP_TITLE).append("</h1>").append("<p>")
				.append("Fragile watermark encoding and decoding application.")
				.append("</p>").append("<br />").append("<p>")
				.append("Created by ").append(AppConfig.APP_AUTHOR)
				.append("</p>").append("<br />").append("<p>")
				.append("Version: ").append(AppConfig.APP_VERSION)
				.append("</p>").append("</html>").toString();

		this.setLayout(new BorderLayout(0, 20));

		this.label = new JLabel(html);

		this.label.setIcon(AppIcons.getAppLogo());

		this.add(label, BorderLayout.CENTER);

		this.add(new LinkButton(AppConfig.GIT_LINK), BorderLayout.SOUTH);
	}
}
