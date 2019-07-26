/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p2
// FILE:             JobListIterator.java
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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class that implements a direct access iterator for a job list
 *
 * Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class JobListIterator implements Iterator<Job> {
	private Listnode<Job> curr;
	
	/** Constructor that initializes JobListIterator 
	 * @param header node of joblist to be used for traversing  
	 **/
	JobListIterator(Listnode<Job> node) {
		curr = node;
	}
	
	/** 
	 * @return true if a job exists in the Job List 
	 * 				else false
	 */
	public boolean hasNext() {
		if (curr.getNext() != null)
			return true;
		return false;
	}
	
	/** 
	 * @return the next job in the Job List 
	 * @throws NoSuchElementException
	 */
	public Job next() throws NoSuchElementException {
		if (!hasNext())
			throw new NoSuchElementException();
		curr = curr.getNext();
		return curr.getData();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
