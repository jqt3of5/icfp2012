/*import Java.Util.ArrayList;
import Java.String;

public class Map {
    public enum CellTypes {Robot, Rock, Closed, Earth, Wall, Lambda, Open, Empty};
    public Robot robot;
    public int waterLevel;
    public int waterRate;
    public int totalPoints;
    public int totalLambdas;
    public ArrayList<Point> lambdaPos;
    public Point liftLocation;
    public CellTypes layout[][];
    public int layoutWidth;
    public int layoutHeight;

    public Map(int width, int height)
    {
	
	layout = new CellTypes[height][width];
	robot = new Robot();
	totalPoints = 0;
	waterLevel = 0;
	waterRate = 0;
	lambdaPos = new ArrayList<Point>();
    }

    public static Map read(String mapAcsii, int width, int height)
    {
	Map newMap = new Map(width, height);
	int index = 0;
	for (int i = 0; i < height; ++i)
	    {
		for (int j = 0; j < width; ++j, ++index)
		    {
			switch (mapAscii[index])
			    {
			    case '*':
				newMap.layout[i][j] = Rock;
				break;
			    case '#':
				newMap.layout[i][j] = Wall;
				break;
			    case 'R':
				newMap.layout[i][j] = Robot;
				break;
			    case '.':
				newMap.layout[i][j] = Earth;
				break;
			    case '\\':
				newMap.layout[i][j] = Lambda;
				lambdaPos.push_back(new Point(j,i)); // careful the order
				break;
			    case 'L':
				newMap.layout[i][j] = Closed;
				break;
			    case ' ':
				newMap.layout[i][j] = Empty;
				break;
			    case 'O':
				newMap.layout[i][j] = Open;
				break;
			    }
		    }
	    }
    }

    public void move(char move) //should change internal state, or create a new one?
    {
	int x = robot.getPos().x;
	int y = robot.getPos().y;
	int xp, yp;

	switch(move)
	    {
	    case 'L':
		if (x-1 > 0){
			xp = x-1;
			yp = y;
		    }
		break;
	    case 'R':
		if (x+1 < width){
			xp = x+1;
			yp = y;
		    }
		break;
	    case 'U':
		if (y+1 < height){
		    xp = x;
		    yp = y+1;
		}
		break;
	    case 'D':
		if (y-1 > 0){
		    xp = x;
		    yp = y-1;
		}
		break;
	    }
	
	if (layout[yp][xp] == 'O')
	    {
		System.out.println("You won!");
	    }
	if (layout[yp][xp] == '*' || layout[yp][xp] == '#' || layout[yp][xp] == 'L')
	    {
		return;
	    }
	if (layout[yp][xp] == '\\')
	    {
		totalPoints += 25;
		totalLambdas += 1;
		lambdaPos.remove(new Point(xp,yp)); //careful order!
	    }

	layout[y][x] = Empty;
	layout[yp][xp] = Robot;
	robot.setPos(xp,yp);
    }

    public void move(String move) //same question as above
    {
	for (char m : move)
	    {
		move(m);
	    }
    }
    
    public void update() //again
    {
	for (int i = 0; i < height; ++i)
	    {
		for (int j = 0; j < width; ++j)
		    {
			if (layout[i][j] == 'L' && lambdaPos.size() == 0)
			    {
				layout[i][j] == 'O';
			    }
			if (layout[i][j] == '*')
			    {
				
			    }
		    }
	    }
    }

    public void checkConditions()
    {
	
    }
    public void tick()
    {
	move(robot.getNextMove());
	update();
	checkConditions();
    }
    
    }*/