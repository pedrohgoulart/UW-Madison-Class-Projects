/**
 * The water class is responsible for managing the water objects. The constructor
 * sets the position, direction and speed of the water. The update method calls
 * the graphic repeatedly until the distance traveled is more than 200px.
 */
public class Water {
	private Graphic graphic;
	private float 	speed;
	private float 	distanceTraveled;
	
	/**
	 * This constructor initializes a the Water object, so that the GameEngine
	 * can begin calling its update() method to print and move the water in the
	 * screen. In the process of this initialization, all of the variables should
	 * be instantiated and initialized to their beginning states.
	 * @param x uses the float value passed to set the vertical water position 
	 * on the screen.
	 * @param y uses the float value passed to set the horizontal water position
	 * on the screen.
	 * @param direction is the direction of hero to set the direction of the 
	 * water.
	 */
	public Water(float x, float y, float direction) {
		//Initialization of Water
		this.speed = 0.7f;
		graphic = new Graphic("WATER");
		
		//Sets Water to the x, y and direction values provided
		graphic.setPosition(x, y);
		graphic.setDirection(direction);
	}
	
	/**
	 * This update method draws the water to the screen and moves it. If the
	 * distance traveled is more than 200px, this method returns null, otherwise,
	 * it returns itself.
	 * @param time uses the time provided to control how water moves on the 
	 * screen.
	 */
	public Water update(int time) {
		//Creates Water
		graphic.draw();
		
		if (distanceTraveled <= 200){
			graphic.setX(graphic.getX() + graphic.getDirectionX() * speed * time);
			graphic.setY(graphic.getY() + graphic.getDirectionY() * speed * time);
			distanceTraveled += (speed * time);
			return this;
		}
		else
			return null;
	}
	
	/**
	 * Makes the water graph accessible to other classes, so that they can verify
	 * if this object has collided with others.
	 */
	public Graphic getGraphic(){
		return this.graphic;
	}
}
