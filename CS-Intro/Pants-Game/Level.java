//
// Title:            Pants On Fire
// Files:            Levels.java, PantsOnFire.jar, Hero.java, Pant.java, Water.java, Fireball.java, Fire.java
// Semester:         CS302 Fall 2016
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// CS Login:         koeler-goulart
// Lecturer's Name:  Gary Dahl
// Lab Section:      331
//

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * The Level class is responsible for managing all of the objects in your game.
 * The GameEngine creates a new Level object for each level, and then calls that
 * Level object's update() method repeatedly until it returns either "ADVANCE"
 * (to go to the next level), or "QUIT" (to end the entire game).
 * <br/><br/>
 * This class should contain and use at least the following private fields:
 * <tt><ul>
 * <li>private Random randGen;</li>
 * <li>private Hero hero;</li>
 * <li>private Water[] water;</li>
 * <li>private ArrayList&lt;Pant&gt; pants;</li>
 * <li>private ArrayList&lt;Fireball&gt; fireballs;</li>
 * <li>private ArrayList&lt;Fire&gt; fires;</li>
 * </ul></tt>
 */

public class Level {
	private Random randGen;
	private Scanner input;
	private Hero	hero;
	private Water[] water;
	private ArrayList<Pant> pants;
	private ArrayList<Fireball> fireballs;
	private ArrayList<Fire> fires;
	
	/**
	 * This constructor initializes a new Level object, so that the GameEngine
	 * can begin calling its update() method to advance the game's play.  In
	 * the process of this initialization, all of the objects in the current
	 * level should be instantiated and initialized to their beginning states.
	 * @param randGen is the only Random number generator that should be used
	 * throughout this level, by the Level itself and all of the Objects within.
	 * @param level is a string that either contains the word "RANDOM", or the 
	 * contents of a level file that should be loaded and played. 
	 */
	public Level(Random randGen, String level) {
		//Random initializer
		this.randGen = randGen;
		
		//Scanner initializer
		this.input = new Scanner(level); 
		
		//Water initializer
		water = new Water[8];
				
		//Pants initializer
		pants = new ArrayList<Pant>();
				
		//Fire-balls initializer
		fireballs = new ArrayList<Fireball>();
				
		//Fires initializer
		fires = new ArrayList<Fire>();
		
		if (level.contains("RANDOM")) {
			createRandomLevel();
		}
		else {
			loadLevel(level);
		}
		
		//Display number of fires and pants
		this.getHUDMessage();
	}

	/**
	 * The GameEngine calls this method repeatedly to update all of the objects
	 * within your game, and to enforce all of the rules of your game.
	 * @param time is the time in milliseconds that have elapsed since the last
	 * time this method was called.  This can be used to control the speed that
	 * objects are moving within your game.
	 * @return When this method returns "QUIT" the game will end after a short
	 * 3 second pause and a message indicating that the player has lost.  When
	 * this method returns "ADVANCE", a short pause and win message will be 
	 * followed by the creation of a new level which replaces this one.  When
	 * this method returns anything else (including "CONTINUE"), the GameEngine
	 * will simply continue to call this update() method as usual. 
	 */
	public String update(int time) {	
		//HERO
		hero.update(time, water);
		//Checks for collision between hero and fire-ball
		if (hero.handleFireballCollisions(fireballs) == true){
			return "QUIT";
		}
		
		//WATER
		for (int i=0; i < water.length; i++){
			if(water[i] != null) water[i] = water[i].update(time);
		}
		
		//PANTS
		for (int i=0; i < pants.size(); i++){
			//Updates pants according to the ArrayList size
			pants.get(i).update(time);
			//Checks for collision between pant and fire-ball
			Fire fireCollision = pants.get(i).handleFireballCollisions(fireballs);
			if (fireCollision != null) fires.add(fireCollision);
		}
		
		//FIRE
		for (int i=0; i < fires.size(); i++){
			//Updates fire (and creates fire-balls)
			Fireball fireballCollision = fires.get(i).update(time);
			if (fireballCollision != null) fireballs.add(fireballCollision);
			//Checks for collision between fire and water
			fires.get(i).handleWaterCollisions(water);
		}
				
		//FIRE-BALLS
		for (int i=0; i < fireballs.size(); i++){
			if (fireballs.get(i) != null) {
				//Updates fire-balls
				fireballs.get(i).update(time);
				//Checks for collision between fire-ball and water
				fireballs.get(i).handleWaterCollisions(water);
			}
			
		}
		
		//REMOVE DEAD ARRAYS
		//Remove pants
		for (int i=0; i < pants.size(); i++){
			if (pants.get(i).shouldRemove() == true) {
				pants.remove(i--);
			}
		}
		//Remove fires
		for (int i=0; i < fires.size(); i++){
			if (fires.get(i).shouldRemove() == true) {
				fires.remove(i--);
			}
		}
		//Remove fire-balls, makes sure only non-null fire-balls are be removed
		for (int i=0; i < fireballs.size(); i++){
			if (fireballs.get(i) != null && 
					fireballs.get(i).shouldRemove() == true) {
				fireballs.remove(i--);
			}
		}
		
		//CHECK IF THERE ARE ANY FIRES OR PANTS LEFT
		if (fires.size() == 0) {
			return "ADVANCE"; //Win
		}
		if (pants.size() == 0) {
			return "QUIT"; //Loose
		}
		
		

		return "CONTINUE";
	}	
	
	/**
	 * This method returns a string of text that will be displayed in the
	 * upper left hand corner of the game window.  Ultimately this text should
	 * convey the number of unburned pants and fires remaining in the level.
	 * However, this may also be useful for temporarily displaying messages that
	 * help you to debug your game.
	 * @return a string of text to be displayed in the upper-left hand corner
	 * of the screen by the GameEngine. 
	 */
	public String getHUDMessage() {
		int numberOfPants = pants.size();
		int numberOfFires = fires.size();
		
		return "Pants Left: " + numberOfPants + "\n" + "Fires Left: " + 
				numberOfFires + "\n"; 
	}

	/**
	 * This method creates a random level consisting of a single Hero centered
	 * in the middle of the screen, along with 6 randomly positioned Fires,
	 * and 20 randomly positioned Pants.
	 */
	public void createRandomLevel() { 
		//Calls hero constructor
		int controlType = randGen.nextInt(3) + 1;
		hero = new Hero(GameEngine.getWidth()/2, GameEngine.getHeight()/2, 
				controlType);
		
		//Calls pant constructor (populates arraylist with 20 pants)
		for (int i=0; i < 20; i++) {
			int x = randGen.nextInt(GameEngine.getWidth() + 1);
			int y = randGen.nextInt(GameEngine.getHeight() + 1);
			Pant newPant = new Pant(x, y, randGen);
			pants.add(newPant);
		}
				
		//Calls fire constructor (populates arraylist with 6 fires)
		for (int i=0; i < 6; i++) {
			int x = randGen.nextInt(GameEngine.getWidth() + 1);
			int y = randGen.nextInt(GameEngine.getHeight() + 1);
			Fire newFire = new Fire(x, y, randGen);
			fires.add(newFire);
		}
	}

	/**
	 * This method initializes the current game according to the Object location
	 * descriptions within the level parameter.
	 * @param level is a string containing the contents of a custom level file 
	 * that is read in by the GameEngine.  The contents of this file are then 
	 * passed to Level through its Constructor, and then passed from there to 
	 * here when a custom level is loaded.  You can see the text within these 
	 * level files by dragging them onto the code editing view in Eclipse, or 
	 * by printing out the contents of this level parameter.  Try looking 
	 * through a few of the provided level files to see how they are formatted.
	 * The first line is always the "ControlType: #" where # is either 1, 2, or
	 * 3.  Subsequent lines describe an object TYPE, along with an X and Y 
	 * position, formatted as: "TYPE @ X, Y".  This method should instantiate 
	 * and initialize a new object of the correct type and at the correct 
	 * position for each such line in the level String.
	 */
	public void loadLevel(String level) {
		input.skip("ControlType: ");
		
		//Variables
		int controlType = 0;
		float heroPositionX = 0;
		float heroPositionY = 0;
		float pantPositionX = 0;
		float pantPositionY = 0;
		float firePositionX = 0;
		float firePositionY = 0;
		
		//Passes controlType input
		controlType = input.nextInt();
		
		//While there is a line left, analyze its contents
		while (input.hasNext()){
			input.nextLine();
			//If it finds HERO in file
			if (input.hasNext("HERO")){
				input.skip("HERO @ ");
				/**Gets first 4 characters after the skip, and converts them
				 * from string to float, then does the same for the following 
				 * number.
				 */
				heroPositionX = Float.parseFloat(input.next().substring(0,4));
				heroPositionY = Float.parseFloat(input.next());
				//Set values to the constructor
				hero = new Hero(heroPositionX, heroPositionY, controlType);
			}
			//If it finds PANT in file
			else if (input.hasNext("PANT")){
				input.skip("PANT @ ");
				/**Gets first 4 characters after the skip, and converts them
				 * from string to float, then does the same for the following 
				 * number.
				 */
				pantPositionX = Float.parseFloat(input.next().substring(0,4));
				pantPositionY = Float.parseFloat(input.next());
				//Add pant to arraylist
				Pant newPant = new Pant(pantPositionX, pantPositionY, randGen);
				pants.add(newPant);
			}
			//If it finds FIRE in file
			else if (input.hasNext("FIRE")){
				input.skip("FIRE @ ");
				/**Gets first 4 characters after the skip, and converts them
				 * from string to float, then does the same for the following 
				 * number.
				 */
				firePositionX = Float.parseFloat(input.next().substring(0,4));
				firePositionY = Float.parseFloat(input.next());
				//Add fire to arraylist
				Fire newFire = new Fire(firePositionX, firePositionY, randGen);
				fires.add(newFire);
			}
		}
	}

	/**
	 * This method creates and runs a new GameEngine with its first Level.  Any
	 * command line arguments passed into this program are treated as a list of
	 * custom level filenames that should be played in a particular order.
	 * @param args is the sequence of custom level files to play through.
	 */
	public static void main(String[] args) {
		GameEngine.start(null,args);
	}
}
