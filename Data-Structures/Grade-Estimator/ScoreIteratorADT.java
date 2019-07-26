/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             ScoreIteratorADT.java
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
import java.util.NoSuchElementException;
/** 
 * An Iterator that traverses through a given Score List 
 */
public interface ScoreIteratorADT {
	/** 
	 * @return if score of corresponding category exists in the Score List 
	 */
	boolean hasNext();
	
	/** 
	 * @return the next score of given category in the Score List 
	 * @throws NoSuchElementException
	 */
	Score next() throws NoSuchElementException;
}
