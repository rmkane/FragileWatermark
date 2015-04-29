package example;

import java.awt.*;
import javax.swing.*;

/**
 * An example of a layered panels.
 * 
 * @author Ryan M. Kane
 */
public class LayerPanelExample extends JPanel {
	private static final long serialVersionUID = -146793497697754826L;

	public LayerPanelExample() {
		super();

		this.initComponents();
	}

	private void initComponents() {
		JLayeredPane layers = new JLayeredPane();
		layers.setPreferredSize(new Dimension(340, 180));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JTextArea(), BorderLayout.CENTER);
		panel.add(new JButton("Submit"), BorderLayout.SOUTH);
		panel.setSize(340, 180); // JLayeredPane has no layout.

		JPanel glass1 = new JPanel();
		glass1.setOpaque(true);
		glass1.setBackground(new Color(0x7F00FF00, true));
		glass1.setSize(300, 140);
		glass1.setLocation(10, 10);
		
		JPanel glass2 = new JPanel();
		glass2.setOpaque(true);
		glass2.setBackground(new Color(0x7F0000FF, true));
		glass2.setSize(300, 140);
		glass2.setLocation(20, 20);
		
		JPanel glass3 = new JPanel();
		glass3.setOpaque(true);
		glass3.setBackground(new Color(0x7FFF0000, true));
		glass3.setSize(300, 140);
		glass3.setLocation(30, 30);

		layers.add(panel, Integer.valueOf(1));
		layers.add(glass1, Integer.valueOf(2));
		layers.add(glass2, Integer.valueOf(3));
		layers.add(glass3, Integer.valueOf(4));
		
		this.add(layers);
	}

	public static void main(String args[]) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				LayerPanelExample panel = new LayerPanelExample();
				
				frame.setContentPane(panel);
				frame.setTitle("Layers Example");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}