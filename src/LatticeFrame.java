import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;


class LatticePanel extends JPanel{
	Lattice array;
	int arraySize;
	int iteration = 0;

	public LatticePanel(Lattice array){
		this.array = array;
		this.arraySize = array.getArray().length;
		setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		int w = getWidth()/getN(array);
		int h = getHeight()/getN(array);

		for(int i = 0; i < getN(array); i++){
			for(int j = 0; j < getN(array); j++){

				if(getArray().getArray()[i][j].getState() == 1){
					g.setColor(Color.RED);
					g.fillRect(w*i, h*j, w, h);

				}else if (getArray().getArray()[i][j].getState() == -1){
					g.setColor(Color.BLUE);
					g.fillRect(w*i, h*j, w, h);
				}else{
					g.setColor(Color.WHITE);
					g.fillRect(w*i, h*j, w, h);
				}
			}
		}
	}


	public void glauberAnimate(){
		for(int i = 0; i < 1000; i++){
		array.glauberDynamics();
		}
		repaint();
	}

	public void kawasakiAnimate(){
		for(int i = 0; i < 1000; i++){
		array.kawasakiDynamics();
		}
		repaint();
	}

	public void resetRandom(){
		LatticePoint[][] arrayPoints = LatticePoint.randLatticeArray(array.getArray().length);
		array = new Lattice(arrayPoints, array.getT());
		repaint();
	}

	public void resetAligned(){
		LatticePoint[][] arrayPoints = LatticePoint.allPointsUp(array.getArray().length);
		array = new Lattice(arrayPoints, array.getT());
		repaint();
	}

	public void changeSize(int n){
		LatticePoint[][] arrayPoints = LatticePoint.randLatticeArray(n);
		array = new Lattice(arrayPoints, array.getT());
		repaint();
	}

	public void changeTemperature(double T){
		array.setT(T);
		repaint();
	}

	public Lattice getArray(){
		return array;
	}

	public int getN(Lattice array){
		return array.getArray().length;
	}
}



public class LatticeFrame extends JFrame{

	int dimension = 64;
	double T = 2;

	LatticePanel lPan;
	Lattice array;
	Timer glauberTimer, kawasakiTimer;
	JLabel tempLabel = new JLabel("Temperature: ");
	JTextField tempField = new JTextField(String.valueOf(T), 10);
	JLabel sizeLabel = new JLabel("Size of system: ");
	JTextField sizeField = new JTextField(String.valueOf(dimension), 10);

	JButton glauberButton = new JButton("Glauber");
	JButton kawasakiButton = new JButton("Kawasaki");
	JButton resetRandButton = new JButton("Reset randomly");
	JButton resetAlignedButton = new JButton("Reset Aligned");
	JButton highThenLowButton = new JButton("High then Low");

	public LatticeFrame(){

		LatticePoint[][] arrayPoints = LatticePoint.randLatticeArray(dimension);
		Lattice lattice = new Lattice(arrayPoints, T);

		lPan = new LatticePanel(lattice);
		lPan.setPreferredSize(new Dimension(512, 512));

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(glauberButton);
		buttonPanel.add(kawasakiButton);
		buttonPanel.add(resetRandButton);
		buttonPanel.add(resetAlignedButton);
		//buttonPanel.add(highThenLowButton);

		JPanel inputPanel = new JPanel();
		inputPanel.add(tempLabel);
		inputPanel.add(tempField);
		inputPanel.add(sizeLabel);
		inputPanel.add(sizeField);

		getContentPane().add(lPan, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(inputPanel, BorderLayout.NORTH);
		pack();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				System.exit(0);
			}
		});

		setTitle("Ising model Mk II");
		setLocation(100, 20);
		setVisible(true);
		setBackground(Color.LIGHT_GRAY);
		glauberAnimate();
		kawasakiAnimate();
		resetRandomly();
		resetAligned();
		highThenLow();
		changeTemperature();
		changeSize();
	}


	public LatticeFrame(Lattice lattice){

		lPan = new LatticePanel(lattice);
		lPan.setPreferredSize(new Dimension(512, 512));
		getContentPane().add(lPan, BorderLayout.CENTER);
		pack();

		setTitle("Ising model");
		setLocation(100, 20);
		setVisible(true);
		setBackground(Color.LIGHT_GRAY);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				System.exit(0);
			}
		});

	}



	public void glauberAnimate(){

		glauberTimer = new Timer(0, (e)->{
			lPan.glauberAnimate();
		});

		glauberButton.addActionListener((e)->{

			if(kawasakiTimer.isRunning()) kawasakiTimer.stop();

			if(!glauberTimer.isRunning()){
				glauberTimer.start();
			} else {
				glauberTimer.stop();
			}
		});
	}


	public void kawasakiAnimate(){

		kawasakiTimer = new Timer(0, (e)->{
			lPan.kawasakiAnimate();
		});

		kawasakiButton.addActionListener((e)->{
			if(glauberTimer.isRunning()) glauberTimer.stop();

			if(!kawasakiTimer.isRunning()){
				kawasakiTimer.start();
			} else {
				kawasakiTimer.stop();
			}
		});
	}


	public void resetRandomly(){
		resetRandButton.addActionListener((e)->{
			glauberTimer.stop();
			kawasakiTimer.stop();
			lPan.resetRandom();
		});
	}


	public void resetAligned(){
		resetAlignedButton.addActionListener((e)->{
			glauberTimer.stop();
			kawasakiTimer.stop();
			lPan.resetAligned();
		});
	}
	
	
	public void highThenLow(){
		
		glauberTimer = new Timer(0, (e)->{
			lPan.glauberAnimate();
		});
		
		highThenLowButton.addActionListener((e)->{
			lPan.changeTemperature(300);
			glauberTimer.start();
			
			try{
				wait(400000);
			}catch (Exception ex){}
			
			lPan.changeTemperature(0.7);		
		});
	}


	public void changeTemperature(){
		tempField.addActionListener((e)->{
			double T = Double.parseDouble(tempField.getText());
			lPan.changeTemperature(T);
		});

	}

	public void changeSize(){
		sizeField.addActionListener((e)->{
			int n = Integer.parseInt(sizeField.getText());
			lPan.changeSize(n);
		});
	}


}
