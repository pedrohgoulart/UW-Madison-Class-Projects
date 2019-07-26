import java.util.Comparator;
/////////////////////////////////////////////////////////////////////////////
//Semester:         CS367 Spring 2017 
//PROJECT:          p3
//FILE:             FileLinePriorityQueue.java
//
//Authors: Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
//Author 1: Jiang Huiyu, hjiang94@wisc.edu, hjiang94, 001
//Author 2: Kyle Malinowski, kmalinowski2@wisc.edu, kmalinowski2, 001
//Author 3: Pedro Henrique Koeler Goulart, koelergoular@wisc.edu, koelergoular, 001
//Author 4: Tushar Narang, tnarang@wisc.edu, tnarang, 001
//Author 5: Zachary Lesavich, zlesavich@wisc.edu, zlesavich, 001
//
//---------------- OTHER ASSISTANCE CREDITS 
//Persons: NA
//
//Online sources: NA
////////////////////////////80 columns wide //////////////////////////////////
/**
 * An implementation of the MinPriorityQueueADT interface. This implementation stores FileLine objects.
 * See MinPriorityQueueADT.java for a description of each method. 
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class FileLinePriorityQueue implements MinPriorityQueueADT<FileLine> {

	//Initialize local variables for use n the priority queue
	private FileLine[] queue;
    private int maxSize; 
    private int numItems;
    Comparator<FileLine> cmp;
    
    //We will want the item at index 0 empty
    

    public FileLinePriorityQueue(int initialSize, Comparator<FileLine> cmp) {
		this.cmp = cmp;
		maxSize = initialSize;
		numItems = 0;
		
		queue = new FileLine[initialSize + 1];
    }

    /**
     * Removes the item with the lowest priority from the priority queue.
     * 
     * @throws PriorityQueueEmxptyException
     */
    public FileLine removeMin() throws PriorityQueueEmptyException {
		
    	//Throw PriorityQueueEmptyException if the priority queue is empty.
    	if(numItems <= 0) throw new PriorityQueueEmptyException();
		
    	//Sets a variable to store the item with the lowest priority.
		FileLine hold = queue[1];
		
		//Move the last item to the top, and re-format the heap to follow the
		//correct structure.
		queue[1] = queue[numItems]; 
		queue[numItems] = null;
		numItems--;
		if (numItems > 1)
		{
			heapify(1);
		}
		
		//Return the item with the lowest priority that was stored at the
		//beginning.
		return hold;
		
    }
    
    /**
     * Reorganize the heap to follow the proper tree structure.
     * 
     * @param root - The index of the root item.
     */
    private void heapify(int root) //Used only for remove method.
    {
    	
    	//Pulls the item at the root, and makes comparisons to re-organize the
    	//heap to correctly sort the queue.
    	FileLine last = queue[root];
    	int child, k = root;
    	while (2*k <= numItems)
    	{
    		child = 2*k;
    		if(child < numItems && cmp.compare(queue[child], queue[child + 1]) > 0)
    		{
    			child++;
    		}
    		if(cmp.compare(last, queue[child]) <= 0)
    		{
    			break;
    		}
    		else
    		{
    			queue[k] = queue[child];
    			k = child;
    		}
    	}
    	queue[k] = last;
    }

    /**
     * Insert an item into the priority queue.
     * 
     * @param fl - A FileLine containing the item that should be added to the
     * priority queue.
     * @throws PriorityQueueFullException
     */
    public void insert(FileLine fl) throws PriorityQueueFullException {
		if(numItems == maxSize) throw new PriorityQueueFullException();
		
		numItems++;

		queue[numItems] = fl;
		int k = numItems;
		
		//reheapifying 
		while (k > 1 && cmp.compare(queue[k/2], fl) > 0)
		{
			queue[k] = queue[k/2];
			k /= 2;
		}
		queue[k] = fl;
    }
    
    /**
     * Returns whether or not the queue is empty.
     * 
     * @return true if empty, false if the queue is not empty.
     */
    public boolean isEmpty() {
		return numItems == 0;
    }
}
