import java.util.Random;
import java.util.ArrayList;

/**
 * The Pant class is responsible for managing the pant objects. The constructor
 * sets the position of the pants and the graphic prints the pants to the screen.
 * The update method calls the graphic repeatedly until isAlive is false.
 */
public class Pant {
	private Graphic graphic;
	private Random randGen;
	private boolean isAlive;
	
	/**
	 * This constructor initializes a new Pant object, so that the GameEngine
	 * can begin calling its update() method to print pants to the screen. In
	 * the process of this initialization, all of the variables should be 
	 * instantiated and initialized to their beginning states.
	 * @param x uses the float value passed to set the vertical pant position 
	 * on the screen.
	 * @param y uses the float value passed to set the horizontal pant position 
	 * on the screen.
	 * @param randGen uses the Random number generator value passed to configure
	 * this class' random number generator.
	 */
	public Pant(float x, float y, Random randGen) {
		//Initialization of pants
		graphic = new Graphic("PANT");
		this.isAlive = true;
		this.randGen = randGen;
		
		//Sets Pant to the x and y values provided
		graphic.setPosition(x, y);
	}
	
	/**
	 * This update method simply prints pants to the screen if their isAlive
	 * value is true.
	 */
	public void update(int time) {
		if (isAlive == true){
			//Creates Pant
			graphic.draw();
		}
	}
	
	/**
	 * Makes the pant graph accessible to other classes, so that they can verify
	 * if this object has collided with others.
	 */
	public Graphic getGraphic(){
		return this.graphic;
	}
	
	/**
	 * Verifies if isAlive is false and returns true if it is.
	 */
	public boolean shouldRemove() {
		boolean shouldRemove = false;
		
		//Sets method to true when isAlive is false
		if (isAlive == false){
			shouldRemove = true;
		}
		
		return shouldRemove;
	}
	
	/**
	 * This method checks if a fire-ball has collided with a pant and:
	 * 1) Destroys the fire-ball;
	 * 2) Kills the pant;
	 * 3) Creates a new fire in the same position of the pant.
	 * If no collision happened, then this method simply returns null.
	 * @param fireballs uses the fire-balls ArrayList to check if it has collided
	 * with a pant object.
	 */
	public Fire handleFireballCollisions(ArrayList<Fireball> fireballs) {
		for (int i = 0; i < fireballs.size(); i++){
			if (fireballs.get(i) != null && 
					graphic.isCollidingWith(fireballs.get(i).getGraphic()) == true && 
					this.isAlive == true){
				fireballs.get(i).destroy();
				isAlive = false;
				Fire newFireFromPant = new Fire(this.graphic.getX(), 
						this.graphic.getY(), randGen);
				return newFireFromPant;
			}
		}
		
		return null;
	}
}
