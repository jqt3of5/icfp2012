//import java.awt.Point;
import java.util.ArrayList;

public class Board {
  public enum CellTypes {
    Robot, Rock, Closed, Earth, Wall, Lambda, Open, Empty
  };
  public enum GameState {
    Win, Lose, Abort, Continue
  };
  public Robot robot;
  public int waterLevel;
  public int waterRate;
  public ArrayList<Point> lambdaPos;
  public Point liftLocation;
  public CellTypes layout[][];
  public int layoutWidth;
  public int layoutHeight;
  public int ticks;

  public Board(int width, int height) {
    ticks = 0;
    layout = new CellTypes[height][width];
    waterLevel = 0;
    waterRate = 0;
    lambdaPos = new ArrayList<Point>();
  }

  public Board(String map) {
    String[] lines = map.split("\\r\\n|\\r|\\n");
    int width = 0, height = lines.length;
    for (String line : lines) {
      if (line.length() > width) {
        width = line.length();
      }
    }

    ticks = 0;
    layout = new CellTypes[height][width];
    waterLevel = 0;
    waterRate = 0;
    lambdaPos = new ArrayList<Point>();
   

    int y = 0;
    for (String line : lines) {
      for (int x = 0; x < line.length(); ++x) {
        switch (line.charAt(x)) {
        case '*':
          layout[y][x] = CellTypes.Rock;
          break;
        case '#':
          layout[y][x] = CellTypes.Wall;
          break;
        case 'R':
          layout[y][x] = CellTypes.Robot;
          robot = new Robot(x,y);
          break;
        case '.':
          layout[y][x] = CellTypes.Earth;
          break;
        case '\\':
          layout[y][x] = CellTypes.Lambda;
          lambdaPos.add(new Point(x, y)); // careful the order
          break;
        case 'L':
          layout[y][x] = CellTypes.Closed;
          liftLocation = new Point(x,y);
          break;
        case ' ':
          layout[y][x] = CellTypes.Empty;
          break;
        case 'O':
          layout[y][x] = CellTypes.Open;
          break;
        }
      }
      y++;
    }
  }

  public GameState move(char move) // should change internal state, or create a new
                              // one?
  {
    int x = robot.getPosition().getX();
    int y = robot.getPosition().getY();
    int xp = 0, yp = 0;

    switch (move) {
    case 'L':
        xp = x - 1;
        yp = y;
      break;
    case 'R': 
        xp = x + 1;
        yp = y; 
      break;
    case 'U':
        xp = x;
        yp = y + 1;
      break;
    case 'D':
        xp = x;
        yp = y - 1;
      break;
    }

    if (layout[yp][xp] == CellTypes.Open) {
      return GameState.Win;
    }
    
    if (layout[yp][xp] == CellTypes.Wall
        || layout[yp][xp] == CellTypes.Closed) {
      return GameState.Continue;
    }
    if (layout[yp][xp] == CellTypes.Lambda) {
     robot.gainLambda();
       lambdaPos.remove(new Point(xp, yp)); // careful order!
    }

    if (move == 'L' && layout[yp][xp] == CellTypes.Rock && layout[yp][xp-1] == CellTypes.Empty)
    {
     layout[yp][xp-1] = CellTypes.Rock;
    }
    else if (move == 'R' && layout[yp][xp] == CellTypes.Rock && layout[yp][xp+1] == CellTypes.Empty)
    {
      layout[yp][xp+1] = CellTypes.Rock; 
    }
    else if (layout[yp][xp] == CellTypes.Rock)
    {
      return GameState.Continue;
    }
    layout[y][x] = CellTypes.Empty;
    layout[yp][xp] = CellTypes.Robot;
    robot.setPosition(xp, yp);
    return GameState.Continue;
  }

  public GameState move(String move) // same question as above
  {
    for (char m : move.toCharArray()) {
      GameState state = move(m);
      if (state != GameState.Continue) {
        return state;
      }
    }
    return GameState.Continue;
  }

  public GameState update() // again
  {

    if (ticks % waterRate == waterRate - 1) {
      waterLevel += 1;
    }
    
    
    if(robot.getWaterTime() == robot.getWaterThreshold())
      return GameState.Lose; //is a drowning lose or abort?
    
    robot.stayInWater();//at what point do we want this called?
    
    for (int y = 0; y < layoutHeight; ++y) {
      for (int x = 0; x < layoutWidth; ++x) {
        
        if (layout[y][x] == CellTypes.Closed && lambdaPos.size() == 0) {
          layout[y][x] = CellTypes.Open;
        }
        
        if (layout[y][x] == CellTypes.Rock) {
          
          int xp = 0, yp = 0;
          if (y-1 > 0 && layout[y-1][x] == CellTypes.Empty)
          {
            xp = x;
            yp = y-1;
          }
          if (y-1 > 0 && x+1 < layoutWidth-1 && 
              layout[y-1][x] == CellTypes.Rock && 
              layout[y][x+1] == CellTypes.Empty && 
              layout[y-1][x+1] == CellTypes.Empty )
          {
            xp = x+1;
            yp = y-1;
          }
          if (y-1 > 0 && x+1 < layoutWidth-1 && x-1 > 0 &&
              layout[y-1][x] == CellTypes.Rock && 
              (layout[y][x+1] != CellTypes.Empty || layout[y-1][x+1] != CellTypes.Empty) && 
              layout [y][x-1] == CellTypes.Empty &&
              layout[y-1][x-1] == CellTypes.Empty)
          {
            xp = x-1;
            yp = y-1;
          }
          if (y-1 > 0 && x+1 < layoutWidth-1 &&
              layout[y-1][x] == CellTypes.Lambda &&
              layout[y][x+1] == CellTypes.Empty &&
              layout[y-1][x+1] == CellTypes.Empty)
          {
            xp = x+1;
            yp = y-1;
          }
          
          layout[y][x] = CellTypes.Empty;
          layout[yp][xp] = CellTypes.Rock;
          if (layout[yp-1][xp] == CellTypes.Robot)
          {
            return GameState.Lose;
          }
          
        }
      }
    }
    return GameState.Continue;
  }

  
  public GameState tick(char nextMove) { // will be changed to an enum
    GameState state;
    if (nextMove == 'A')
      return GameState.Abort;
    state = move(nextMove);
    if (state != GameState.Continue)
      return state;
    
    state = update();
    if (state != GameState.Continue)
      return state;
    ticks += 1;
   
    return GameState.Continue;
  }

}
