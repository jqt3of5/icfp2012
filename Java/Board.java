import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board implements Cloneable {
  public enum CellTypes {
    Robot, Rock, Closed, Earth, Wall, Lambda, Open, Empty, Tramp, Target, Beard, TempBeard, Razor
  };
  public enum GameState {
    Win, Lose, Abort, Continue
  };

  public Robot robot;
  public int waterLevel;
  public int waterRate;
  public int growthRate;
  public int razorCount;
  public ArrayList<Point> lambdaPos;
  public Point liftLocation;
  public BoardRep rep;

  public ArrayList<Point> trampolines;
  public HashMap<Point, Point> trampToTargets;

  public ArrayList<Point> tempBeards;
  public ArrayList<Point> beards;
  public ArrayList<Point> razors;
  public int layoutWidth;
  public int layoutHeight;
  public int ticks;


  // private Board(int width, int height) {
  //   ticks = 0;
  //   layoutWidth = width;
  //   layoutHeight = height;
  //   // rep = new BoardRep(height, width);//new CellTypes[height][width];
  //   waterLevel = 0;
  //   waterRate = 0;
  //   growthRate = 25;
  //   razorCount = 0;
  //   lambdaPos = new ArrayList<Point>();
  // }

  //needs to handle meta date!

  public Board(final String map) {
    robot = new Robot(0,0);
    ticks = 0;
    lambdaPos = new ArrayList<Point>();
    trampolines = new ArrayList<Point>();
    trampToTargets = new HashMap<Point,Point>();
    beards = new ArrayList<Point>();
    razors = new ArrayList<Point>();
    tempBeards = new ArrayList<Point>();


    final String[] lines = map.split("\\r\\n|\\r|\\n");

    // Parse map
    layoutWidth = 0;
    int i;
    for (i = 0; i < lines.length; i++) {
      if (lines[i] == "")
        break;

      if (lines[i].length() > layoutWidth) {
        layoutWidth = lines[i].length();
      }
    }
    layoutHeight = i;

    final CellTypes[][] layout = new CellTypes[layoutHeight][layoutWidth];

    // Parse metadata
    waterLevel = 0;
    waterRate = 0;
    growthRate = 25;
    razorCount = 0;
    robot.setWaterThreshold(0);

    final HashMap<String, String> labelTolabel = new HashMap<String, String>();
    final HashMap<Point, String> trampToLabel =  new HashMap<Point, String>();
    final HashMap<String, Point> labelToTarget =  new HashMap<String, Point>();

    for (; i < lines.length; i++) {
      final String[] words = lines[i].split(" ");
      final String type = words[0];
      if (type == "Water")
        waterLevel = Integer.parseInt(words[1]);
      else if (type == "Flooding")
        waterRate = Integer.parseInt(words[1]);
      else if (type == "Waterproof")
        robot.setWaterThreshold(Integer.parseInt(words[1]));
      else if (type == "Growths")
        growthRate = Integer.parseInt(words[1]);
      else if (type == "Razors")
        razorCount = Integer.parseInt(words[1]);
      else if (type == "Trampoline")
        labelTolabel.put(words[1], words[3]);
    }

    for (int r = 0; r < layoutHeight; r++) {
      for (int c = 0; c < layoutWidth; ++c) {
        final String line = lines[layoutHeight-1-r];
        switch (line.charAt(c)) {
        case '*':
          layout[r][c] = CellTypes.Rock;
          break;
        case '#':
          layout[r][c] = CellTypes.Wall;
          break;
        case 'R':
          layout[r][c] = CellTypes.Robot;
          robot.setPosition(r,c);
          break;
        case '.':
          layout[r][c] = CellTypes.Earth;
          break;
        case '\\':
          layout[r][c] = CellTypes.Lambda;
          lambdaPos.add(new Point(r, c)); // careful the order
          break;
        case 'L':
          layout[r][c] = CellTypes.Closed;
          liftLocation = new Point(r, c);
          break;
        case ' ':
          layout[r][c] = CellTypes.Empty;
          break;
        case 'O':
          layout[r][c] = CellTypes.Open;
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
          layout[r][c] = CellTypes.Tramp;
          trampToLabel.put(new Point(r, c), Character.toString(line.charAt(c)));
          trampolines.add(new Point(r, c));
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
          layout[r][c] = CellTypes.Target;
          //conversion, so that tramps and targets have same labels.
          labelToTarget.put(Character.toString(line.charAt(c)),new Point(r,c));

        break;

        case 'W':
          layout[r][c] = CellTypes.Beard;
          beards.add(new Point(r,c));
          break;
        case '!':
          layout[r][c] = CellTypes.Razor;
          razors.add(new Point(r,c));
          break;
        }
      }
    }

    for (final Point p : trampolines)
    {
      final String trampLabel = trampToLabel.get(p);
      final String targetLabel = labelTolabel.get(trampLabel);
      final Point target = labelToTarget.get(targetLabel);

      trampToTargets.put(p,  target);
    }

    rep = new BoardRep(layout);
  }


  public Board(final Board oldBoard) {
    robot = new Robot(oldBoard.robot);
    waterLevel = oldBoard.waterLevel;
    waterRate = oldBoard.waterRate;
    lambdaPos = new ArrayList<Point>(oldBoard.lambdaPos);
    liftLocation = oldBoard.liftLocation;
    layoutWidth = oldBoard.layoutWidth;
    layoutHeight = oldBoard.layoutHeight;

    rep = new BoardRep(oldBoard.rep);
    ticks = oldBoard.ticks;
  }

  public BoardState getBoardState() {
    final BoardState state = new BoardState();
    state.position = getRobotPosition();
    state.move = null;
    state.deltaId = rep.globalDeltaId;
    return state;
  }

  @Override
  public String toString() {
    final StringBuilder s = new StringBuilder();
    for (int y = 0; y < layoutHeight; y++) {
      for (int x = 0; x < layoutWidth; x++) {
        s.append(rep.get(y,x));
      }
      s.append("\n");
    }
    return s.toString();
  }

  public GameState move(final Robot.Move move) // should change internal state, or create a new
  {
    final int x = robot.getPosition().getC();
    final int y = robot.getPosition().getR();
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
      if (razorCount < 1)
        return GameState.Continue;
      for (int i = y-1; i < 3; ++i)
        for (int j = x-1; j < 3; ++j)
        {
          if (rep.get(i,j) == CellTypes.Beard)
          {
            //temp beards are because we want to differentiate bettween new beards, and old.
            //else, out of control growth.
            rep.set(i, j, CellTypes.Empty);
            beards.remove(new Point(i,j));
          }
        }
      razorCount--;
      return GameState.Continue;

    }


    if (rep.get(yp,xp) == CellTypes.Razor)
    {
      razorCount += 1;
    }
    //if we get to the exit and it is open, we win
    if (rep.get(yp,xp) == CellTypes.Open) {
      return GameState.Win;
    }
    //cannot go through a wall, or a closed lift, or a beard
    if (rep.get(yp,xp) == CellTypes.Wall ||
        rep.get(yp,xp) == CellTypes.Closed ||
        rep.get(yp,xp) == CellTypes.Beard) {
      return GameState.Continue;
    }
    //we stumbled on a lambda! pick it up
    if (rep.get(yp,xp) == CellTypes.Lambda) {
     robot.gainLambda();
       lambdaPos.remove(new Point(yp, xp)); // careful order!
    }
    //if we can move the rock left/right or we just tried to run into it
    if (move == Robot.Move.Left && rep.get(yp,xp) == CellTypes.Rock && rep.get(yp, xp-1) == CellTypes.Empty)
    {
     rep.set(yp,xp-1, CellTypes.Rock);
    }
    else if (move == Robot.Move.Right && rep.get(yp,xp) == CellTypes.Rock && rep.get(yp, xp+1) == CellTypes.Empty)
    {
      rep.set(yp, xp+1,CellTypes.Rock);
    }
    else if (rep.get(yp, xp) == CellTypes.Rock)
    {
      //cant move this rock
      return GameState.Continue;
    }

    //if we step on a tramp, find our coresponding target, and set that.

    if (rep.get(yp, xp) == CellTypes.Tramp)
    {

      final Point target = trampToTargets.get(new Point(yp, xp));

      trampToTargets.remove(new Point(yp, xp));
      trampolines.remove(new Point(yp, xp));

      //this is so we can remove all tramps that jump to this target.
      if (trampToTargets.containsValue(target))
      {
        for (final Point tramp : trampolines)
        {
          if (trampToTargets.get(tramp) == target)
          {
            trampToTargets.remove(tramp);
            trampolines.remove(tramp);
          }
        }
      }

      rep.set(yp,xp,CellTypes.Empty);
      xp = target.c;
      yp = target.r;

    }
    //update our position
    rep.set(y,x,CellTypes.Empty);
    rep.set(yp,xp,CellTypes.Robot);
    robot.setPosition(yp, xp);
    return GameState.Continue;
  }

  private GameState move(final List<Robot.Move> moves) // same question as above, dont use
  {
    for (final Robot.Move m : moves) {
      final GameState state = move(m);
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

        if (rep.get(y,x) == CellTypes.Closed && lambdaPos.size() == 0) {
          rep.set(y,x,CellTypes.Open);
        }
        //grow beards
        if(ticks%growthRate == growthRate-1 && rep.get(y,x) == CellTypes.Beard)
        {
              for (int i = y-1; i < 3; ++i)
                for (int j = x-1; j < 3; ++j)
                {
                  if (rep.get(i,j) == CellTypes.Empty)
                  {
                    //temp beards are because we want to differentiate bettween new beards, and old.
                    //else, out of control growth.
                    rep.set(i,j,CellTypes.TempBeard);
                    tempBeards.add(new Point(i,j));
                    beards.add(new Point(i,j));
                  }
                }
        }

        if (rep.get(y,x) == CellTypes.Rock) {

          int xp = 0, yp = 0;
          if (y-1 > 0 && rep.get(y-1,x) == CellTypes.Empty)
          {
            xp = x;
            yp = y-1;
          }
          if (y-1 > 0 && x+1 < layoutWidth-1 &&
              rep.get(y-1, x) == CellTypes.Rock &&
              rep.get(y, x+1) == CellTypes.Empty &&
              rep.get(y-1, x+1) == CellTypes.Empty )
          {
            xp = x+1;
            yp = y-1;
          }
          if (y-1 > 0 && x+1 < layoutWidth-1 && x-1 > 0 &&
              rep.get(y-1, x) == CellTypes.Rock &&
              (rep.get(y, x+1) != CellTypes.Empty || rep.get(x+1, y-1) != CellTypes.Empty) &&
              rep.get(y, x-1)== CellTypes.Empty &&
              rep.get(y-1, x-1) == CellTypes.Empty)
          {
            xp = x-1;
            yp = y-1;
          }
          if (y-1 > 0 && x+1 < layoutWidth-1 &&
              rep.get(y-1, x) == CellTypes.Lambda &&
              rep.get(y, x+1) == CellTypes.Empty &&
              rep.get(y-1, x+1) == CellTypes.Empty)
          {
            xp = x+1;
            yp = y-1;
          }

          rep.set(y,x,CellTypes.Empty);
          rep.set(yp, xp, CellTypes.Rock);
          if (rep.get(yp-1, xp) == CellTypes.Robot)
          {
            return GameState.Lose;
          }

        }
      }
    }

    //need to change the new beards for this round into permanent beards
    if (ticks%growthRate == growthRate-1)
    {
      for (final Point p : tempBeards)
      {
        rep.set(p.r, p.c, CellTypes.Beard);
        tempBeards.remove(p);
      }
    }
    return GameState.Continue;
  }

  public void displayBoard()
  {
    for (int y = 0; y < layoutHeight; ++y)
    {
      for(int x = 0; x < layoutWidth; ++x)
      {
        switch(rep.get(y,x))
        {
          case Robot:
            System.out.print('R');
            break;
          case Rock:
            System.out.print('*');
            break;
          case Empty:
            System.out.print(' ');
            break;
          case Earth:
            System.out.print('.');
            break;
          case Lambda:
            System.out.print('/');
            break;
          case Closed:
            System.out.print('L');
            break;
          case Open:
            System.out.print('O');
            break;
          case Beard:
            System.out.print('W');
            break;
          case Razor:
            System.out.print('!');
            break;
          case Wall:
            System.out.print('#');
            break;
          case Tramp:
            System.out.print('A');
            break;
          case Target:
            System.out.print('1');
            break;
        }
      }
      System.out.print("\n");
    }
  }
  public GameState tick(final Robot.Move nextMove) {
    GameState state;
    if (nextMove == Robot.Move.Abort)
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

  public void revert(final BoardState revertState) {
    rep.revert(revertState.deltaId);
  }

  public Point getRobotPosition() {
    return robot.getPosition();
  }

  public List<BoardState> getAvailableMoves() {
    final List<BoardState> retList = new ArrayList<BoardState>();
    final int[] dr = {-1, 0, 0, 1};
    final int[] dc = {0, -1, 1, 0};
    final Robot.Move[] robotMove = {Robot.Move.Down, Robot.Move.Left,
			      Robot.Move.Right, Robot.Move.Up};

    // TODO(jack): need to handle trampoline and possibly death
    // conditions here.

    for (int i = 0; i < dr.length; i++) {
      final int r = robot.getPosition().r + dr[i];
      final int c = robot.getPosition().c + dc[i];
      if (0 <= r && r < layoutHeight && 0 <= c && c < layoutWidth &&
          rep.get(r, c) != CellTypes.Wall) {
        final BoardState state = new BoardState();
        state.position = new Point(r, c);
        state.move = robotMove[i];
        state.deltaId = rep.globalDeltaId;
      }
    }

    return retList;
  }
}
