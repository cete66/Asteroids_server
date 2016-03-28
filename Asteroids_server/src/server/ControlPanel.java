package server;

import java.awt.BorderLayout;
//import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
//import javax.swing.border.EmptyBorder;

/**
 * Parte visual del servidor, para ver el marcador
 * @author Robin
 *
 */
public class ControlPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pane;
	private static ControlPanel frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//EventQueue.invokeLater(new Runnable() {
			//public void run() {
				try {
					frame = new ControlPanel();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			//}
		//});
	}

	/**
	 * Create the frame.
	 */
	public ControlPanel() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		pane = new JPanel();
		//pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setLayout(new BorderLayout(0, 0));
		setContentPane(pane);
	}

}
