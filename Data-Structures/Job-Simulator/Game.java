/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p2
// FILE:             Game.java
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
 * A class that implements the basic functionality of a game 
 *
 * Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class Game {
	
    /**
     * A list of all jobs currently in the queue.
     */
    private ListADT<Job> listOfJobs;
    /**
     * Whenever a Job is completed it is added to the score board
     */
    private ScoreboardADT scoreBoard;
    /**
     * Current time left for the game to finish 
     */
    private int timeToPlay;
    /**
     * A simple job generator for the game 
     */
    private JobSimulator jobSimulator;

    /**
     * Constructor. Initializes all variables.
     * @param seed
     * seed is used to seed the random number generator in the Jobsimulator 
     * class.
     * @param timeToPlay
     * duration used to determine the length of the game.
     */
    public Game(int seed, int timeToPlay) {
    	this.listOfJobs = new JobList();
    	this.scoreBoard = new Scoreboard();
        this.timeToPlay = timeToPlay;
        this.jobSimulator = new JobSimulator(seed);
    }

    /**
     * Returns the amount of time currently left in the game.
     * @returns timeToPlay - the amount of time left in the game.
     */
    public int getTimeToPlay() {
        return timeToPlay;
    }
    
    /**
     * Sets the amount of time that the game is to be executed for.
     * Can be used to update the amount of time remaining.
     * @param timeToPlay
     *        the remaining duration of the game
     */
    public void setTimeToPlay(int timeToPlay) {
        this.timeToPlay = timeToPlay;
    }

    /**
     * States whether or not the game is over yet.
     * @returns true if the amount of time remaining in
     * the game is less than or equal to 0,
     * else returns false
     */
    public boolean isOver() {
        if (timeToPlay <= 0) {
        	return true;
        }
        else {
        	return false;
        }
    }
    /**
     * This method simply invokes the simulateJobs method
     * in the JobSimulator object.
     */
    public void createJobs() {
    	jobSimulator.simulateJobs(listOfJobs, timeToPlay);
    }

    /**
     * @returns listOfJobs - the length of the Joblist.
     */
    public int getNumberOfJobs() {
    	return listOfJobs.size();
    }

    /**
     * Adds a job to a given position in the joblist.
     * Also requires to calculate the time Penalty involved in
     * adding a job back into the list and update the timeToPlay
     * accordingly
     * @param pos
     *      The position that the given job is to be added to in the list.
     * @param item
     *      The job to be inserted in the list.
     */
    public void addJob(int pos, Job item) {
    	if(pos >= 0 && pos <= listOfJobs.size()) {
    		timeToPlay -= pos;
    		listOfJobs.add(pos, item);
    	}
    	else{
    		timeToPlay -= listOfJobs.size();
    		listOfJobs.add(item);
    	}
    }
    
    /**
     * Adds a job to the joblist.
     * @param item - The job to be inserted in the list.
     */
    public void addJob(Job item) {
        listOfJobs.add(item);
    }

    /**
     * Given a valid index and duration,
     * executes the given job for the given duration.
     *
     * This function should remove the job from the list and
     * return it after applying the duration.
     *
     * This function should set duration equal to the
     * amount of time remaining if duration exceeds it prior
     * to executing the job.
     * After executing the job for a given amount of time,
     * check if it is completed or not. If it is, then
     * it must be inserted into the scoreBoard.
     * This method should also calculate the time penalty involved in
     * executing the job and update the timeToPlay value accordingly
     * @param index
     *      The job to be inserted in the list.
     * @param duration
     *      The amount of time the given job is to be worked on for.
     */
    public Job updateJob(int index, int duration) throws 
    							IndexOutOfBoundsException, 
    							IllegalArgumentException {
    	//Checks for valid index and duration
    	if (index >= getNumberOfJobs() || index < 0) {
 	    	throw new  IndexOutOfBoundsException();
 	    }
    	if (duration < 0) {
    		throw new IllegalArgumentException();
    	}
    	
    	//Decreases time to play according to chosen index (time penalty)
    	timeToPlay -= index;
    	 
    	//Prevents user from using more time than available
    	if(duration > timeToPlay) {
    		duration = timeToPlay;
    	}
    	
    	//Removes job chosen from the list and sets it to currJob
		Job currJob = listOfJobs.remove(index);
		
		//Updates the current steps needed to complete the job
		int remainingsteps = currJob.getTimeUnits() - currJob.getSteps();
		currJob.setSteps(currJob.getSteps() + duration);
		
		/*
	     * If user enters more time than it is required to complete the job,
	     * only the time needed to complete the job is used.
	     */
		if(currJob.getSteps() > currJob.getTimeUnits()) {
			currJob.setSteps(currJob.getTimeUnits());
			duration = remainingsteps;
		}
		
		//Adds chosen job to ScoreBoard if it is completed
		if(currJob.isCompleted()) {
			scoreBoard.updateScoreBoard(currJob);
		}
	
		//Decreases time to play according to the duration of the job
		timeToPlay -= duration;
		
		return currJob;
    }

    /**
     * This method produces the output for the initial Job Listing, IE:
     * "Job Listing
     *  At position: job.toString()
     *  At position: job.toString()
     *  ..."
     *
     */
    public void displayActiveJobs() {
    	System.out.println("Job Listing");
    	for (int i = 0; i < getNumberOfJobs(); i++) {
    		System.out.println("At position: " + i + " " + 
    							listOfJobs.get(i).toString());
    	}
    }
    
    /**
     * This function simply invokes the displayScoreBoard method in the
     * ScoreBoard class.
     */
    public void displayCompletedJobs(){
        scoreBoard.displayScoreBoard();
    }

    /**
     * This function simply invokes the getTotalScore method of the ScoreBoard
     * class.
     * @return the value calculated by getTotalScore
     */
    public int getTotalScore(){
        return scoreBoard.getTotalScore();
    }
}