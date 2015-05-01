package watermark.gui.components;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.JButton;

import watermark.core.util.GuiUtils;
import watermark.core.util.WebUtil;

/**
 * This class represents a hyper link that can be clicked on to navigate to the
 * specified hyperlink in the users' browser.
 *
 * @author Ryan M. Kane
 */
public class LinkButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 1549979332914523157L;

	private String hyperlinkText;
	private HashMap<TextAttribute, Object> textAttributes;

	private static Color linkTextColor = Color.BLUE;
	private static Color hoverTextColor = Color.BLUE;
	private static Color activeTextColor = Color.RED;

	public LinkButton(String hyperlinkText) {
		super();

		this.hyperlinkText = hyperlinkText;
		this.textAttributes = new HashMap<TextAttribute, Object>();

		this.textAttributes.put(TextAttribute.FOREGROUND, linkTextColor);
		this.updateFont();

		this.setText(hyperlinkText);;
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setFocusPainted(false);
		this.setOpaque(false);

		this.addMouseListener(this);
	}

	public String getHyperlinkText() {
		return hyperlinkText;
	}

	public void setHyperlinkText(String hyperlinkText) {
		this.hyperlinkText = hyperlinkText;
	}

	protected void updateFont() {
		this.setFont(this.getFont().deriveFont(this.textAttributes));
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		this.updateFont();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.textAttributes.put(TextAttribute.UNDERLINE, -1);
		this.textAttributes.put(TextAttribute.FOREGROUND, linkTextColor);
		this.updateFont();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.textAttributes.put(TextAttribute.FOREGROUND, activeTextColor);
		this.updateFont();

		try {
			WebUtil.openWebpage(new URL(this.hyperlinkText));
		} catch (MalformedURLException ex) {
			GuiUtils.showErrorMessage("Could not open link.");
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		this.textAttributes.put(TextAttribute.FOREGROUND, hoverTextColor);
	}
}
