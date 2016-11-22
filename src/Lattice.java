import java.util.Random;


public class Lattice {

	private LatticePoint[][] array;
	private double T;
	private double J = 1.0, kB = 1.0;
	private static Random rand = new Random();

	//Constructor for the lattice class.  The arguments are an array of LatticePoints and a double for the system temperature
	public Lattice(LatticePoint[][] array, double T){
		this.array = array;
		this.T = T;
	}

	public LatticePoint[][] getArray(){
		return array;
	}
	public void setArray(LatticePoint[][] array){
		this.array = array;
	}

	public LatticePoint getLatticePoint(int i, int j){
		return getArray()[i][j];
	}

	public double getT(){
		return T;
	}
	public void setT(double T){
		this.T = T;
	}

	public int getPointState(int i, int j){
		return getArray()[i][j].getState();
	}
	public void setPointState(int i, int j, int state){
		getArray()[i][j].setState(state);
	}

	//simple void for printing the lattice to the terminal.
	public void printLattice(){
		for(int i = 0; i < array.length; i++){
			System.out.println();
			for(int j = 0; j < array.length; j++){
				System.out.print("[" + array[i][j].getState() + "]");
			}
		}
	}

	//simple void to graphically display the lattice at a given time.
	public void displayLattice(){
		LatticeFrame lf  = new LatticeFrame(new Lattice(getArray(), getT()));
		lf.setVisible(true);
	}

	//method for calculating the total energy of the system.
	//it works by calculating the sum of the product of the spins of a particle's south and east neighbours.
	//these are then summed for every particle in the lattice.
	public double systemEnergy(){

		double E1 = 0, E2 = 0;
		double runningTotal = 0;
		int n = getArray().length - 1;

		for(int i = 0; i <= n; i++){
			for(int j = 0; j <= n; j++){

				if(i == n) E1 = (getPointState(i, j)*getPointState(0, j));
				else E1 = (getPointState(i, j)*getPointState(i+1, j));

				if(j == n) E2 = (getPointState(i, j)*getPointState(i, 0));
				else E2 = (getPointState(i, j)*getPointState(i, j+1));

				runningTotal += E1 + E2;
			}
		}

		double systemEnergy = -J*runningTotal;
		return systemEnergy;	
	}

	//method for calculating the local energy of a particle.
	//it does this by calculating the product of the spins of the particle's 4 nearest neighbours.
	public double localEnergy(int i, int j){

		int n = getArray().length - 1;
		double E1 = 0, E2 = 0, E3 = 0, E4 = 0;

		if(i == 0) E1 = getPointState(i, j)*getPointState(n, j);
		else E1 = getPointState(i, j)*getPointState(i-1, j);

		if(j == n) E2 = getPointState(i, j)*getPointState(i, 0);
		else E2 = getPointState(i, j)*getPointState(i, j+1);

		if(i == n) E3 = getPointState(i, j)*getPointState(0, j);
		else E3 = getPointState(i, j)*getPointState(i+1, j);

		if(j == 0) E4 = getPointState(i, j)*getPointState(i, n);
		else E4 = getPointState(i, j)*getPointState(i, j-1);

		return -J*(E1 + E2 + E3 + E4);
	}

	//this method returns the system's magnetisation by summing over all the spins.
	public double systemMagnetisation(){

		int n = getArray().length;
		double runningTotal = 0.0;

		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				runningTotal += getPointState(i, j);
			}
		}
		return runningTotal;
	}

	//method for calculating the boltzmann weight of the system.
	public double boltzmannWeight(double E){
		return Math.exp(-E/(kB*getT()));
	}

	//a method for calculating if a random number is less than or equal to the boltzmann
	//weight of the system.
	public boolean boltzmannSuccess(double deltaE){

		
		double prob = rand.nextDouble();

		if(prob <= boltzmannWeight(deltaE)) return true;

		return false;
	}

	//method for use in glauber dynamics, it picks a specific particle and inverts its spin.
	public void spinFlip(int i, int j){

		if(getPointState(i, j) == 1) setPointState(i, j, -1);
		else setPointState(i, j, 1);
	}

	//method for use in kawasaki dynamics, it picks two particles and swaps their spins.
	public void spinSwap(int i1, int j1, int i2, int j2){
		int state1 = getPointState(i1, j1);
		int state2 = getPointState(i2, j2);

		setPointState(i1, j1, state2);
		setPointState(i2, j2, state1);
	}

	//method for glauber dynamics. it picks a point at random and flips its spin.
	//if this is energetically favourable then it stays flipped, if not, then it only stays flipped
	//according to the boltzmann probability of the energy change.
	public void glauberDynamics(){

		int n = getArray().length;
		int iRand = rand.nextInt(n);
		int jRand = rand.nextInt(n);

		double origLE = localEnergy(iRand, jRand);

		spinFlip(iRand, jRand);

		double newLE = localEnergy(iRand, jRand);
		double deltaE = newLE - origLE;

		if(deltaE > 0 && !boltzmannSuccess(deltaE)) spinFlip(iRand, jRand);
	}

	//method for kawasaki dynamics. it picks two points at random and swaps their spins.
	//if this is energetically favourable then they stay swapped, if not, then they only stay swapped
	//according to the boltzmann probability of the energy change.
	public void kawasakiDynamics(){

		int n = getArray().length;
		int iRand1 = rand.nextInt(n);
		int jRand1 = rand.nextInt(n);
		int iRand2 = rand.nextInt(n);
		int jRand2 = rand.nextInt(n);

		double origLE = localEnergy(iRand1, jRand1) + localEnergy(iRand2, jRand2);

		spinSwap(iRand1, jRand1, iRand2, jRand2);

		double newLE = localEnergy(iRand1, jRand1) + localEnergy(iRand2, jRand2);
		double deltaE = newLE - origLE;

		if(Math.abs(iRand1 - iRand2) == 1 && Math.abs(jRand1 - jRand2) == 1) deltaE += 4*J; //need to correct for periodic bc

		if(deltaE > 0 && !boltzmannSuccess(deltaE)) spinSwap(iRand1, jRand1, iRand2, jRand2);
	}

	//void to perform either glauber or kawasaki dynamics to the lattice for a specified number of iterations
	public void dynamicsRepeated(int iterations, String methodID){

		if(methodID.equals("g")){
			for(int i = 0; i < iterations; i++){
				glauberDynamics();
			}
		}else if(methodID.equals("k")){
			for(int i = 0; i < iterations; i++){
				kawasakiDynamics();
			}
		}else System.out.println("CODE ERROR, METHOD ID MUST BE g OR k");
	}

	//method to calculate the susceptibility of the system. it takes in an array of system magnetisations
	//then finds the their variance and multiplies this by 1/(N*T**2) where N is the no. of particles.
	public static double susceptibility(double[] magnetisms, double T, int systemSize){

		double sysMag = 0.0;
		double sysMagSq = 0.0;
		int nMags = magnetisms.length;

		for(int i = 0; i < nMags; i++){
			sysMag += magnetisms[i];
			sysMagSq += magnetisms[i]*magnetisms[i];
		}

		double mAvgSq = (sysMag/nMags)*(sysMag/nMags);
		double mSqAvg = sysMagSq/nMags;
		double chi = (1/(systemSize*systemSize*T))*(mSqAvg - mAvgSq);	
		return chi;
	}

	
	//method for finding the heat capacity of the system
	public static double heatCapacity(double[] energies, double T){

		double sysE = 0.0;
		double sysESq = 0.0;
		int nNrgs = energies.length;

		for(int i = 0; i < nNrgs; i++){
			sysE += energies[i];
			sysESq += energies[i]*energies[i];
		}

		double eSqAvg = sysESq/nNrgs;
		double eAvgSq = (sysE/nNrgs)*(sysE/nNrgs);
		double C = (1/(T*T))*(eSqAvg - eAvgSq);
		return C;

	}


	public static double suscepBootstrap(double[] magnetisms, double T, int systemSize){

		int nSus = 256;
		int nMags = magnetisms.length;
		double[] bootedSusceps = new double[nSus];
		double[] bootedSuscepsSq = new double[nSus];
		double origSus = Lattice.susceptibility(magnetisms, T, systemSize);
		bootedSusceps[0] = origSus;
		bootedSuscepsSq[0] = origSus*origSus;
		

		for(int i = 1; i < nSus; i++){
			double[] bootedMags = new double[nMags];

			for(int j = 0; j < nMags; j++){

				bootedMags[j] = magnetisms[rand.nextInt(nMags)];
			}

			double bootedSuscepI = Lattice.susceptibility(bootedMags, T, systemSize);
			bootedSusceps[i] = bootedSuscepI;
			bootedSuscepsSq[i] = bootedSuscepI*bootedSuscepI;

		}

		double chiSqBar = Toolbox.avgArrayValue(bootedSuscepsSq);
		double chiBar = Toolbox.avgArrayValue(bootedSusceps);

		return Math.sqrt(chiSqBar - chiBar*chiBar);
	}


	public static double capacBootstrap(double[] energies, double T){

		int nCaps = 256;
		int nEs = energies.length;
		double[] bootedCaps = new double[nCaps];
		double[] bootedCapsSq = new double[nCaps];
		double origCap = Lattice.heatCapacity(energies, T);
		bootedCaps[0] = origCap;
		bootedCapsSq[0] = origCap*origCap;

		for(int i = 1; i < nCaps; i++){
			double[] bootedMags = new double[nEs];

			for(int j = 0; j < nEs; j++){

				bootedMags[j] = energies[rand.nextInt(nEs)];
			}

			double bootedSuscepI = Lattice.heatCapacity(energies, T);
			bootedCaps[i] = bootedSuscepI;
			bootedCapsSq[i] = bootedSuscepI*bootedSuscepI;

		}

		double cSqBar = Toolbox.avgArrayValue(bootedCapsSq);
		double cBar = Toolbox.avgArrayValue(bootedCaps);

		return Math.sqrt(cSqBar - cBar*cBar);

	}



	public static double suscepJacknife(double[] magnetisms, double T, int systemSize){

		int n = magnetisms.length;
		double[] jackedSusceptibilities = new double[n];

		for(int i = 0; i < n; i++){

			double[] lessMagnetisms = Toolbox.removeIthArrayElement(magnetisms, i);
			double suscepI = Lattice.susceptibility(lessMagnetisms, T, systemSize);
			jackedSusceptibilities[i] = suscepI;

		}

		double chiBar = Toolbox.avgArrayValue(jackedSusceptibilities);
		double runningTotal = 0.0;

		for(int i = 0; i < n; i++){

			double errorI = (jackedSusceptibilities[i] - chiBar)*(jackedSusceptibilities[i] - chiBar);
			runningTotal += errorI;
		}

		return Math.sqrt(runningTotal);
	}


	public static double capacJacknife(double[] energies, double T){

		int n = energies.length;
		double[] jackedCapacities = new double[n];

		for(int i = 0; i < n; i++){

			double[] lessEnergies = Toolbox.removeIthArrayElement(energies, i);
			double capacI = Lattice.heatCapacity(lessEnergies, T);
			jackedCapacities[i] = capacI;

		}

		double cBar = Toolbox.avgArrayValue(jackedCapacities);
		double runningTotal = 0.0;

		for(int i = 0; i < n; i++){

			double errorI = (jackedCapacities[i] - cBar)*(jackedCapacities[i] - cBar);
			runningTotal += errorI;
		}

		return Math.sqrt(runningTotal);
	}


	public static void susceptibilityGraph(){

		int systemSize = 50;
		int sweep = systemSize*systemSize;
		int nDatums = 80;
		int equibThreshold = 100*sweep;
		int postEquibThreshold = 16*sweep;
		String dynamicsChoice = "g";
		int nMagnetisms = 512;
		String filename = "susceptibilitiesfinal_jacknife2_50";

		double T = 1.5;
		double finalT = 4;
		double increment = (finalT-T)/(double)nDatums;
		double[] susceptibilities = new double[nDatums];
		double[] errors = new double[nDatums];
		double[] temperatures = new double[nDatums];

		Lattice lattice = new Lattice(LatticePoint.allPointsUp(systemSize), T);
		LoadingFrame lf = new LoadingFrame("Susceptibility Graph", nDatums);
		lf.setVisible(true);


		for(int d = 0; d < nDatums; d++){

			double[] magnetisms = new double[nMagnetisms];

			lattice.dynamicsRepeated(equibThreshold, dynamicsChoice);

			for(int i = 0; i < nMagnetisms; i++){
				lattice.dynamicsRepeated(postEquibThreshold, dynamicsChoice);
				double magnetism = lattice.systemMagnetisation();	
				magnetisms[i] = magnetism;
			}

			susceptibilities[d] = Lattice.susceptibility(magnetisms, T, systemSize);
			errors[d] = Lattice.suscepBootstrap(magnetisms, T, systemSize);
			temperatures[d] = T;
			T+=increment;
			lattice.setT(T);
			lf.updateLoadingBar(d);
		}

		Toolbox.createGraph(temperatures, susceptibilities, "Susceptibility", "T", "Chi");
		Toolbox.writeResultsToFile(temperatures, susceptibilities, errors, filename);

	}


	public static void heatCapacityGraph(){

		int systemSize = 64;
		int sweep = systemSize*systemSize;
		int nDatums = 80;
		int equibThreshold = 100*sweep;
		int postEquibThreshold = 16*sweep;
		String dynamicsChoice = "k";
		int nEnergies = 512;
		String filename = "heatCapacitiesfinal_kawasaki_bootstrap2";
		String graphTitle = "Heat Capcity vs T";

		double T = 1.5;
		double finalT = 4;
		double increment = (finalT-T)/(double)nDatums;
		double[] capacities = new double[nDatums];
		double[] errors = new double[nDatums];
		double[] temperatures = new double[nDatums];

		Lattice lattice = new Lattice(LatticePoint.allPointsUp(systemSize), T);
		if(dynamicsChoice.equals("k")){
			lattice = new Lattice(LatticePoint.randLatticeArray(systemSize), T);
			lattice.dynamicsRepeated(equibThreshold, dynamicsChoice);
			graphTitle = "Heat Capacity (kawasaki) vs T";
		}

		LoadingFrame lf = new LoadingFrame("Heat Capacity Graph", nDatums);
		lf.setVisible(true);


		for(int d = 0; d < nDatums; d++){

			double[] energies = new double[nEnergies];

			lattice.dynamicsRepeated(equibThreshold, dynamicsChoice);

			for(int i = 0; i < nEnergies; i++){
				lattice.dynamicsRepeated(postEquibThreshold, dynamicsChoice);
				double energy = lattice.systemEnergy();
				energies[i] = energy;
			}

			capacities[d] = Lattice.heatCapacity(energies, T);
			errors[d] = Lattice.capacBootstrap(energies, T);
			temperatures[d] = T;
			T+=increment;
			lattice.setT(T);
			lf.updateLoadingBar(d);
		}

		Toolbox.createGraph(temperatures, capacities, graphTitle, "T", "C");
		Toolbox.writeResultsToFile(temperatures, capacities, errors, filename);
	}



}
