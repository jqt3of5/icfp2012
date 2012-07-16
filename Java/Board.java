import java.util.*;

public class Board implements Cloneable {

  private static CellTypes[][] tempMap;
  private static int tempHeight;
  private static int tempWidth;

  public enum CellTypes {
    Robot("R"), Rock("*"), Closed("L"), Earth("."),
    Wall("#"), Lambda("\\"), Open("O"), Empty(" "),
    Tramp(""), Target(""), Beard("W"), TempBeard("w"),
    Razor("!"), HigherOrder("@");

    private String rep;
    private CellTypes(String inRep) { rep = inRep; }
    public String toString() { return rep; }
  }

  public enum GameState { Win, Lose, Abort, Continue }

  public int waterLevel;
  public int waterRate;
  public int robotWaterLimit;
  public int growthRate;
  public int higherOrderCount;
  public ArrayList<Point> lambdaPos;
  public Point liftLocation;
  public CellTypes[][] map;
  public Robot robby;

  //  public ArrayList<Point> trampolines;
  public HashMap<Point, Point> trampToTargets;
  public HashMap<Point, String> trampLabel;
  public HashMap<Point, String> targetLabel;
  public ArrayList<Point> tempBeards;
  public ArrayList<Point> beards;
  public ArrayList<Point> razors;
  public int width;
  public int height;
  public int ticks;

  public GameState state;

  // private Board(int width, int height) {
  // ticks = 0;
  // width = width;
  // height = height;
  // // rep = new BoardRep(height, width);//new CellTypes[height][width];
  // waterLevel = 0;
  // waterRate = 0;
  // growthRate = 25;
  // razorCount = 0;
  // lambdaPos = new ArrayList<Point>();
  // }

  public Board(final String mapStr) {
    ticks = 0;
    lambdaPos = new ArrayList<Point>();
    //    trampolines = new ArrayList<Point>();
    trampToTargets = new HashMap<Point, Point>();
    beards = new ArrayList<Point>();
    razors = new ArrayList<Point>();
    tempBeards = new ArrayList<Point>();
    trampLabel = new HashMap<Point, String>();
    targetLabel = new HashMap<Point, String>();

    state = GameState.Continue;

    final String[] lines = mapStr.split("\\r\\n|\\r|\\n");

    // Parse map
    width = 0;
    int i;
    for (i = 0; i < lines.length; i++) {
      if (lines[i].length() == 0)
        break;

      if (lines[i].length() > width) {
        width = lines[i].length();
      }
    }
    height = i;

    map = new CellTypes[height][width];

    // Parse metadata
    waterLevel = 0;
    waterRate = 0;
    growthRate = 25;
    int razorCount = 0;
    higherOrderCount = 0;
    final HashMap<String, String> labelTolabel = new HashMap<String, String>();
    final HashMap<Point, String> trampToLabel = new HashMap<Point, String>();
    final HashMap<String, Point> labelToTarget = new HashMap<String, Point>();

    for (; i < lines.length; i++) {
      if (lines[i].length() == 0)
        continue;

      final String[] words = lines[i].split(" ");
      final String type = words[0];
      if (type.equals("Water"))
        waterLevel = Integer.parseInt(words[1]);
      else if (type.equals("Flooding"))
        waterRate = Integer.parseInt(words[1]);
      else if (type.equals("Waterproof"))
        robotWaterLimit = Integer.parseInt(words[1]);
      else if (type.equals("Growth"))
        growthRate = Integer.parseInt(words[1]);
      else if (type.equals("Razors"))
        razorCount = Integer.parseInt(words[1]);
      else if (type.equals("Trampoline"))
        labelTolabel.put(words[1], words[3]);
    }

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; ++c) {
        final String line = lines[height - 1 - r];

        if (line.length() <= c) {
          map[r][c] = CellTypes.Empty;
          continue;
        }

        switch (line.charAt(c)) {
        case '@':
          map[r][c] = CellTypes.HigherOrder;
          higherOrderCount += 1;
          break;
        case '*':
          map[r][c] = CellTypes.Rock;
          break;
        case '#':
          map[r][c] = CellTypes.Wall;
          break;
        case 'R':
          map[r][c] = CellTypes.Robot;
          robby = new Robot(new Point(r, c), razorCount);
          break;
        case '.':
          map[r][c] = CellTypes.Earth;
          break;
        case '\\':
          map[r][c] = CellTypes.Lambda;
          lambdaPos.add(new Point(r, c)); // careful the order
          break;
        case 'L':
          map[r][c] = CellTypes.Closed;
          liftLocation = new Point(r, c);
          break;
        case ' ':
          map[r][c] = CellTypes.Empty;
          break;
        case 'O':
          map[r][c] = CellTypes.Open;
          break;
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
          map[r][c] = CellTypes.Tramp;
          trampLabel.put(new Point(r, c), Character.toString(line.charAt(c)));
          trampToLabel.put(new Point(r, c), Character.toString(line.charAt(c)));
          //          trampolines.add(new Point(r, c));
          break;
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          targetLabel.put(new Point(r, c), Character.toString(line.charAt(c)));
          map[r][c] = CellTypes.Target;
          // conversion, so that tramps and targets have same labels.
          labelToTarget
            .put(Character.toString(line.charAt(c)), new Point(r, c));

          break;

        case 'W':
          map[r][c] = CellTypes.Beard;
          beards.add(new Point(r, c));
          break;
        case '!':
          map[r][c] = CellTypes.Razor;
          razors.add(new Point(r, c));
          break;
        }
      }
    }

    for (final Point p : trampToLabel.keySet()) {
      final String trampLabel = trampToLabel.get(p);
      final String targetLabel = labelTolabel.get(trampLabel);
      final Point target = labelToTarget.get(targetLabel);

      trampToTargets.put(p, target);
    }

    System.out.println("Creating a board from string");
  }

  public Board(final Board oldBoard) {
    waterLevel = oldBoard.waterLevel;
    waterRate = oldBoard.waterRate;
    robotWaterLimit = oldBoard.robotWaterLimit;
    growthRate = oldBoard.growthRate;
    lambdaPos = new ArrayList<Point>(oldBoard.lambdaPos);
    liftLocation = oldBoard.liftLocation;
    higherOrderCount = oldBoard.higherOrderCount;
    width = oldBoard.width;
    height = oldBoard.height;

    map = new CellTypes[height][width];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        map[r][c] = oldBoard.map[r][c];
      }
    }

    robby = new Robot(oldBoard.robby);

    trampLabel = new HashMap<Point, String>(oldBoard.trampLabel);
    targetLabel = new HashMap<Point, String>(oldBoard.targetLabel);
    //    trampolines = new ArrayList<Point>(oldBoard.trampolines);
    trampToTargets = new HashMap<Point, Point>(oldBoard.trampToTargets);

    tempBeards = new ArrayList<Point>(oldBoard.tempBeards);
    beards = new ArrayList<Point>(oldBoard.beards);
    razors = new ArrayList<Point>(oldBoard.razors);
    ticks = oldBoard.ticks;

    state = oldBoard.state;
  }

  public BoardState getBoardState() {
    return new BoardState(new Board(this));
  }

  @Override
  public String toString() {
    StringBuilder s;
    List<String> lines = new ArrayList<String>();
    for (int y = 0; y < height; y++) {
      s = new StringBuilder();
      for (int x = 0; x < width; x++) {
        if (map[y][x] == CellTypes.Tramp) {
          s.append(trampLabel.get(new Point(y, x)));
          // s.append((char)('A' + trampolines.indexOf(new Point(y,x))));
        } else if (map[y][x] == CellTypes.Target) {
          s.append(targetLabel.get(new Point(y, x)));
          // s.append((char)('1' + trampolines.indexOf(new Point(y,x))));
        } else {
          s.append(map[y][x]);
        }
      }
      lines.add(s.toString());
    }
    Collections.reverse(lines);

    s = new StringBuilder();

    for (int i = 0; i < lines.size() - 1; i++) {
      s.append(lines.get(i));
      s.append("\n");
    }

    s.append(lines.get(lines.size() - 1));

    return s.toString();
  }

  // destructive update
  public GameState move(final Robot.Move move) {
    final int x = robby.position.c;
    final int y = robby.position.r;
    int xp = 0, yp = 0;

    switch (move) {
    case Left:
      xp = x - 1;
      yp = y;
      break;
    case Right:
      xp = x + 1;
      yp = y;
      break;
    case Up:
      xp = x;
      yp = y + 1;
      break;
    case Down:
      xp = x;
      yp = y - 1;
      break;
    case Wait:
      return GameState.Continue;
    case Abort:
      return GameState.Abort;
    case Shave:
      if (robby.razorCount < 1)
        return GameState.Continue;
      for (int i = y - 1; i < 3; ++i)
        for (int j = x - 1; j < 3; ++j) {
          if (map[i][j] == CellTypes.Beard) {
            // temp beards are because we want to differentiate bettween new
            // beards, and old.
            // else, out of control growth.
            map[i][j] = CellTypes.Empty;
            beards.remove(new Point(i, j));
          }
        }
      robby.razorCount--;
      return GameState.Continue;

    }

    if (map[yp][xp] == CellTypes.Razor) {
      robby.razorCount++;
    }
    // if we get to the exit and it is open, we win
    if (map[yp][xp] == CellTypes.Open) {
      map[y][x] = CellTypes.Empty;
      map[yp][xp] = CellTypes.Robot;
      robby.setPosition(yp, xp);

      robby.liftLambda();
      return GameState.Win;
    }
    // cannot go through a wall, or a closed lift, or a beard
    if (map[yp][xp] == CellTypes.Wall || map[yp][xp] == CellTypes.Closed
        || map[yp][xp] == CellTypes.Beard) {
      return GameState.Continue;
    }
    // we stumbled on a lambda! pick it up
    if (map[yp][xp] == CellTypes.Lambda) {
      robby.gainLambda();
      lambdaPos.remove(new Point(yp, xp)); // careful order!
    }
    // if we can move the rock left/right or we just tried to run into it
    if (move == Robot.Move.Left
        && (map[yp][xp] == CellTypes.Rock || map[yp][xp] == CellTypes.HigherOrder)
        && map[yp][xp - 1] == CellTypes.Empty) {
      map[yp][xp - 1] = map[yp][xp];
    } else if (move == Robot.Move.Right
               && (map[yp][xp] == CellTypes.Rock || map[yp][xp] == CellTypes.HigherOrder)
               && map[yp][xp + 1] == CellTypes.Empty) {
      map[yp][xp + 1] = map[yp][xp];
    } else if (map[yp][xp] == CellTypes.Rock
               || map[yp][xp] == CellTypes.HigherOrder) {
      // cant move this rock
      return GameState.Continue;
    }

    // if we step on a tramp, find our coresponding target, and set that.

    if (map[yp][xp] == CellTypes.Tramp) {


      final Point target = trampToTargets.get(new Point(yp, xp));

      map[yp][xp] = CellTypes.Empty;

      xp = target.c;
      yp = target.r;

      // this is so we can remove all tramps that jump to this target.
      for (Point p : new ArrayList<Point>(trampToTargets.keySet()))
	{
	  if (trampToTargets.get(p) == target)
	    {
	      map[p.r][p.c] = CellTypes.Empty;
	      trampToTargets.remove(p);
	    }
	}

    }
    // update our position
    map[y][x] = CellTypes.Empty;
    map[yp][xp] = CellTypes.Robot;
    robby.setPosition(yp, xp);
    return GameState.Continue;
  }

  // destructive update
  private GameState move(final List<Robot.Move> moves) {
    for (final Robot.Move m : moves) {
      final GameState state = move(m);
      if (state != GameState.Continue) {
        return state;
      }
    }
    return GameState.Continue;
  }

  /*
   * Ensure that tempMap has the correct dimensions
   * this is NOT thread-safe!!!
   */
  private void ensureBufferValid() {
    if (tempHeight == height && tempWidth == width) {
      return;
    }

    tempMap = new CellTypes[height][width];
  }

  private void swapBuffers() {
    CellTypes[][] temp = tempMap;
    tempMap = map;
    map = temp;
  }

  // destructive update
  public GameState update() {
    GameState returnState = GameState.Continue;

    if (robby.getPosition().r >= waterLevel)
      robby.cleanWaterTime();

    if (waterRate != 0 && ticks % waterRate == waterRate - 1) {
      waterLevel += 1;
    }

    if (robby.getPosition().r < waterLevel)
      robby.stayInWater();

    if (robby.waterTime > 0 && robby.waterTime > robotWaterLimit) {
      // System.err.println("Underwater too long!!!");
      // System.err.println("Current time: " + ticks + " waterTime: " + robby.waterTime);
      returnState = GameState.Lose;
    }

    ensureBufferValid();

    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        tempMap[y][x] = map[y][x];

        if (map[y][x] == CellTypes.Closed && lambdaPos.size() == 0 && higherOrderCount == 0) {
          tempMap[y][x] = CellTypes.Open;
        }
        // grow beards

        if (growthRate > 0 && (ticks % growthRate == (growthRate - 1))
            && map[y][x] == CellTypes.Beard) {

          for (int i = y - 1; i < y+2; ++i)
            for (int j = x - 1; j < x+2; ++j) {
              if (map[i][j] == CellTypes.Empty) {
                // temp beards are because we want to differentiate bettween new
                // beards, and old.
                // else, out of control growth.
                tempMap[i][j] = CellTypes.TempBeard;
                tempBeards.add(new Point(i, j));
                beards.add(new Point(i, j));
              }
            }
        }

        if (map[y][x] == CellTypes.Rock || map[y][x] == CellTypes.HigherOrder) {

          int xp = 0, yp = 0;
          if (y - 1 > 0 && map[y - 1][x] == CellTypes.Empty) {
            xp = x;
            yp = y - 1;
          } else if (y - 1 > 0
                     && x + 1 < width - 1
                     && (map[y - 1][x] == CellTypes.Rock || map[y][x] == CellTypes.HigherOrder)
                     && map[y][x + 1] == CellTypes.Empty
                     && map[y - 1][x + 1] == CellTypes.Empty) {
            xp = x + 1;
            yp = y - 1;
          } else if (y - 1 > 0
                     && x + 1 < width - 1
                     && x - 1 > 0
                     && (map[y - 1][x] == CellTypes.Rock || map[y][x] == CellTypes.HigherOrder)
                     && (map[y][x + 1] != CellTypes.Empty || map[y - 1][x + 1] != CellTypes.Empty)
                     && map[y][x - 1] == CellTypes.Empty
                     && map[y - 1][x - 1] == CellTypes.Empty) {
            xp = x - 1;
            yp = y - 1;
          } else if (y - 1 > 0 && x + 1 < width - 1
                     && map[y - 1][x] == CellTypes.Lambda
                     && map[y][x + 1] == CellTypes.Empty
                     && map[y - 1][x + 1] == CellTypes.Empty) {
            xp = x + 1;
            yp = y - 1;
          } else {
            continue;
          }

          tempMap[yp][xp] = map[y][x];
          tempMap[y][x] = CellTypes.Empty;

          if (yp > 0 && map[yp - 1][xp] == CellTypes.Robot) {
            returnState = GameState.Lose;
          }

          if (map[y][x] == CellTypes.HigherOrder &&
              yp > 0 && map[yp-1][xp] != CellTypes.Empty)
	    {
	      tempMap[yp-1][xp] = CellTypes.Lambda;
	      lambdaPos.add(new Point(yp-1,xp));
	      higherOrderCount--;
	    }

        }
      }
    }

    // need to change the new beards for this round into permanent beards
    if (growthRate > 0 && ticks % growthRate == growthRate - 1) {
      for (Point p : tempBeards) {
        tempMap[p.r][p.c] = CellTypes.Beard;

      }
      tempBeards.clear();
    }

    swapBuffers();
    return returnState;
  }

  // destructive update
  public GameState tick(final Robot.Move nextMove) {
    if (nextMove == Robot.Move.Abort) {
      state = GameState.Abort;
      return state;
    }

    // System.out.println("Before move:");
    // System.out.println(this);

    state = move(nextMove);
    if (state != GameState.Continue)
      return state;

    // System.out.println("Before update:");
    // System.out.println(this);

    state = update();

    if (state != GameState.Continue)
      return state;
    ticks += 1;

    // System.out.println("After update:");
    // System.out.println(this);

    state = GameState.Continue;
    return state;
  }

  // public void revert(final BoardState revertState) {
  // rep.revert(revertState.deltaId);
  // }

  public Point getRobotPosition() {
    return robby.getPosition();
  }

  public List<Robot.Move> getAvailableMoves() {
    final List<Robot.Move> retList = new ArrayList<Robot.Move>();
    final int[] dr = { -1, 0, 0, 1 };
    final int[] dc = { 0, -1, 1, 0 };
    final Robot.Move[] robotMove = { Robot.Move.Down, Robot.Move.Left,
                                     Robot.Move.Right, Robot.Move.Up};

    // TODO(jack): need to handle trampoline and possibly death
    // conditions here.

    for (int i = 0; i < dr.length; i++) {
      final int r = robby.getPosition().r + dr[i];
      final int c = robby.getPosition().c + dc[i];
      if (0 <= r && r < height && 0 <= c && c < width
          && map[r][c] != CellTypes.Wall
	  && map[r][c] != CellTypes.Beard
	  && map[r][c] != CellTypes.Closed) {

        // Can't push rocks up or down
        if ((robotMove[i] == Robot.Move.Down ||
	     robotMove[i] == Robot.Move.Up)
            && map[r][c] == CellTypes.Rock) {
          continue;
        }

	// Can't push rocks to nonempty cells
	if (robotMove[i] == Robot.Move.Left
            && map[r][c] == CellTypes.Rock
	    && getCell(r, c-1) != CellTypes.Empty) {
          continue;
        }
	if (robotMove[i] == Robot.Move.Right
            && map[r][c] == CellTypes.Rock
	    && getCell(r, c+1) != CellTypes.Empty) {
          continue;
	}

	// Death conditions
	if (cellWillBeEmpty(r+1, c) &&
	    ( isRock(getCell(r+2, c)) ||
	      (isRock(getCell(r+2, c-1)) &&
	       (getCell(r+1, c-1) == CellTypes.Lambda || isRock(getCell(r+1, c-1)) )) ||
	      (isRock(getCell(r+2, c+1)) &&
	       (isRock(getCell(r+1, c+1)) && getCell(r+1, c+2) != CellTypes.Empty))
	    )) {
	  continue;
	}

	retList.add(robotMove[i]);
      }
    }

    if (robby.razorCount > 0 && adjacentBeard(robby.getPosition().r, robby.getPosition().c))
      retList.add(Robot.Move.Shave);

    // for (Robot.Move b : retList) {
    // System.out.print(b + " ");
    // }
    // System.out.println();

    return retList;
  }

  public boolean cellWillBeEmpty(int r, int c) {
    if (getCell(r, c) != CellTypes.Empty) return false;
    if (growthRate > 0 && (ticks+1) % growthRate == growthRate - 1) {
      if (adjacentBeard(r, c)) return false;
    }
    return true;
  }

  public CellTypes getCell(int r, int c) {
    if (0 <= r && r < height && 0 <= c && c < width) return map[r][c];
    else return CellTypes.Wall;
  }

  private boolean isRock(CellTypes type) {
    return type == CellTypes.Rock || type == CellTypes.HigherOrder;
  }


  private boolean adjacentBeard(int r, int c) {
    if (r-1 >= 0) {
      if (c-1 >= 0 && map[r-1][c-1] == CellTypes.Beard)
        return true;
      if (map[r-1][c-1] == CellTypes.Beard)
        return true;
      if (c+1 < width && map[r-1][c+1] == CellTypes.Beard)
        return true;
    }

    if (c-1 >= 0 && map[r][c-1] == CellTypes.Beard)
      return true;
    if (map[r][c-1] == CellTypes.Beard)
      return true;
    if (c+1 < width && map[r][c+1] == CellTypes.Beard)
      return true;

    if (r+1 < height) {
      if (c-1 >= 0 && map[r+1][c-1] == CellTypes.Beard)
        return true;
      if (map[r+1][c-1] == CellTypes.Beard)
        return true;
      if (c+1 < width && map[r+1][c+1] == CellTypes.Beard)
        return true;
    }

    return false;
  }

  public CellTypes get(Point p) {
    return map[p.r][p.c];
  }
}
