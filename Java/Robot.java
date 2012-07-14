public class Robot{
	private Point position;
	private int lambdaCount;
	private int score;
	private int waterTime;
	
	public Robot(int x, int y) {
		position = new Point(x, y);
		lambdaCount = 0;
		score = 0;
		waterTime = 0;
	}
	
	public void moveUp() {
		position.increY();
		score--;
	}
	
	public void moveDown() {
		position.decreY();
		score--;
	}
	
	public void moveLeft() {
		position.decreX();
		score--;
	}
	
	public void moveRight() {
		position.increX();
		score--;
	}
	
	public void stay() {
		score--;
	}
	
	public void gainLambda() {
		lambdaCount++;
		score += 25;
	}
	
	public void liftLambda() {
		score += 50*lambdaCount;
	}
	
	public void abort() {
		score += 25*lambdaCount;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getLambda() {
		return lambdaCount;
	}
	
	public void cleanWaterTime() {
		waterTime = 0;
	}
	
	public void stayInWater() {
		waterTime++;
	}
	
	public int getWaterTime() {
		return waterTime;

	}
}