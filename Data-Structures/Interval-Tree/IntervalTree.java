import java.util.ArrayList;
import java.util.List;

/////////////////////////////////////////////////////////////////////////////
//Semester:         CS367 Spring 2017 
//PROJECT:          p4
//FILE:             IntervalTree.java
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
 * Main class that handles and controls the functions required by the
 * IntervalTree.
 * 
 * @author Jiang, Kyle, Pedro Henrique, Tushar, Zachary.
 *
 */
public class IntervalTree<T extends Comparable<T>> 
					implements IntervalTreeADT<T> {
	
	private IntervalNode<T> root;
	private int nodeCount = 0;
	
	/**
	 * Gets and returns the root node in the IntervalTree.
	 * 
	 * @return The root of the tree.
	 */
	@Override
	public IntervalNode<T> getRoot() {
		return root;
	}

	/**
	 * Insert an interval into the Interval tree.
	 * 
	 * @param a non-null object that extends IntervalADT of type T.
	 */
	@Override
	public void insert(IntervalADT<T> interval)
					throws IllegalArgumentException {
		//Checks if passed interval is null
		if (interval == null) {
			throw new IllegalArgumentException();
		}
		
		//Calls helper method to insert node
		root = insertHelper(root, interval);
		nodeCount++;
	}
	
	/**
	 * Helper method to insert an interval into the tree.
	 * 
	 * @param node - Current node that we are looking at, and comparing to.
	 * @param interval - the interval to add to the tree.
	 * 
	 * @return - new parent node.
	 */
	private IntervalNode<T> insertHelper (IntervalNode<T> node, 
													IntervalADT<T> interval) {
		//Checks if node passed is null
		if (node == null) {
			return new IntervalNode<T>(interval);
		}
		
		//Compares both the node and interval
		int compareIntervals = node.getInterval().compareTo(interval);
		
		//Checks if nodes are the same
		if (compareIntervals == 0) {
			throw new IllegalArgumentException();
		}
		
		//Updates Max End in case End of interval is bigger
		if(interval.getEnd().compareTo(node.getMaxEnd()) == 1){
			node.setMaxEnd(interval.getEnd());
		}
		
		//Checks what direction to go on the tree
		if (compareIntervals < 0) {
			node.setRightNode(insertHelper(node.getRightNode(),interval));
		}
		else if (compareIntervals > 0) {
			node.setLeftNode(insertHelper(node.getLeftNode(),interval));
		}
		
		return node;
	}

	/**
	 * Deletes the given node from the tree.
	 * 
	 * @param interval - the interval object that should be deleted.
	 */
	@Override
	public void delete(IntervalADT<T> interval)
				throws IntervalNotFoundException, IllegalArgumentException {
		//Checks if passed interval is null
		if (interval == null) {
			throw new IllegalArgumentException();
		}
		
		//Checks if method contains this interval
		if(!this.contains(interval)){
			throw new IntervalNotFoundException(interval.toString());
		}
		
		//Calls helper method to insert node
		root = deleteHelper(root, interval);
		nodeCount--;
	}

	/**
	 * Helper method to delete an object from the tree.
	 * 
	 * @param node - current node we are working in.
	 * @param interval - the interval to be deleted.
	 */
	@Override
	public IntervalNode<T> deleteHelper(IntervalNode<T> node, 
				IntervalADT<T> interval)
				throws IntervalNotFoundException, IllegalArgumentException {
		//Checks if node passed is null
		if (node == null) {
			return null;
		}
		
		//Compares both the node and interval
		int compareIntervals = node.getInterval().compareTo(interval);
		
		//Checks if nodes are the same
		if (compareIntervals == 0) {
			//Checks if node has no children
			if (node.getLeftNode() == null && node.getRightNode() == null) {
				return null;
			}
			
			//Checks if node has only left children
			if(node.getLeftNode() == null){
				node.setMaxEnd(node.getRightNode().getMaxEnd());
				return node.getRightNode();
			}
			
			//Checks if node has only right children
			else if(node.getRightNode() == null){
				node.setMaxEnd(node.getLeftNode().getMaxEnd());
				return node.getLeftNode();
			}
			
			//Checks if node has two children
			else {
				node.setInterval(node.getSuccessor().getInterval());
				node.setRightNode(deleteHelper(node.getRightNode(),
												node.getInterval()));
				node.setMaxEnd(recalculateMaxEnd(node));
			}
		}
		else if(compareIntervals < 0){
			node.setRightNode(deleteHelper(node.getRightNode(),interval));
			node.setMaxEnd(recalculateMaxEnd(node));
			return node;
		}
		else if (compareIntervals > 0){
			node.setLeftNode(deleteHelper(node.getLeftNode(),interval)); 
			node.setMaxEnd(recalculateMaxEnd(node));
			return node;
		}
		
		return null;
	}
	
	/**
	 * Calculates the maximum end value in the current node and it's children.
	 * 
	 * @param node - current node to find the max end of.
	 * @return value of the max end.
	 */
	private T recalculateMaxEnd(IntervalNode<T> node){
		//Checks if node passed is null
		if(node == null){
			return null;
		}
		
		//Variable that holds End of the node's interval
		T maxend = node.getInterval().getEnd();
		
		//Checks if left node is bigger than End of the node's interval
        if(node.getLeftNode() != null){
        	T left = node.getLeftNode().getMaxEnd();
        	
        	if(left.compareTo(maxend) == 1){
        		maxend = left;
        	}
        }
		
        //Checks if right node is bigger than End of the node's interval
        if(node.getRightNode() != null){
        	T right = node.getRightNode().getMaxEnd();
        	
        	if(right.compareTo(maxend) == 1){
        		maxend = right;
        	}
        }
        
		return maxend;
	}

	/**
	 * Finds if there are any intervals in the tree that overlap with the given
	 * interval
	 * 
	 * @param interval - The interval that we wish to see if there is an 
	 * 	overlapping object.
	 * @return List of intervals that overlap with the given interval.
	 */
	@Override
	public List<IntervalADT<T>> findOverlapping(
					IntervalADT<T> interval) {
		//Checks if passed interval is null
		if (interval == null) {
			throw new IllegalArgumentException();
		}
		
		List<IntervalADT<T>> list = new ArrayList<IntervalADT<T>>();
		findOverlappingHelper(root, interval, list);
		
		return list;
	}
	
	/**
	 * Helper method to find intervals that overlap with a given interval.
	 * 
	 * @param node - Current working node
	 * @param interval - Interval which we would like to find overlaps of.
	 * @param list - List of intervals that have already been found to overlap.
	 * 
	 */
	private void findOverlappingHelper(IntervalNode<T> node, 
						IntervalADT<T> interval, List<IntervalADT<T>> list) {
		//Checks if node passed is null
		if (node == null) {
			return;
		}
		
		//Checks if current node overlaps with interval
		if (node.getInterval().overlaps(interval)) {
			list.add(node.getInterval());
		}
		
		if (node.getLeftNode() != null) {
			//Compares left node's Max End with the start of interval
			T leftMaxEnd = node.getLeftNode().getMaxEnd();
			int leftNodeComparison = leftMaxEnd.compareTo(interval.getStart());
			
			//Only searches nodes that are lower than Max End
			if (leftNodeComparison >= 0) {
				findOverlappingHelper(node.getLeftNode(), interval, list);
			}
		}
		
		if (node.getRightNode() != null) {	
			//Compares right node's Max End with the start of interval
			T rightMaxEnd = node.getRightNode().getMaxEnd();
			int rightNodeComparison = rightMaxEnd.compareTo(interval.getStart());
		
			//Only searches nodes that are lower than Max End
			if (rightNodeComparison >= 0) {
				findOverlappingHelper(node.getRightNode(), interval, list);
			}
		}
	}

	/**
	 * Search the tree for intervals that the given point is contained within.
	 * 
	 * @param point - Point that we wish to find intervals that contain.
	 * @return List of intervals that contain the given point.
	 */
	@Override
	public List<IntervalADT<T>> searchPoint(T point) {
		//Checks if passed interval is null
		if (point == null) {
			throw new IllegalArgumentException();
		}
		
		List<IntervalADT<T>> list = new ArrayList<IntervalADT<T>>();
		searchPointHelper(root, point, list);
		
		return list;
	}
	
	/**
	 * Helper method to search through the tree and find intervals that contain
	 * 	a given point.
	 * @param node - Current working node.
	 * @param point - Point that we wish to find intervals that contain it.
	 * @param list - List of intervals already found that contain the point.
	 */
	private void searchPointHelper(IntervalNode<T> node, T point, 
												List<IntervalADT<T>> list) {
		//Checks if node is null
		if (node == null) {
			return;
		}
		
		//Checks if current node is within point interval
		if (node.getInterval().contains(point)) {
			list.add(node.getInterval());
		}
		
		//Searches all nodes of the tree for a match
		searchPointHelper(node.getLeftNode(), point, list);
		searchPointHelper(node.getRightNode(), point, list);
	}
	
	/**
	 * Gets and returns the amount of intervals in the interval tree.
	 * 
	 * @return amount of intervals in the interval tree.
	 */
	@Override
	public int getSize() {
		return nodeCount;
	}

	/**
	 * Gets and returns the height of the tree.
	 * 
	 * @return Height of thr tree.
	 */
	@Override
	public int getHeight() {
		return height(root);
	}
	
	/**
	 * Computes the height of the tree.
	 * 
	 * @param node - The node that we wish to find the height of the tree below
	 * it.
	 * @return Height of the tree below the given node.
	 */
	private int height(IntervalNode<T> node) {
		//Checks if tree is null
		if (node == null) {
			return 0;
		}
		
		//Calls recursive method to get height of both sides
		int heightOfTree = 0;
		int heightLeft = height(node.getLeftNode());
		int heightRight = height(node.getRightNode());

		//Compares results of both sides and returns bigger one
		if (heightLeft > heightRight) {
			heightOfTree = heightLeft + 1;
		}
		else {
			heightOfTree = heightRight + 1;
		}
		
		return heightOfTree;
	}

	/**
	 * Checks to see if the tree contains the passed in interval.
	 * 
	 * @param interval - The interval which we want to check and see if it is
	 * 	in the interval tree.
	 * @return true if the passed in interval is in the tree, false if not.
	 */
	@Override
	public boolean contains(IntervalADT<T> interval) {
		//Checks if passed interval is null
		if (interval == null) {
			throw new IllegalArgumentException();
		}
		
		return containsHelper(root, interval);
	}
	
	/**
	 * Helper method to determine if interval is in the interval tree.
	 * 
	 * @param node - Current working node.
	 * @param interval - The interval which we want to check and see if it is
	 * 	in the interval tree.
	 * @return true if the passed in interval is in the tree, false if not.
	 */
	private boolean containsHelper(IntervalNode<T> node, 
												IntervalADT<T> interval) {
		//Checks if node passed is null
		if (node == null) {
			return false;
		}
		
		//Compares both the node and interval
		int compareIntervals = node.getInterval().compareTo(interval);
		
		//Compares both the node and interval
		if (compareIntervals == 0) {
			return true;
		}
		else if (compareIntervals < 0) {
			return containsHelper( node.getRightNode(), interval);
		}
		else if (compareIntervals > 0) {
			return containsHelper(node.getLeftNode(), interval);
		}
		
		return false;
	}

	/**
	 * Prints the height and size of the interval tree.
	 */
	@Override
	public void printStats() {
		System.out.println("-----------------------------------------");
		System.out.println("Height:" + getHeight());
		System.out.println("Size:" + getSize());
		System.out.println("-----------------------------------------");
	}

}
