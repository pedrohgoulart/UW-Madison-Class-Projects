/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p2
// FILE:             Scoreboard.java
//
// Authors: Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
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

/**
 * This class contains the class definition of Scoreboard that displays
 * the jobs completed, and points earned of a user in a game. 
 * 
 *  Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 * 
 */
public class Scoreboard implements ScoreboardADT {
	private ListADT<Job> scoreBoard;
	
	public Scoreboard(){
		this.scoreBoard = new JobList();
	}
	
	/**
     * Calculates the total combined number of points for 
     * 											every job in the scoreboard.
     * 
     * @return The summation of all the points for every job
     * 									 currently stored in the scoreboard.
     */
	public int getTotalScore() {
		int score = 0;
		for (int i = 0; i < scoreBoard.size(); i++) {
			score += scoreBoard.get(i).getPoints();
		}
		return score;
	}
	
	/**
     * Inserts the given job at the end of the scoreboard.
     * 
     * @param job 
     * 		The job that has been completed and 
     * 							is to be inserted into the list.
     */
	public void updateScoreBoard(Job job) {
		scoreBoard.add(job);
	}
	
	/**
     * Prints out a summary of all jobs currently stored in the scoreboard,
     * 											following the specified format  
     */
	public void displayScoreBoard() {
		System.out.println("The jobs completed: ");
		for (int i = 0; i < scoreBoard.size(); i++) {
			System.out.println("Job name: " + scoreBoard.get(i).getJobName());
			System.out.println("Points earned for this job:  " + 
								scoreBoard.get(i).getPoints());
			System.out.println("--------------------------------------------");
		}
	}
}
