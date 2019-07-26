/**
 * A class that stores Score ScoreListADT and performs the functions for it. 
 * This class can also store Score instances.
 *
 * <p>Bugs: None found.
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class Score {
	/** Used to indicate name of Score */
	private String names; 
	
	/** Used to indicate points in Score */
	private double points;
	
	/** Used to indicate maximum number of points possible in Score */
	private double maxPossible;
	
	/**
	 * Constructor that initializes names, points and maxPossible
	 *
	 * <p>PRECONDITIONS: names is assumed to be not null, points is assumed to
	 * be bigger than 0 and less or equal to maxPossible, and maxPossible is
	 * assumed to be bigger than 0.
	 * 
	 * @param names Used to indicate name of Score
	 * @param points Used to indicate points in Score
	 * @param maxPossible Used to indicate maximum number of points possible
	 * in Score
	 * @throws IllegalArgumentException
	 */
	Score (String names, double points, double maxPossible) 
			throws IllegalArgumentException {
		//Checks for exception before proceeding
		if (names == null || points < 0 || points > maxPossible 
										|| maxPossible < 0) {
			throw new IllegalArgumentException();
		}
		
		this.names = names;
		this.points = points;
		this.maxPossible = maxPossible;
	}
	
	/** 
	 * Returns the name related to this Score
	 * @return the name of the score
	 */
	public String getName(){
		return names;
	}
	
	/** 
	 * Returns the points related to this Score
	 * @return the points of the score
	 */
	public double getPoints(){
		return points;
	}
	
	/** 
	 * Returns the maximum number of points possible related to this Score
	 * @return the maximum number possible of the score
	 */
	public double getMaxPossible(){
		return maxPossible;
	}
	
	/** 
	 * Returns the first letter of name related to this Score
	 * @return the first letter of name of the score
	 */
	public String getCategory(){
		return String.valueOf(names.charAt(0));
	}
	
	/** 
	 * Returns the percent rate of points related to this Score
	 * @return the percent rate of points of the score
	 */
	public double getPercent(){
		return (points/maxPossible) * 100;
	}
}
