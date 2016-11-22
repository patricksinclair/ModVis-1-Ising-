import java.util.Arrays;
import java.util.Random;


public class LatticePoint {
	private int state;

	public LatticePoint(int state){
		this.state = state;
	}

	public int getState(){
		return state;
	}
	public void setState(int state){
		this.state = state;
	}


	public static LatticePoint[][] randLatticeArray(int n){

		LatticePoint[][] latticeArray = new LatticePoint[n][n];
		Random rand = new Random();

		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){

				latticeArray[i][j] = new LatticePoint((rand.nextInt(2)));

				if(latticeArray[i][j].getState() == 0){
					latticeArray[i][j].setState(-1);
				}
			}
		}	
		return latticeArray;
	}
	
	
	public static LatticePoint[][] allPointsUp(int n){
		
		LatticePoint[][] latticeArray = new LatticePoint[n][n];
		
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				latticeArray[i][j] = new LatticePoint(1);
			}
		}
		return latticeArray;
	}

	public static LatticePoint[][] copiedLatticePoints(LatticePoint[][] points){

		int n = points.length;
		int[][] states = new int[n][n];
		LatticePoint[][] copiedPoints = new LatticePoint[n][n];

		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				states[i][j] = points[i][j].getState();
			}
		}

		int[][] copiedStates = Arrays.copyOf(states, states.length);

		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				copiedPoints[i][j] = new LatticePoint(copiedStates[i][j]);
			}
		}
		return copiedPoints;

	}

}
