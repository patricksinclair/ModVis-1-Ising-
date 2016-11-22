import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


class LoadingPanel extends JPanel{
	
	int progress;
	int target;
	
	public LoadingPanel(int progress, int target){
		setBackground(Color.BLACK);
		this.progress = progress;
		this.target = target;
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		int h = 40;
		
		g.setColor(Color.GREEN);
		g.fillRect(0, getHeight()/2-h/2, ((progress + 1)*getWidth())/target, h);
	}
	
	public void updateProgress(int progressed){
		progress = progressed;
		repaint();
	}
	
}




public class LoadingFrame extends JFrame{

	LoadingPanel lPan;
	String process;
	
	public LoadingFrame(String process, int target){
		
		lPan = new LoadingPanel(0, target);
		lPan.setPreferredSize(new Dimension(512, 128));
		this.process = process;
		
		getContentPane().add(lPan, BorderLayout.CENTER);
		pack();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		
		setTitle("Calculating: " + process);
		setLocation(600, 20);
		setVisible(true);
		setBackground(Color.LIGHT_GRAY);
		
	}
	
	public JPanel getPanel(){
		return lPan;
	}
	
	public void updateLoadingBar(int i){
		lPan.updateProgress(i);
		repaint();
	}
	
}
