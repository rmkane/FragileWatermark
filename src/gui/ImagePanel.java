package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * This class represents a panel which holds a label and an ImageCanvas.
 * 
 * @author Ryan M. Kane
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = -3590562915044514497L;
	
	private ImageCanvas canvas;
	private JLabel label;
	
	public ImagePanel(String labelText, int canvasWidth, int canvasHeight, int canvasPadding) {
		super();
		
		this.canvas = new ImageCanvas(canvasPadding, true);
		this.label = new JLabel(labelText, SwingConstants.CENTER);
		
		canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
		
		Font lblFont = label.getFont();
		label.setFont(lblFont.deriveFont(lblFont.getStyle() | Font.BOLD));
		
		this.setLayout(new BorderLayout());
		
		this.add(label, BorderLayout.NORTH);
		this.add(canvas, BorderLayout.CENTER);
	}

	public BufferedImage getImage() {
		return canvas.getImage();
	}

	public void setImage(BufferedImage image) {
		this.canvas.setImage(image);
	}

	public ImageCanvas getCanvas() {
		return canvas;
	}

	public void setCanvas(ImageCanvas canvas) {
		this.canvas = canvas;
	}

	public String getLabel() {
		return label.getText();
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}
}
