import java.util.Random;

/////////////////////////////////////////////////////////////////////////////
// Title:            Ice
// Files:            Ice.java (current file)
// Project:			 hw8 - Question 1
// Semester:         CS540 Fall 2017
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// NetID:         	 koelergoular
// Section:      	 002
////////////////////////////80 columns wide //////////////////////////////////

public class Ice {
	//Array with  number of days Lake Mendota was covered with ice
	static int[] iceDays = {118, 151, 121, 96, 110, 117, 132, 104, 125, 118, 125,
					123, 110, 127, 131, 99, 126, 144, 136, 126, 91, 130, 62, 112,
					99, 161, 78, 124, 119, 124, 128, 131, 113, 88, 75, 111, 97,
					112, 101, 101, 91, 110, 100, 130, 111, 107, 105, 89, 126, 108,
					97, 94, 83, 106, 98, 101, 108, 99, 88, 115, 102, 116, 115, 82,
					110, 81, 96, 125, 104, 105, 124, 103, 106, 96, 107, 98, 65,
					115, 91, 94, 101, 121, 105, 97, 105, 96, 82, 116, 114, 92,
					98, 101, 104, 96, 109, 122, 114, 81, 85, 92, 114, 111, 95,
					126, 105, 108, 117, 112, 113, 120, 65, 98, 91, 108, 113, 110,
					105, 97, 105, 107, 88, 115, 123, 118, 99, 93, 96, 54, 111,
					85, 107, 89, 87, 97, 93, 88, 99, 108, 94, 74, 119, 102, 47,
					82, 53, 115, 21, 89, 80, 101, 95, 66, 106, 97, 87, 109, 57,
					87, 117, 91, 62, 65};
	
	//Variable with size of array
	static int iceDaysSize = iceDays.length;

	public static void main(String[] args) {
		//Gets value of flag
		int flag = 0;
		if (args.length != 0) flag = Integer.valueOf(args[0]);
		
		
		if (flag == 100) {
			//Variable to keep track of years
			int year = 1855;
			
			//Outputs year with number of days in iceDays
			for (int i = 0; i < iceDaysSize; i++) {
				System.out.println(year + " " + iceDays[i]);
				year++;
			}
		}
		
		else if (flag == 200) {
			//Outputs number of data points in iceDays
			System.out.println(iceDaysSize);
			
			//Calculates the sample mean and outputs it
			double sampleMean = 0;
			
			for (int i = 0; i < iceDaysSize; i++) {
				sampleMean += iceDays[i];
			}
			
			sampleMean = (sampleMean/iceDaysSize);
			
			System.out.println(String.format("%.2f", sampleMean));
			
			//Calculates the sample standard deviation and outputs it
			double sampleStandardDeviation = 0;
			
			for (int i = 0; i < iceDaysSize; i++) {
				sampleStandardDeviation += Math.pow(iceDays[i] - sampleMean, 2);
			}
			
			sampleStandardDeviation = (sampleStandardDeviation/(iceDaysSize - 1));
			
			System.out.println(String.format("%.2f", sampleStandardDeviation));
		}
		
		else if (flag == 300) {
			double arg1 = Double.valueOf(args[1]);
			double arg2 = Double.valueOf(args[2]);
			
			System.out.println(String.format("%.2f", meanSquaredError(arg1, arg2)));
		}
		
		else if (flag == 400) {
			double arg1 = Double.valueOf(args[1]);
			double arg2 = Double.valueOf(args[2]);
			
			//Variable to keep track of years
			int year = 1855;
			
			//Calculates the gradient descent for both arg1 and arg2 and outputs it
			double gradientDescent1 = 0;
			double gradientDescent2 = 0;
			
			for (int i = 0; i < iceDaysSize; i++) {
				gradientDescent1 += (arg1 + arg2*year - iceDays[i]);
				gradientDescent2 += ((arg1 + arg2*year - iceDays[i])*year);
				year++;
			}
			
			gradientDescent1 = ((2*gradientDescent1)/iceDaysSize);
			
			gradientDescent2 = ((2*gradientDescent2)/iceDaysSize);
			
			System.out.println(String.format("%.2f", gradientDescent1));
			System.out.println(String.format("%.2f", gradientDescent2));
		}
		
		else if (flag == 500) {
			double n = Double.valueOf(args[1]);
			double T = Double.valueOf(args[2]);
			
			//Calculates the gradient descent (no normalization)
			double arg1 = 0;
			double arg2 = 0;
			
			for(int i = 0; i < T; i++) {
				//Variable to keep track of years
				int year = 1855;
				
				//Calculates the gradient descent for both arg1 and arg2
				double gradientDescent1 = 0;
				double gradientDescent2 = 0;
				
				for (int j = 0; j < iceDaysSize; j++) {
					gradientDescent1 += (arg1 + arg2*year - iceDays[j]);
					gradientDescent2 += ((arg1 + arg2*year - iceDays[j])*year);
					year++;
				}
				
				arg1 -= ((n*2)/iceDaysSize)*gradientDescent1;
				
				arg2 -= ((n*2)/iceDaysSize)*gradientDescent2;
				
				//Outputs everything
				String output1 = String.format("%.2f", arg1);
				String output2 = String.format("%.2f", arg2);
				String output3 = String.format("%.2f", meanSquaredError(arg1, arg2));
				
				System.out.println((i+1) + " " + output1 + " " + output2 + 
									" " + output3);
			}
		}
		
		else if (flag == 600) {
			//Variables for mean of years and mean if days
			double[] tempArray = yearsDaysMean();
			double yearsMean = tempArray[0];
			double daysMean = tempArray[1];
			
			//Linear Least Squares
			double[] tempArrayB = linearLeastSquares(yearsMean, daysMean);
			double beta0 = tempArrayB[0];
			double beta1 = tempArrayB[1];
			
			//Outputs everything
			String output1 = String.format("%.2f", beta0);
			String output2 = String.format("%.2f", beta1);
			String output3 = String.format("%.2f", meanSquaredError(beta0, beta1));
			
			System.out.println(output1 + " " + output2 + " " + output3);
		}
		
		else if (flag == 700) {
			int yearArg = Integer.valueOf(args[1]);
			
			//Mean of years and mean of days
			double[] tempArrayA = yearsDaysMean();
			double yearsMean = tempArrayA[0];
			double daysMean = tempArrayA[1];
			
			//Linear Least Squares
			double[] tempArrayB = linearLeastSquares(yearsMean, daysMean);
			double beta0 = tempArrayB[0];
			double beta1 = tempArrayB[1];
			
			//Outputs days
			double days = beta0 + beta1*yearArg;
			System.out.println(String.format("%.2f", days));
		}
		
		else if (flag == 800) {
			double n = Double.valueOf(args[1]);
			double T = Double.valueOf(args[2]);
			
			//Normalizes years
			double[] yearsNormalized = yearsNormalized();
			
			//Calculates the gradient descent (with normalization)
			double arg1 = 0;
			double arg2 = 0;
			
			for(int i = 0; i < T; i++) {
				//Calculates the gradient descent for both arg1 and arg2
				double gradientDescent1 = 0;
				double gradientDescent2 = 0;
				
				for (int j = 0; j < iceDaysSize; j++) {
					gradientDescent1 += (arg1 + arg2*yearsNormalized[j] - 
										iceDays[j]);
					gradientDescent2 += ((arg1 + arg2*yearsNormalized[j] - 
										iceDays[j])*yearsNormalized[j]);
				}
				
				arg1 -= ((n*2)/iceDaysSize)*gradientDescent1;
				
				arg2 -= ((n*2)/iceDaysSize)*gradientDescent2;
				
				//Calculates mean squared error with normalization
				double MSE = meanSquaredError(arg1, arg2, yearsNormalized);
				
				//Outputs everything
				String output1 = String.format("%.2f", arg1);
				String output2 = String.format("%.2f", arg2);
				String output3 = String.format("%.2f", MSE);
				
				System.out.println((i+1) + " " + output1 + " " + output2 + 
									" " + output3);
			}
		}
		
		else if (flag == 900) {
			double n = Double.valueOf(args[1]);
			double T = Double.valueOf(args[2]);
			
			//Normalizes years
			double[] yearsNormalized = yearsNormalized();
			
			//Calculates the gradient descent (with normalization and random seed)
			double arg1 = 0;
			double arg2 = 0;
			Random rng = new Random();
			
			for(int i = 0; i < T; i++) {
				//Calculates the gradient descent for both arg1 and arg2
				double gradientDescent1 = 0;
				double gradientDescent2 = 0;
				
				//Random number
				int random = rng.nextInt(162);
				
				gradientDescent1 = (arg1 + arg2*yearsNormalized[random] - 
									iceDays[random]);
				gradientDescent2 = ((arg1 + arg2*yearsNormalized[random] - 
									iceDays[random])*yearsNormalized[random]);
				
				arg1 -= (n*2)*gradientDescent1;
				
				arg2 -= (n*2)*gradientDescent2;
				
				//Calculates mean squared error with normalization
				double MSE = meanSquaredError(arg1, arg2, yearsNormalized);
				
				//Outputs everything
				String output1 = String.format("%.2f", arg1);
				String output2 = String.format("%.2f", arg2);
				String output3 = String.format("%.2f", MSE);
				
				System.out.println((i+1) + " " + output1 + " " + output2 + 
									" " + output3);
			}
		}
		
	}
	
	
	/**
	 * Class that calculates the mean squared error with no normalization
	 */
	private static double meanSquaredError(double arg1, double arg2) {
		double meanSquaredError = 0;
		int year = 1855;
		
		for (int i = 0; i < iceDaysSize; i++) {
			meanSquaredError += Math.pow(arg1 + arg2*year - iceDays[i], 2);
			year++;
		}
		
		return (meanSquaredError/iceDaysSize);
	}
	
	/**
	 * Class that calculates the mean squared error with normalization
	 */
	private static double meanSquaredError(double arg1, double arg2, 
											double yearsNormalized[]) {
		double meanSquaredError = 0;
		
		for (int i = 0; i < iceDaysSize; i++) {
			meanSquaredError += Math.pow(arg1 + arg2*yearsNormalized[i] - 
										iceDays[i], 2);
		}
		
		return (meanSquaredError/iceDaysSize);
	}
	
	/**
	 * Class that calculates the mean of years and days
	 */
	private static double[] yearsDaysMean() {
		//Array {yearsMean, daysMean}
		double[] yearsDaysMean = {0, 0};
		int year = 1855;
		
		for(int i = 0; i < iceDaysSize; i++) {
			yearsDaysMean[0] += year;
			yearsDaysMean[1] += iceDays[i];
			year++;
		}
		
		yearsDaysMean[0] = yearsDaysMean[0]/iceDaysSize;
		yearsDaysMean[1] = yearsDaysMean[1]/iceDaysSize;
		
		return yearsDaysMean;
	}
	
	/**
	 * Class that calculates the linear least squares for beta 0 and beta 1
	 */
	private static double[] linearLeastSquares(double yearsMean, double daysMean) {
		//Array {beta0, beta1}
		double[] linearLeastSquares = {0, 0};
		int year = 1855;
		
		double sumOne = 0;
		double sumTwo = 0;
		
		for(int i = 0; i < iceDaysSize; i++) {
			sumOne += (year - yearsMean)*(iceDays[i] - daysMean);
			sumTwo += Math.pow(year - yearsMean, 2);
			year++;
		}
		
		linearLeastSquares[1] = sumOne/sumTwo;
		linearLeastSquares[0] = daysMean - linearLeastSquares[1]*yearsMean;
		
		return linearLeastSquares;
	}
	
	/**
	 * Class that normalizes the value of years
	 */
	private static double[] yearsNormalized() {
		//Calculates mean of years
		double[] tempArrayA = yearsDaysMean();
		double yearsMean = tempArrayA[0];
		
		//Calculates standard deviation of years
		double yearsStandDev = 0;
		int year = 1855;
		
		for(int i = 0; i < iceDaysSize; i++) {
			yearsStandDev += Math.pow(year - yearsMean, 2);
			year++;
		}
		
		yearsStandDev = Math.sqrt(yearsStandDev/(iceDaysSize - 1));
		
		//Normalizes each year
		double [] yearsNormalized = new double[iceDaysSize];
		year = 1855;
		
		for(int j = 0; j < iceDaysSize; j++) {
			yearsNormalized[j] = (year - yearsMean)/yearsStandDev;
			year++;
		}
		
		return yearsNormalized;
	}
}
