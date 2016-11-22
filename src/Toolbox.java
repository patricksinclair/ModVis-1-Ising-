import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;


public class Toolbox {

	
	public static XYDataset compileData(double[] xData, double[] yData){
		
		DefaultXYDataset ds = new DefaultXYDataset();

        double[][] data = {xData, yData};

        ds.addSeries("series1", data);

        return ds;
	}
	
	public static void createGraph(double[] xData, double[] yData, String graphTitle, String xLabel, String yLabel){
		XYDataset data = Toolbox.compileData(xData, yData);
		
		JFrame frame = new JFrame("Graph");
        
        JFreeChart chart = ChartFactory.createXYLineChart(
        		graphTitle,
                xLabel, 
                yLabel, 
                data, 
                PlotOrientation.VERTICAL, 
                true, 
                true,
                false
                );
        
        ChartPanel cp = new ChartPanel(chart);

        frame.getContentPane().add(cp);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
	}
	
	
	public static void writeResultsToFile(double[] xData, double[] yData, double[] errors, String filename){
		
		try{
			
			File file = new File(filename+".txt");
			
			if(!file.exists()) file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int i = 0; i < xData.length; i ++){
				String output = String.valueOf(xData[i]) + ", " + String.valueOf(yData[i]) +", " + String.valueOf(errors[i]);
				bw.write(output);
				bw.newLine();
			}
			
			bw.close();
		}catch (IOException e){}
	}
	
	
	public static double[] removeIthArrayElement(double[] array, int iRemove){
		
		int n = array.length;
		double[] copiedArray = new double[n];
		double[] newArray = new double[n-1];
		
		for(int i = 0; i < n; i++){
			copiedArray[i] = array[i];
		}
		
		for(int i = iRemove; i < n-1; i++){
			
			copiedArray[i] = array[i+1];
		}
		
		for(int i = 0; i < n-1; i++){
			
			newArray[i] = copiedArray[i];
		}
		return newArray;
	}
	
	
	public static double avgArrayValue(double[] array){
		
		int n = array.length;
		double runningTotal = 0.0;
		
		for(int i = 0; i < n; i++){
			runningTotal+=array[i];
		}
		
		return runningTotal/(double)n;
	}
}

