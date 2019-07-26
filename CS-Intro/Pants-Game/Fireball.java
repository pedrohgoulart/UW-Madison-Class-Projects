/**
 * The fire-ball class is responsible for managing the fire-ball objects. The
 * constructor sets the position of the fire-balls and the graphic prints the 
 * fire-balls to the screen. The update method calls the graphic repeatedly 
 * until isAlive is false.
 */
public class Fireball {
	private Graphic graphic;
	private float speed;
	private boolean isAlive;
	
	/**
	 * This constructor initializes a the fire-ball object, so that the 
	 * GameEngine can begin calling its update() method to print and move the
	 * fire-balls in the screen. In the process of this initialization, all of
	 * the variables should be instantiated and initialized to their beginning 
	 * states.
	 * @param x uses the float value passed to set the vertical fire-ball 
	 * position on the screen.
	 * @param y uses the float value passed to set the horizontal fire-ball 
	 * position on the screen.
	 * @param directionAngle is the direction of fire-ball.
	 */
	public Fireball(float x, float y, float directionAngle) {
		//Initialization of fire-ball
		this.speed = 0.2f; 
		graphic = new Graphic("FIREBALL");
		isAlive = true;
		
		//Sets fire-ball to the x and y values generated and rotates it
		graphic.setPosition(x, y);
		graphic.setDirection(directionAngle);
	}

	/**
	 * This update method draws the fire-ball to the screen and moves it. If the
	 * distance traveled is more than 100px on either side of the screen, this 
	 * method sets isAlive to false. If isAlive is true, then it updates the
	 * fire-balls position.
	 * @param time uses the time provided to control how fire-balls move on the
	 * screen.
	 */
	public void update(int time) {
		//Calls destroy method if fire-ball is 100px from the edge of the screen
		if (graphic.getX() < -100 || graphic.getY() < -100 || 
				graphic.getX() > GameEngine.getWidth() + 100 || 
				graphic.getY() > GameEngine.getHeight() + 100){
			this.destroy();
		} 
		
		//Creates fire-ball and updates its speed if it is alive
		if (isAlive == true){
			graphic.setX(graphic.getX() + graphic.getDirectionX() * 
					speed * time);
			graphic.setY(graphic.getY() + graphic.getDirectionY() * 
					speed * time);
			graphic.draw();
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
	 * Sets isAlive to false when called.
	 */
	public void destroy() {
		if (this.isAlive == true) {
			this.isAlive = false;
		}
	}
	
	/**
	 * This method verifies if isAlive is false and, if it is, then it returns
	 * true, if not, then it returns false.
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
	 * This method checks if water has collided with a fire-ball and:
	 * 1) Calls the destroy method;
	 * 2) Sets the water to null.
	 * @param water uses the water array to check if it has collided with a 
	 * fire-ball object.
	 */
	public void handleWaterCollisions(Water[] water) {
		for (int i = 0; i < water.length; i++){
			if (water[i] != null && 
					graphic.isCollidingWith(water[i].getGraphic()) && 
					this.isAlive == true){
				this.destroy();
				water[i] = null;
			}
		}
	}
}
