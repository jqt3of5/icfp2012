import java.awt.Point;
import java.util.ArrayList;

public class Board {
  public enum CellTypes {
    Robot, Rock, Closed, Earth, Wall, Lambda, Open, Empty
  };

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
  public int ticks;

  public Board(int width, int height) {
    ticks = 0;
    layout = new CellTypes[height][width];
    totalPoints = 0;
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

    Board newMap = new Board(width, height);

    int y = 0;
    for (String line : lines) {
      for (int x = 0; x < line.length(); ++x) {
        switch (line.charAt(x)) {
        case '*':
          newMap.layout[y][x] = CellTypes.Rock;
          break;
        case '#':
          newMap.layout[y][x] = CellTypes.Wall;
          break;
        case 'R':
          newMap.layout[y][x] = CellTypes.Robot;
          break;
        case '.':
          newMap.layout[y][x] = CellTypes.Earth;
          break;
        case '\\':
          newMap.layout[y][x] = CellTypes.Lambda;
          newMap.lambdaPos.add(new Point(x, y)); // careful the order
          break;
        case 'L':
          newMap.layout[y][x] = CellTypes.Closed;
          break;
        case ' ':
          newMap.layout[y][x] = CellTypes.Empty;
          break;
        case 'O':
          newMap.layout[y][x] = CellTypes.Open;
          break;
        }
      }
      y++;
    }
  }

  public void move(char move) // should change internal state, or create a new
                              // one?
  {
    int x = robot.getPos().x;
    int y = robot.getPos().y;
    int xp = 0, yp = 0;

    switch (move) {
    case 'L':
      if (x - 1 > 0) {
        xp = x - 1;
        yp = y;
      }
      break;
    case 'R':
      if (x + 1 < layoutWidth) {
        xp = x + 1;
        yp = y;
      }
      break;
    case 'U':
      if (y + 1 < layoutHeight) {
        xp = x;
        yp = y + 1;
      }
      break;
    case 'D':
      if (y - 1 > 0) {
        xp = x;
        yp = y - 1;
      }
      break;
    }

    if (layout[yp][xp] == CellTypes.Open) {
      System.out.println("You won!");
    }
    if (layout[yp][xp] == CellTypes.Rock || layout[yp][xp] == CellTypes.Wall
        || layout[yp][xp] == CellTypes.Closed) {
      return;
    }
    if (layout[yp][xp] == CellTypes.Lambda) {
      totalPoints += 25;
      totalLambdas += 1;
      lambdaPos.remove(new Point(xp, yp)); // careful order!
    }

    layout[y][x] = CellTypes.Empty;
    layout[yp][xp] = CellTypes.Robot;
    robot.getPos().x = xp;
    robot.getPos().y = yp;
    totalPoints -= 1;
  }

  public void move(String move) // same question as above
  {
    for (char m : move.toCharArray()) {
      move(m);
    }
  }

  public void update() // again
  {

    if (ticks % waterRate == waterRate - 1) {
      waterLevel += 1;
    }
    for (int i = 0; i < layoutHeight; ++i) {
      for (int j = 0; j < layoutWidth; ++j) {
        if (layout[i][j] == CellTypes.Closed && lambdaPos.size() == 0) {
          layout[i][j] = CellTypes.Open;
        }
        if (layout[i][j] == CellTypes.Rock) {

        }
      }
    }
  }

  public void checkConditions() {

  }

  public void tick() {

    // move(robot.getNextMove());
    update();
    checkConditions();
    ticks += 1;
  }

}
