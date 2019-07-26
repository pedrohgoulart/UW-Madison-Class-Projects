/////////////////////////////////////////////////////////////////////////////
//Semester:         CS367 Spring 2017 
//PROJECT:          p4
//FILE:             Interval.java
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
 * An implementation of the IntervalADT.
 * 
 * @author Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class Interval<T extends Comparable<T>> implements IntervalADT<T> {

    private T start;
    private T end;
    private String label;

    /**
     * Main constructor to create a new Interval.
     * 
     * PRE-CONDITIONS: start and end >= 0.
     * 
     * @param start - a comparable type T with the start value where the item
     * 	to be scheduled should start.
     * @param end - a comparable type T with the end value where the item to be
     * 	scheduled should end.
     * @param label - A string that contains the name value of the event that
     * 	is being scheduled.
     */
    public Interval(T start, T end, String label) throws IllegalArgumentException {
    	if(end.compareTo(start) == -1){
    		throw new IllegalArgumentException();
    	}
    	this.start = start;
    	this.end = end;
    	this.label = label;
    }

    /**
     * Returns the start value for the Interval object.
     * 
     * @return - start value in type T.
     */
    @Override
    public T getStart() {
        return start;
    }

    /**
     * Returns the end value for the Interval object.
     * 
     * @return - end value in type T.
     */
    @Override
    public T getEnd() {
    	return end;
    }

    /**
     * Returns the name of the "event" that is the scheduled time block.
     * 
     * @return - name of the event in a string.
     */
    @Override
    public String getLabel() {
    	return label;
    }

    /**
     * Tests whether or not the Interval object intersects with another
     * interval object that is passed in through parameters.
     * 
     * @param other - The other interval object to check if there is an overlap
     * 	with.
     */
    @Override
    public boolean overlaps(IntervalADT<T> other) {
    	//Compares the end to the other start
    	int endCompareOtherStart = getEnd().compareTo(other.getStart());
    	//Compares the start to the other end
    	int startCompareOtherEnd = getStart().compareTo(other.getEnd());
    	
    	/* Returns true if the result is less than or equal to 0, which means
    	 * only one of the variables above returned 1 (thisEnd < otherStart, or 
    	 * thisStart < otherEnd). 
    	 * This property comes from (b1 - a2) * (b2 - a1) <= 0.
    	 */
        return (endCompareOtherStart * startCompareOtherEnd) <= 0;
    }

    /**
     * Checks to see if the given time is in this interval.
     * 
     * @param point - Value of comparable type T to check if point is within
     * 	the interval.
     * @return true iff point is within the interval. False if not.
     */
    @Override
    public boolean contains(T point) {
       if (point.compareTo(this.getEnd()) <= 0) {
    	   if (point.compareTo(this.getStart()) >= 0) {
    		   return true;
    	   }
       }
       return false;
    }

    /**
     * Compares two intervals to determine which comes before or after the
     * 	other.
     * 
     * @param other - Interval to compare this interval to.
     * 
     * @return -1 if this is before other, 0 if equal, 1 if after.
     */
    @Override
    public int compareTo(IntervalADT<T> other) {
    	if (this.getStart() != other.getStart()) {
    		return this.getStart().compareTo(other.getStart());
    	}
    	else {
    		return this.getEnd().compareTo(other.getEnd());
    	}
    }
    
    /**
     * Puts the interval into a human readable string.
     * 
     * @return Human readable string.
     */
    public String toString(){
		return label + " [" + start + ", " + end + "]";
	}

}
