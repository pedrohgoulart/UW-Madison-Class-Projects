// 
// Title:            Places
// Files:            MyPlacesApp.java, PlaceList.jar, Place.java
// Semester:         CS302 Fall 2016
// 
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// CS Login:         koeler-goulart
// Lecturer's Name:  Gary Dahl
// Lab Section:      331
// 

import java.util.ArrayList;

/**
 * The PlaceList class is responsible for managing the ArrayList of Place 
 * objects. The constructor initializes the ArrayList and the add and remove
 * methods add and remove an item from the ArrayList, respectively. The size
 * method returns the size of the ArrayList. The Has places method checks if
 * there is any content in the ArrayList, and returns true if it does. The 
 * get method returns the Place in the ArrayList according to it's position.
 * The contains method analyzes the ArrayList and verifies if a Place has the
 * same name as another Place.
 * 
 * &lt;p&gt;Bugs: no bugs encountered.
 * 
 * @author Pedro Henrique Koeler Goulart
 */
public class PlaceList {
	private ArrayList<Place> places;
	
	/**
	 * This constructor simply initializes the places ArrayList.
	 */
	PlaceList() {
		places = new ArrayList<Place>();
	}
	
	/**
	 * This method adds a place to the ArrayList.
	 * @param place is the Place object that will be added.
	 */
	public void add(Place place){
		places.add(place);
	}
	
	/**
	 * This method removes a place from the ArrayList.
	 * @param index is the number of the position of the Place object that will
	 * be removed.
	 */
	public void remove(int index){
		places.remove(index);
	}
	
	/**
	 * This method simply returns the size of the ArrayList.
	 */
	public int size(){
		return places.size();
	}
	
	/**
	 * This method checks if the ArrayList has any Place Objects in it, and
	 * returns false if it doesn't, and true if it does.
	 */
	public boolean hasPlaces(){
		if (places.size() <= 0){
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * This method gets a place from the ArrayList according to its position in
	 * it.
	 * @param index is the number of the position of the Place object.
	 */
	public Place get(int index){
		return places.get(index);
	}
	
	/**
	 * This method checks the ArrayList for a place Object with the same name
	 * as the one entered. If it finds, then it returns true, otherwise it
	 * returns false.
	 * @param Place is the Place object that will be compared to the places in
	 * the ArrayList.
	 */
	public boolean contains(Place place){
		for (int i = 0; i < places.size(); i++){
			if (this.places.get(i).equals(place)) {
				return true;
			}
		}
		
		return false;
	}
}
