/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             GradeEstimator.java
//
// Authors: Jiang, Kyle, Pedro Henrique, Tushar Narang, and Zachary.
// Author 1: Jiang Huiyu, hjiang94@wisc.edu, hjiang94, 001
// Author 2: Kyle Malinowski, kmalinowski2@wisc.edu, kmalinowski2, 001
// Author 3: Pedro Henrique Koeler Goulart, koelergoular@wisc.edu, koelergoular, 001
// Author 4: Tushar Narang, tnarang@wisc.edu, tnarang, 001
// Author 5: Zachary Lesavich, zlesavich@wisc.edu, zlesavich, 001
//
// ---------------- OTHER ASSISTANCE CREDITS 
// Persons: NA
//
// Online sources: NA
//////////////////////////// 80 columns wide //////////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**  CLASS HEADER
 * To compute the final grade and print out the grade report of a student
 * for a given file containing grading information for a particular class
 *
 * Bugs: none known
 *
 * @author       Jiang, Kyle, Pedro, Tushar, and Zachary. 
 * @version      1.1
 * @see also     GradeFileFormatException.java, ScoreIteratorADT.java, 
 * ScoreIterator.java
 */

public class GradeEstimator {
	//Arrays to hold file format information 
	private String[] letterGrades;    
	private double[] minimumThresholds;
	private String[] categoryNames;
	private double[] categoryWeights;
	//Contains the student's scores for the particular class 
	private ScoreList scoreList;
	
	/** 
	 * Prints the grade report for a student based on the file inputed
	 * @param command line argument containing file name 
	 * @throws FileNotFoundException, GradeFileFormatExcpetion
	 */
	public static void main(String[] args) throws FileNotFoundException, 
											GradeFileFormatException {
		
		GradeEstimator mGradeEstimator;
		//If no argument or multiple arguments are sent
		//print the default message 
		if (args.length != 1) {
			System.out.println(Config.USAGE_MESSAGE);
			
			//Create a new default instance of GradeEstimator
			mGradeEstimator = new GradeEstimator();
			//Generate the estimate to be displayed
			mGradeEstimator.getEstimateReport();
		}
		else {
			try {
				//Otherwise, create the GradeEstimator from the specified file.
				mGradeEstimator = createGradeEstimatorFromFile(args[0]);
				//And generate the estimate.
				System.out.println(mGradeEstimator.getEstimateReport());
			}
			catch (FileNotFoundException e) {
				//In the case the specified file cannot be found.
				System.out.println(e.toString());
			}
			
			catch (GradeFileFormatException e) {
				//In the case the file is found but there is an issue with the formatting.
				System.out.println("GradeFileFormatException");	
			}
		}
	}
	/** 
	 * Default constructor in case no definite file is found 
	 * @param none
	 **/
	public GradeEstimator() {
		this(Config.GRADE_LETTER, 
			 Config.GRADE_THRESHOLD, 
			 Config.CATEGORY_KEY, 
			 Config.CATEGORY_WEIGHT, new ScoreList());
	}
	/** 
	* Constructor that creates an instance based on the information retrieved 
	* from the file 
	* @param arrays of letter grades, and it's corresponding threshold
	* 		 arrays of different type of assignments, and it's associated 
	* 		 weight in class 
	**/ 
	public GradeEstimator(String[] letterGrades, double[] minimumThresholds, 
							String[] categoryNames, double[] categoryWeights, 
							ScoreList scoreList) {
		this.letterGrades = letterGrades;
		this.minimumThresholds = minimumThresholds;
		this.categoryNames = categoryNames;
		this.categoryWeights = categoryWeights;
		this.scoreList = scoreList;
	}
	/** 
	 * @param file name 
	 * @returns a GradeEstimator instance with grading information instances as
	 * recorded from the file inputed
	 * @throws FileNotFoundException, GradeFileFormatExcpetion
	 */
	public static GradeEstimator createGradeEstimatorFromFile(String gradeInfo)
			throws FileNotFoundException, GradeFileFormatException {
		//Arrays containing letter grades, and assignment names for the class
		String[] letter, catNames;
		//Arrays containing corresponding grades for letter and associated 
		//Weights of assignments
		double[] threshold, catWeights;
		
		File file = new File(gradeInfo);
		Scanner scanFile = new Scanner(file);
		
		try {
			//Get the letter grades.
			letter = scanFile.nextLine().split("#")[0].trim().split(" ");
			
			//Get the thresholds
			String[] tempThresholds = 
					scanFile.nextLine().split("#")[0].trim().split(" ");
			threshold = new double[tempThresholds.length];
			
			//Parse the thresholds received to usable numbers
			for(int i = 0; i < tempThresholds.length; i++) {
					if(tempThresholds[i].split("#").length > 1) {
						tempThresholds[i] = tempThresholds[i].split("#")[0];
					}
					threshold[i] = Double.parseDouble(tempThresholds[i].trim());
			}
			//Get the category names.
			catNames = scanFile.nextLine().split("#")[0].trim().split(" ");
				
			//Get their corresponding weights
			String[] tempWeights = 
					scanFile.nextLine().split("#")[0].trim().split(" ");
			catWeights = new double[tempWeights.length];
				
			//Parse the weights received to usable numbers
			for(int i = 0; i < tempWeights.length; i++) {
				if(tempWeights[i].split("#").length > 1) {
					tempWeights[i] = tempWeights[i].split("#")[0];
				}
				catWeights[i] = Double.parseDouble(tempWeights[i].trim());
			}
			ScoreList listOfScores = new ScoreList();
		
			//Reads the scores and adds them to the listOfScores
			while (scanFile.hasNextLine()) {
				//Reads the line of scanFile and splits at the #.
				//Splits once more by spaces.
				String[] temp = scanFile.nextLine().split("#")[0].split(" ");
				
				//Getting the instances for score of corresponding assignment
				String names = temp[0].trim();
				Double points = Double.parseDouble(temp[1].trim());
				Double maxPossible = Double.parseDouble(temp[2].trim());
				
				listOfScores.add(new Score(names, points, maxPossible));
			}
			
			scanFile.close();
			//In case the number of grades and assignments don't match with 
			//their corresponding thresholds and weights
			if ((letter.length != threshold.length)
					|| (catNames.length != catWeights.length)){
				throw new GradeFileFormatException();
			}
			return new GradeEstimator(letter, threshold, catNames, 
										catWeights, listOfScores);
		} 
		//In case file format informations are swapped, parseDouble
		//will throw an error or in case formatting of the points 
		//of the score isn't valid  Score() will throw an error
	    //which is caught and thrown as a GradeFileFormatException
		catch (Exception e) {
			scanFile.close();
			throw new GradeFileFormatException();
		}
	}
	/** 
	 * @param none
	 * @returns grade report of the student 
	 * for corresponding grading information 
	 */
	public String getEstimateReport() {
		String estimateReport = ""; //report to be returned 
		int numberOfScores = 0; 
		double averageScoreSum;
		double weightedAverageScore; //Score out of Weighted limit
		double unweightedAverageScore; //Score out of 100
		double totalWeightedPercent = 0.0; //Final grade 
		String letterGradeEstimate = ""; 
		
		ScoreIterator scoreIterator;
		
		//Gets the name and the percentage of the individual scores in the list
		for (int i = 0; i < categoryNames.length; i++) {
			scoreIterator = new ScoreIterator(scoreList, categoryNames[i]);

			while (scoreIterator.hasNext()) {
				Score nameAndGrade = scoreIterator.next();
				estimateReport += String.format(nameAndGrade.getName()
									+ "%7.2f \n", nameAndGrade.getPercent());
				numberOfScores++;
			}
			
		}
		
		//Reports the number of scores the grade estimate is based on
		estimateReport += "Grade estimate is based on " + numberOfScores 
							+ " scores \n";
		
		//Performs the calculations and adds them to the report
		for (int i = 0; i < categoryNames.length; i++) {

			scoreIterator = new ScoreIterator(scoreList, categoryNames[i]);
			//Used to calculate the individual weights, and final grade weight
			int count = 0;
			averageScoreSum = 0.0;
			weightedAverageScore = 0.0;
			
			//Individual weight of corresponding assignment 
			while (scoreIterator.hasNext()) {
				Score temp = scoreIterator.next();
				if (temp.getCategory().charAt(0) == categoryNames[i].charAt(0)) {
					averageScoreSum += temp.getPercent();
					count++;
				}
			}
			weightedAverageScore = ((averageScoreSum / count) 
										* categoryWeights[i]) / 100;
			unweightedAverageScore = (averageScoreSum / count);
			
			totalWeightedPercent += weightedAverageScore;
			
			estimateReport += String.format("%7.2f%% =  %5.2f%% of %2.0f%% for "
							+ categoryNames[i] + "\n", weightedAverageScore,
								unweightedAverageScore, categoryWeights[i]);
		}
		
		estimateReport += "-------------------------------- \n";
		estimateReport += String.format("%7.2f%% weighted percent \n", 
													totalWeightedPercent);
		
		//Calculates the grade estimate and assigns letter grade for the class
		for (int i = 0; i < minimumThresholds.length; i++) {
			if (totalWeightedPercent >= minimumThresholds[i]) {
				letterGradeEstimate = letterGrades[i];
				break;
			}
			if (i == minimumThresholds.length - 1) {
				letterGradeEstimate = "unable to estimate letter grade for "
													+ totalWeightedPercent;
				break;
			}
		}
		
		estimateReport += "Letter Grade Estimate: " + letterGradeEstimate;
		
		return estimateReport;
	}
}

