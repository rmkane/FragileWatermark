package watermark.gui.components;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import watermark.core.util.ImageUtil;

public class AboutPanel extends JPanel {
	private static final long serialVersionUID = -2875126868453799505L;

	private JLabel label;
	private ImageIcon icon;

	public AboutPanel(String appTitle, String appVersion) {
		String html = new StringBuffer("<html>").append("<h1>")
				.append(appTitle).append("</h1>").append("<p>")
				.append("Fragile watermark encoding and decoding application.")
				.append("</p>").append("<br />").append("<p>")
				.append("Created by Ryan M. Kane").append("</p>")
				.append("<br />").append("<p>").append("Version: ")
				.append(appVersion).append("</p>").append("</html>").toString();

		this.setLayout(new BorderLayout(0, 20));

		this.label = new JLabel(html);
		this.icon = ImageUtil.loadIcon("resources/icon-128.png");

		this.label.setIcon(this.icon);
		this.add(label, BorderLayout.CENTER);

		this.add(
				new LinkButton("https://github.com/ryankane/FragileWatermark"),
				BorderLayout.SOUTH);
	}
}
