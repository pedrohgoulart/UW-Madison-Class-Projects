/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p2
// FILE:             JobList.java
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

/**
 * This class contains the class definition for a list of jobs
 * implemented as a singly linked list 
 * 
 * Contains variables which define a singly linked list including
 *
 * numItems: The current number of active jobs in a list 
 *
 * head: A reference to the first job in the list 
 * 
 * Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class JobList implements ListADT<Job> 
{
	/** Gives a reference to the header node */
	private Listnode<Job> head;
	
	/** Used to keep track of number of items inside the ListNode */
	private int numItems;
	
	/**
     * Constructor for instantiating a list of Jobs
     * @param - none
     */
	public JobList() 
	{
		head = new Listnode<Job>(null);
		numItems = 0;
	}
	/** Adds a job to the end of the list
     * @param job Job to be added to the list
     * 
     * @throws IllegalArgumentException - if job is null
     * 
     */
	public void add(Job job) throws IllegalArgumentException {
		//Checks for exception before proceeding
		if (job == null) {
			throw new IllegalArgumentException();
		}
		
		//Adds a node to the end of ListNode
		Listnode<Job> newnode = new Listnode<Job>(job);
		Listnode<Job> curr = head;
		
		while (curr.getNext() != null) {
			curr = curr.getNext();
		}
		curr.setNext(newnode);
		numItems++;
	}
	
	/** Add a job at any position in the list
     * @param item - an item to be added to the list
     * 
     * @param pos - position at which the item must be added. Indexing starts
     * from 0.
     * @throws IllegalArgumentException if item is null
     * 
     * @throws IndexOutOfBoundsException if pos is less than 0 
     * 											or greater than size() - 1
     */
	public void add(int pos, Job job) throws IllegalArgumentException,
									IndexOutOfBoundsException {
		//Checks for exceptions before proceeding
		if (job == null) {
			throw new IllegalArgumentException();
		}
		if (pos < 0 || pos > numItems) {
			throw new IndexOutOfBoundsException();
		}
		
		//Adds a node to the ListNode at an exact position
		Listnode<Job> newnode = new Listnode<Job>(job);
		Listnode<Job> curr = head;
		for (int i = 0; i < pos; i++) {
			curr = curr.getNext();
		}
		
		newnode.setNext(curr.getNext());
		curr.setNext(newnode);
		numItems++;
	}
	
	/** Check if a particular job exists in the list
	 * 
    * @param job - the job to be checked for in the list
    * 
    * @return true if job exists, else false
    * 
    * @throws IllegalArgumentException if job is null
    */
	public boolean contains(Job job) throws IllegalArgumentException {
		//Checks for exception before proceeding
		if (job == null) {
			throw new IllegalArgumentException();
		}
		
		Listnode<Job> curr = head.getNext();
		for (int i = 0; i < numItems; i++) {
		
			//deep check to ensure each field of job matches
			if(curr.getData().getJobName().equals(job.getJobName()) &&
					curr.getData().getPoints() == job.getPoints() &&
					curr.getData().getTimeUnits() == job.getTimeUnits() &&
					curr.getData().getSteps() == job.getSteps())
				return true;
			
			curr = curr.getNext();
		}
		return false;
	}
	
	/** Returns the position of the job to return
	 * 
     * @param pos - position of the job to be returned
     * 
     * @throws IndexOutOfBoundsException
     *              if position is less than 0 or greater than size() - 1
     */
	public Job get(int pos) {
		//Checks for exception before proceeding
		if (pos < 0 || pos > numItems - 1) {
			throw new IndexOutOfBoundsException();
		}
		
		//Checks the content of a certain node on the ListNode
		Listnode<Job> curr = head;
		for (int i = 0; i <= pos; i++) {
			curr = curr.getNext();
		}
		return curr.getData();
	}
	
	/** Returns true if the list is empty
	 * 
     * @return value is true if the list is empty
     *              else false
     */
	public boolean isEmpty() {
		return numItems <= 0;
	}
	
	/** Removes the job at the given positions
     * @param pos - the position of the job to be deleted from the list
     * 
     * @return returns the job deleted
     * 
     * @throws IndexOutOfBoundsException
     *          if the pos value is less than 0 or greater than size() - 1
     */
	public Job remove(int pos) throws IndexOutOfBoundsException {
		//Checks for exception before proceeding
		if (pos < 0 || pos > numItems - 1) {
			throw new IndexOutOfBoundsException();
		}
		
		//Removes a node at a certain position from the ListNode
		Listnode<Job> curr = head;
		for (int i = 0; i < pos; i++) {
			curr = curr.getNext();
		}
		Listnode<Job> removeitem = curr.getNext();
		curr.setNext(curr.getNext().getNext());
		numItems--;
		
		return removeitem.getData();
	}
	
	/** Returns the size of the singly linked list
     * @return numItems - number of jobs in the list 
     */
	public int size() {
		return numItems;
	}
	
	/** Returns an direct access iterator of a JobList 
     * @return - an iterator based of the head node 
     * 						to be used to traverse the list
     */
	public Iterator<Job> iterator() {
		return new JobListIterator(head);
	}

}
