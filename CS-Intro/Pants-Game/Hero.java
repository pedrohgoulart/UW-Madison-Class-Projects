import java.util.ArrayList;

/**
 * The Hero class is responsible for managing the hero object. The constructor
 * sets the position of the hero and the graphic prints the hero to the screen.
 * The update method calls the graphic repeatedly and analyzes the keys pressed
 * or movement of the mouse to move hero.
 */
public class Hero{
	private Graphic graphic;
	private float   speed;
	private int     controlType;
	
	/**
	 * This constructor initializes a the Hero object, so that the GameEngine
	 * can begin calling its update() method to print the hero to the screen.
	 * In the process of this initialization, all of the variables should be 
	 * instantiated and initialized to their beginning states.
	 * @param x uses the float value passed to set the vertical hero position 
	 * on the screen.
	 * @param y uses the float value passed to set the horizontal hero position 
	 * on the screen.
	 * @param controlType is the way hero should move, the value ranges from 1 
	 * to 3.
	 */
	public Hero(float x, float y, int controlType) {
		//Initialization of Hero
		this.speed = 0.12f;
		this.controlType = controlType;
		graphic = new Graphic("HERO");
		
		//Sets Hero to the x and y values provided
		graphic.setPosition(x, y);
	}
	
	/**
	 * Makes the Hero graph accessible to other classes, so that they can verify
	 * if this object has collided with others.
	 */
	public Graphic getGraphic(){
		return this.graphic;
	}
	
	/**
	 * This update method checks for the controlType and for controlType:
	 * 1) Hero should move when either (WASD) key is held, moving up, right,
	 * down and left respectively, and facing that direction.
	 * 2) Hero should move when either (WASD) key is held, moving up, right,
	 * down and left respectively, and facing the mouse direction.
	 * 3) Hero should move close to the position of the mouse, and stop 20px
	 * away from it.
	 * Hero should also throw water when either space or left mouse button is
	 * pressed.
	 * @param time uses the time provided to control how hero moves on the 
	 * screen.
	 * @param water uses the water array when space or left mouse button is
	 * pressed.
	 */
	public void update(int time, Water[] water) {
		/**If controlType is 1, changes the Hero position according to the key 
		 * pressed
		 */
		if (this.controlType == 1) {
			if (GameEngine.isKeyHeld("D") == true) {
				graphic.setX(graphic.getX() + speed * time); //Makes Hero go right
				graphic.setDirection(0); //Makes Hero face right
			}
			if (GameEngine.isKeyHeld("A") == true) {
				graphic.setX(graphic.getX() - speed * time); //Makes Hero go left
				graphic.setDirection((float)Math.PI); //Makes Hero face left
			}
			if (GameEngine.isKeyHeld("W") == true) {
				graphic.setY(graphic.getY() - speed * time); //Makes Hero go up
				graphic.setDirection((float)-Math.PI/2); //Makes Hero face up
			}
			if (GameEngine.isKeyHeld("S") == true) {
				graphic.setY(graphic.getY() + speed * time); //Makes Hero go down
				graphic.setDirection((float)Math.PI/2); //Makes Hero face down
			}
		}
		
		/**If controlType is 2, changes the Hero position according to the key
		 * pressed, but Hero faces the mouse cursor 
		 */
		else if (this.controlType == 2) {
			//Makes Hero face mouse
			graphic.setDirection(GameEngine.getMouseX(), GameEngine.getMouseY());
			//Makes Hero go right
			if (GameEngine.isKeyHeld("D") == true) 
				graphic.setX(graphic.getX() + speed * time);
			//Makes Hero go left
			if (GameEngine.isKeyHeld("A") == true) 
				graphic.setX(graphic.getX() - speed * time);
			//Makes Hero go up
			if (GameEngine.isKeyHeld("W") == true) 
				graphic.setY(graphic.getY() - speed * time);
			//Makes Hero go down
			if (GameEngine.isKeyHeld("S") == true) 
				graphic.setY(graphic.getY() + speed * time);
		}
		
		/**If controlType is 3, changes the Hero position according to the mouse
		 * cursor 
		 */
		else if (this.controlType == 3) {
			//Makes Hero face mouse
			graphic.setDirection(GameEngine.getMouseX(), GameEngine.getMouseY());
			
			//Calculates distance between Hero and mouse
			double distanceX = Math.pow(GameEngine.getMouseX() - 
					graphic.getX(), 2);
			double distanceY = Math.pow(GameEngine.getMouseY() - 
					graphic.getY(), 2);
			double totalDistance = Math.sqrt(distanceX + distanceY);
			
			//Moves Hero towards the mouse
			if (totalDistance >= 20) {
				graphic.setX(graphic.getX() + graphic.getDirectionX() * 
						speed * time);
				graphic.setY(graphic.getY() + graphic.getDirectionY() * 
						speed * time);
			}
		}
		
		//Create Water if space or left mouse button is pressed
		if (GameEngine.isKeyHeld("SPACE") == true || 
				GameEngine.isKeyHeld("MOUSE") == true) {
			for (int i=0; i < water.length; i++){ 
			    if (water[i] == null) {
			        water[i] = new Water(graphic.getX(), graphic.getY(), 
			        		graphic.getDirection());
			        break;
			    }
			}
		}
		
		//Creates Hero
		graphic.draw();
	}
	
	/**
	 * This method checks if a fire-ball has collided with a hero and returns
	 * true.
	 * @param fireballs uses the fire-balls ArrayList to check if it has collided
	 * with a pant object.
	 */
	public boolean handleFireballCollisions(ArrayList<Fireball> fireballs) {
		boolean handleFireballCollisions = false;
		
		for (int i = 0; i < fireballs.size(); i++){
			if (fireballs.get(i) != null && 
					graphic.isCollidingWith(fireballs.get(i).getGraphic()) 
					== true){
				handleFireballCollisions = true;
				break;
			}
		}
		
		return handleFireballCollisions;
	}
}
