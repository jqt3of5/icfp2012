import java.util.*;

public class HungrySkynet extends Skynet {

  abstract class Strategy implements Comparable<Strategy> {
    Board board;
    Path path;
    double value;

    public abstract void init(Board currentBoard);
    public abstract boolean exec();

    public Board getBoard() { return board; }
    public Path getPath() {return path; }
    public double getValue() { return value; }

    public int compareTo(Strategy arg1) {
      return (this.getValue() - arg1.getValue() == 0 ? 0 : (int)(this.getValue()
                                                           - arg1.getValue()));
    }

  }

  public class Shave extends Strategy
  {
    Path stratPath = new Path();

    @Override
    public void init(Board currentBoard) {
      board = currentBoard;
      path = new Path();
      value = -1;
    }
    
    public boolean exec() {
      board.tick(Robot.Move.Shave);
      return true;
    }
  }

  public class Greediest extends Strategy {
    static final int NUM_LAMBDAS_PER_ITERATION = 3;
    Queue<Point> nearestPoints;

    public void init(Board init) {
      board = init;
      path = null;
      nearestPoints = findClosestLambdas();
      value = nearestPoints.size();
    }

    public boolean exec() {
      System.err.println("Starting Greediest");

      double bestScore = board.robby.getScore();
      Board bestBoard = board;

      int numIterations = NUM_LAMBDAS_PER_ITERATION;
      if (nearestPoints.size() < numIterations)
        numIterations = nearestPoints.size();

      for (int i = 0; i < numIterations ||
             (path == null && i < nearestPoints.size()); i++) {
        final Point lambdaPt = nearestPoints.remove();
        // System.err.println("Finding: " + lambdaPt);

        final Board newBoard = new Board(board);
        TerminationConditions.PointTermination terminator
          = new TerminationConditions.PointTermination(lambdaPt);
        AStar pathfinder = new AStar(new CostFunctions.BoardSensingCost(),
                                     new CostFunctions.ManhattanCost(),
                                     terminator);
        pathfinder.setTimeout(3000);
        final boolean finished = pathfinder.findPath(newBoard, lambdaPt);

        // save path
        if (finished &&
            terminator.getBoard().robby.getScore() > bestScore) {
          bestBoard = terminator.getBoard();
          bestScore = terminator.getBoard().robby.getScore();
          path = terminator.getPath();
        }

        // System.err.println("Best path: " + path);
        if (Main.gotSIGINT) {
          return false;
        }
      }
      board = bestBoard;
      return true;
    }
  }

  List<Strategy> strategies;
  final LinkedList<History> history = new LinkedList<History>();
  Path bestPath = null;
  int bestScore = Integer.MIN_VALUE;
  Board bestBoard = null;

  public HungrySkynet(String mapStr) {
    super(mapStr);
    strategies = new ArrayList<Strategy>();
    strategies.add(new Shave());
    strategies.add(new Greediest());
  }

  @Override
  public String plan() {
    Strategy bestStrategy = null;
    BoardState curState = null;
    while(true)
    {
      if (Main.gotSIGINT)
        break;

      History curHistory = history.peekLast();
      if (curHistory != null)
        System.err.println(curHistory.path);

      double bestValue = Double.NEGATIVE_INFINITY;
      int i;
      for (i = 0; i < strategies.size(); i++) {
        if (curHistory != null
            && curHistory.triedChildren.contains(i)) {
          continue;
        }
        Strategy s = strategies.get(i);
        s.init(curBoard);
        // System.err.println(s + " " + s.getValue());
        if (s.getValue() > bestValue) {
          bestStrategy = s;
          bestValue = s.getValue();
        }
      }

      if (bestValue == Double.NEGATIVE_INFINITY) {
        backtrack();
      }

      boolean finished = bestStrategy.exec();
      // System.err.println(bestStrategy);

      if (!finished || Main.gotSIGINT) {
        break;
      }

      curBoard = bestStrategy.getBoard();
      history.add(new History(bestStrategy.getPath(), new Board(curBoard), i));
      if (curBoard.robby.getScore() > bestScore) {
        bestPath = new Path();
        for (History h : history) {
          bestPath.addAll(h.path);
        }
        bestScore = curBoard.robby.getScore();
        bestBoard = curBoard;
      }

      System.err.println(curBoard);

      if (Main.gotSIGINT)
        break;

      if (curBoard.lambdaPos.size() == 0 && curBoard.higherOrderCount == 0) {
        System.err.println("Finishing");
        finish();
      }

      if (curBoard.state == Board.GameState.Win) break;

      if (curBoard.state == Board.GameState.Lose) backtrack();

      if (stuck()) {
        System.err.println("Hey, I'm stuck!");
        backtrack();
      }
    }

    if (bestPath != null) {
      curBoard = bestBoard;
      return bestPath.toString();
    } else {
      return "A";
    }
  }

  void backtrack() {
    History last = history.pollLast();
    History cur = history.peekLast();
    cur.triedChildren.add(last.strategyIndex);
    curBoard = cur.board;
  }

  static final Board.CellTypes[] targetTypes = new Board.CellTypes[]{
    Board.CellTypes.Lambda
  }; // TODO - add more as strategies exist for them
  boolean stuck() {
    Queue<Point> targets = findClosest(targetTypes, 1);
    return targets.size() == 0;
  }

  void finish() {
    Board newBoard = new Board(curBoard);
    TerminationConditions.PointTermination terminator = new TerminationConditions
      .PointTermination(curBoard.liftLocation);
    AStar pathfinder = new AStar(new CostFunctions.BoardSensingCost(),
                           new CostFunctions.ManhattanCost(), terminator);
    boolean finished = pathfinder.findPath(newBoard, curBoard.liftLocation);
    if (finished) {
      newBoard = terminator.getBoard();

      if (newBoard.robby.getScore() > bestScore) {
        history.add(new History(terminator.getPath(), newBoard, 0));
        curBoard = newBoard;
        bestBoard = newBoard;
        bestScore = newBoard.robby.getScore();
        bestPath = new Path();
        for (History h : history) {
          bestPath.addAll(h.path);
        }
      }
    }
  }

  class History {
    Path path;
    Board board;
    int strategyIndex;
    LinkedList<Integer> triedChildren = new LinkedList<Integer>();

    public History(Path p, Board b, int strat) {
      path = p;
      board = b;
      strategyIndex = strat;
    }
  }
}
