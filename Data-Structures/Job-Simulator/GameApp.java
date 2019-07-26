/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p2
// FILE:             GameApp.java
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

import java.util.Scanner;

/**
 * A class that implements the front end of a game
 *
 * Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class GameApp{
    /**
     * Scanner instance for reading input from console
     */
    private static final Scanner STDIN = new Scanner(System.in);

    /**
     * Game variable to store game class information.
     */
	private static Game game;
	
    /**
     * Constructor for instantiating game class
     * @param seed: Seed value as processed in command line
     * @param timeToPlay: Total time to play from command line
     */
    public GameApp(int seed, int timeToPlay){
        game = new Game(seed, timeToPlay);
    }

    /**
     * Main function which takes in command line arguments and instantiates
     * the GameApp class.
     * It terminates when the game ends.
     * Uses the getIntegerInput function to read inputs from console
     *
     * @param args: Command line arguments <seed> <timeToPlay>
     */
    public static void main(String[] args){
    	//Welcome message
        System.out.println("Welcome to the Job Market!");

        //Takes the  input from the command line, and handles it accordingly  
        try {
        	//If there are not exactly two arguments
        	if(args.length != 2) {
        		throw new IllegalArgumentException();
        	}
        	//If the timeToPlay is not 1 or greater
        	if(Integer.parseInt(args[0]) < 1 || Integer.parseInt(args[1]) < 1) {
        		throw new NumberFormatException();
        	}
        	
        	GameApp gameApp = new GameApp(Integer.parseInt(args[0]), 
        									Integer.parseInt(args[1]));
        	
        	gameApp.start(); //Game loop
        	
        	System.out.println("Game Over!");
        	System.out.print("Your final score: " + game.getTotalScore());
        }
        catch (NumberFormatException e) {
        	System.out.println("ERROR: One or more supplied arguments" 
        											+ " are invalid.");
        }
        catch (IllegalArgumentException e) {
        	System.out.println("ERROR: Necessary arguments are missing.");
        }

        
    }

    /**
     * The game continuously goes on while there is enough time offering
     * the user to work on a particular job each time in an attempt to 
     * maximize their score 
     */
    private void start(){
    	
    	while(!game.isOver()) {
    		//Time left in the game
    		System.out.println("You have " + game.getTimeToPlay() + 
    											" left in the game!");
    		//Creates jobs and adds them to the list
    		game.createJobs();
    		
    		//Job listing
        	game.displayActiveJobs();
            System.out.println(""); 
            
            //User end of the game 
    		try { 
    			int index = getIntegerInput("Select a job to work on: ");
        	    
                int duration = getIntegerInput("For how long would you like "
                								+ "to work on this job?: ");
                
    			Job job = game.updateJob(index, duration);
    			
    			//If job isn't completed, ask user to reposition it on the list
    			if(!job.isCompleted()){
                	int pos = getIntegerInput("At what position would you " 
                			+ "like to insert the job back into the list?\n");
                	game.addJob(pos, job);
                }
            	else {
            		//For jobs completed
                	System.out.println("Job completed! Current Score: " + 
                									game.getTotalScore());
                	System.out.println("Total Score: " + game.getTotalScore());
                	game.displayCompletedJobs(); 
                } 
    		}
    		catch (IndexOutOfBoundsException e) {
    			//Shows error message, sets time to 0 and exists the while loop
    			System.out.println("ERROR: The job or the position you chose "
    											+ "does not exist.");
    			game.setTimeToPlay(0);
    		} 
    		catch (IllegalArgumentException e) {
    			//Shows error message, sets time to 0 and exists the while loop
    			System.out.println("ERROR: The number of steps for the job you"
    													+ " chose is invalid.");
    			game.setTimeToPlay(0);
    		}
    	}
    	
    }

    /**
     * Displays the prompt and returns the integer entered by the user
     * to the standard input stream.
     *
     * Does not return until the user enters an integer.
     * Does not check the integer value in any way.
     * @param prompt The user prompt to display before waiting for this 
     * integer.
     */
    public static int getIntegerInput(String prompt) {
        System.out.print(prompt);
        while ( ! STDIN.hasNextInt() ) {
            System.out.print(STDIN.next() + 
            		" is not an int.  Please enter an integer: ");
        }
        return STDIN.nextInt();
    }
}