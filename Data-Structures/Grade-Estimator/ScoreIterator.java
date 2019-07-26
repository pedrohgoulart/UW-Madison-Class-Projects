/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             ScoreIterator.java
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
 * A class that implements ScoreIteratorADT and performs the functions for it
 *
 * Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
import java.util.NoSuchElementException;

public class ScoreIterator implements ScoreIteratorADT {
	//List of Scores to traverse through
	private ScoreList scores;
	//Position of iterator in score list
	private int currentPos;
	//Category to be used for traversing and comparing from score list
	private String category;
	
	/** Constructor that initializes scoresIterator 
	 * @param a score list, and the category to be used for traversing  
	 **/
	public ScoreIterator(ScoreList score, String category) {
		this.scores = score;
		currentPos = 0;
		this.category = category;
	}
	
	/** 
	 * @return if score of corresponding category exists in the Score List 
	 */
	public boolean hasNext(){
		for (int i = currentPos; i < scores.size(); i++)
		{
			if(scores.get(i).getCategory().charAt(0) == category.charAt(0)) {
				currentPos = i;
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * @return the next score of given category in the Score List 
	 * @throws NoSuchElementException
	 */	
	public Score next(){
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		Score scoreNext = scores.get(currentPos);
	    currentPos++;
	    return scoreNext;

	}
}