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

/**
 * The Place class is responsible for managing the Place objects. The
 * constructor sets the name and the address of the place, and the get methods
 * simply return both the name and address of the place. The equals method
 * analyzes if names of 2 Places match and returns true if it does, and false
 * otherwise.
 * 
 * &lt;p&gt;Bugs: no bugs encountered.
 * 
 * @author Pedro Henrique Koeler Goulart
 */
public class Place {
	
	private String name;
	private String address;
	
	/**
	 * This constructor initializes a Place object. In the process of this 
	 * initialization, all of the variables should be instantiated to their 
	 * beginning states.
	 * @param name sets the name of the Place.
	 * @param address sets the address of the Place.
	 */
	Place (String name, String address){
		this.name = name;
		this.address = address;
	}
	
	/**
	 * This method simply returns the name of the Place.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * This method simply returns the address of the Place.
	 */
	public String getAddress(){
		return address;
	}
	
	/**
	 * This method verifies if the object obj entered is an instance of the
	 * specified place (has the same type) and also verifies if it has the 
	 * same name as it. If it does, the method returns true, otherwise, it 
	 * returns false.
	 * @param obj the object that will be verified.
	 */
	@Override
	public boolean equals(Object obj){
		//Verifies if both types are the same
		if (obj instanceof Place) {
			Place temporary = (Place)obj;
			//Checks if the names are the same, ignoring the case
			if (this.getName().equalsIgnoreCase(temporary.getName())){
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

}
