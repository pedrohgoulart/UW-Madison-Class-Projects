/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             ScoreList.java
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
 * A class that implements ScoreListADT and performs the functions for it. This
 * class can also store Score instances.
 *
 * <p>Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class ScoreList implements ScoreListADT {
	/** Array used to store Score instances */
	private Score[] scores;
	
	/** Used to keep track of Score[] array size */
	private int scoresSize;
	
	/** Constructor that initializes scores and scoresSize */
	ScoreList() {
		scores = new Score[100];
		scoresSize = 0;
	}
	
	/** 
	 * Returns the number of Scores in the list or zero
	 * @return the number of scores in this list
	 */
	public int size() {
		return scoresSize;
	}
	
	/** 
	 * Adds the score to the end of this list.
	 * 
	 * <p>PRECONDITIONS: s is assumed to be not null
	 * 
	 * @param s a non-null Score to place as the last item in the list.
	 * @throws IllegalArgumentException
	 */
	public void add(Score s) throws IllegalArgumentException {
		//Checks for exception before proceeding
		if (s == null) 
			throw new IllegalArgumentException();
		
		//Checks if Array needs to be expanded
		if(scoresSize == scores.length) {
			expandArray();
		}
		
		//Adds Score to Array and increments scoresSize
		scores[scoresSize] = s;
		scoresSize++;
	}
	
	/**
	 * Removes and returns the item at index position i.
	 * If i is less than zero or greater than size()-1,
	 * will throw an IndexOutOfBoundsException.
	 * 
	 * <p>PRECONDITIONS: i is assumed to be between 0 and size()-1
	 * 
	 * @param i must be greater than or equal to zero and less than size()
	 * @return the item at index i
	 * @throws IndexOutOfBoundsException
	 */
	public Score remove(int i) throws IndexOutOfBoundsException {
		//Checks for exception before proceeding
		if (i < 0 || i > (scores.length - 1)) 
			throw new IndexOutOfBoundsException();
		
		//Keeps track of removedScore to be returned later
		Score removedScore = scores[i];
		
		//Removes score and changes all following scores to the left of Array.
		for (int j = i + 1; j < scoresSize; j++) {	
			scores[j-1] = scores[j];
		}
		
		//Sets last score in Array to null and reduces scoresSize
		scores[scoresSize - 1] = null;
		scoresSize--;
			
		return removedScore;
	}
	
	/**
	 * Returns (without removing) the item at index position i.
	 * If i is less than zero or greater than size()-1,
	 * will throw an IndexOutOfBoundsException.
	 * 
	 * <p>PRECONDITIONS: i is assumed to be between 0 and size()-1
	 * 
	 * @param i must be greater than or equal to zero and less than size()
	 * @return the item at index i
	 * @throws IndexOutOfBoundsException
	 */
	public Score get(int i) {
		//Checks for exception before proceeding
		if (i < 0 || i > (scoresSize - 1)) 
			throw new IndexOutOfBoundsException();
		
		return scores[i];	
	}
	
	/** Doubles the size of scores Array. */
	private void expandArray() {
		//Creates a tempArray with double the size of scores Array.
		Score[] tempArray = new Score[scores.length * 2];
		
		//Copies all scores data to tempArray.
		for(int i = 0; i < scoresSize; i++) {	
			tempArray[i] = scores[i];	
		}
		
		//Changes the tempArray reference to scores.
		scores = tempArray;	
	}
	
}
