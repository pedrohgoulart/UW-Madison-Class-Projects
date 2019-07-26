import java.util.Random;

/**
 * The Fire class is responsible for managing the Fire objects. The constructor
 * sets the position of the Fires and the graphic prints the fires to the screen.
 * The update method calls the graphic repeatedly until heat is 0.
 */
public class Fire {
	private Graphic graphic;
	private Random randGen;
	private int fireballCountdown;
	private int heat;
	
	/**
	 * This constructor initializes a new Fire object, so that the GameEngine
	 * can begin calling its update() method to print fires to the screen. In
	 * the process of this initialization, all of the variables should be 
	 * instantiated and initialized to their beginning states.
	 * @param x uses the float value passed to set the vertical fire position 
	 * on the screen.
	 * @param y uses the float value passed to set the horizontal fire position 
	 * on the screen.
	 * @param randGen uses the Random number generator value passed to configure
	 * this class' random number generator.
	 */
	public Fire(float x, float y, Random randGen) {
		//Initialization of fire
		graphic = new Graphic("FIRE");
		heat = 40;
		this.randGen = randGen;
		
		//Sets Fire to the x and y values provided
		graphic.setPosition(x, y);

		
		//Time between fire-balls (3000 to 6000 milliseconds)
		fireballCountdown = randGen.nextInt(3000) + 3000;
	}
	
	/**
	 * This update method creates the fire-balls that will be thrown by the fire
	 * and if the fireballCountdown is less than 1, than it generates a random
	 * angle and initializes a fire-ball. It also resets the random generator
	 * after. It also returns the fire-ball.
	 * If the heat is less than 1 or if the fireballCountdown is not less than
	 * 1, then it returns null.
	 * @param time uses the time provided to control how fire-balls moves on 
	 * the screen.
	 */
	public Fireball update(int time) {
		//Prevents fire from launching new fire-balls if heat is less than 1
		if (heat < 1) return null; 
		
		//Creates Fire
		graphic.draw();
		
		//Fire-ball countdown
		fireballCountdown -= time;
		
		if (fireballCountdown <= 0){
			//Generates the fire-ball angle and adds position to arrayList
			float fireballAngle = 2*(float)Math.PI * randGen.nextFloat();
			Fireball fireball = new Fireball(this.graphic.getX(), 
					this.graphic.getY(), fireballAngle);
			
			//Resets time between fire-balls (3000 to 6000 milliseconds)
			fireballCountdown = randGen.nextInt(3000) + 3000;
			
			return fireball;
		}
		else {
			return null;
		}
	}	
	
	/**
	 * Makes the fire graph accessible to other classes, so that they can verify
	 * if this object has collided with others.
	 */
	public Graphic getGraphic(){
		return this.graphic;
	}
	
	/**
	 * This method verifies if the fire has a heat less than 1, if it does, then
	 * it returns true, if not, then it returns false.
	 */
	public boolean shouldRemove() {
		boolean shouldRemove = false;
		
		//Sets method to true when heat is less than 1
		if (heat < 1){
			shouldRemove = true;
		}
		
		return shouldRemove;
	}
	
	/**
	 * This method checks if water has collided with a fire and:
	 * 1) Decreases the fire heat by 1;
	 * 2) Sets the water to null.
	 * @param water uses the water array to check if it has collided with a fire
	 * object.
	 */
	public void handleWaterCollisions(Water[] water) {
		for (int i = 0; i < water.length; i++){
			if (water[i] != null && 
					graphic.isCollidingWith(water[i].getGraphic()) && 
					heat >= 1){
				heat--;
				water[i] = null;
			}
		}
	}
}
