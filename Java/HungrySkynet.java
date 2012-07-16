import java.util.*;

public class HungrySkynet extends Skynet {

  abstract class Strategy implements Comparable<Strategy> {
    Board board;
    Path path;
    double value;

    public abstract void init(Board currentBoard);
    public abstract void exec();

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
      value = 0;
    }
    
    public void exec() {
      board.tick(Robot.Move.Shave);
    }
  }

  public class Greediest extends Strategy {
    static final int NUM_LAMBDAS_PER_ITERATION = 3;

    public void init(Board init) {
      board = init;
      path = new Path();
      value = 1.0;
    }

    public void exec() {
      System.err.println("Starting Greediest");

      double bestScore = board.robby.getScore();
      Board bestBoard = board;
      Path bestPath = null;

      final Queue<Point> nearestPoints = findClosestLambdas();
      int numIterations = NUM_LAMBDAS_PER_ITERATION;
      if (nearestPoints.size() < numIterations)
        numIterations = nearestPoints.size();

      for (int i = 0; i < numIterations ||
             (bestPath == null && i < nearestPoints.size()); i++) {
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

        // save bestPath
        if (finished &&
            terminator.getBoard().robby.getScore() > bestScore) {
          bestBoard = terminator.getBoard();
          bestScore = terminator.getBoard().robby.getScore();
          bestPath = terminator.getPath();
        }

        // System.err.println("Best path: " + bestPath);
        if (Main.gotSIGINT) {
          if (bestPath != null) path.addAll(bestPath);
          return;
        }
      }
      board = bestBoard;

      if (bestPath == null) return;
      path.addAll(bestPath);
    }
  }

  List<Strategy> strategies;
  final LinkedList<History> history = new LinkedList<History>();

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
    History curHistory = history.peekLast();
    while(true)
    {
      System.err.println(curBoard);

      double bestValue = Double.NEGATIVE_INFINITY;
      int i;
      for (i = 0; i < strategies.size(); i++) {
        if (curHistory != null
            && curHistory.triedChildren.indexOf(i) != -1) {
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

      bestStrategy.exec();
      // System.err.println(bestStrategy);

      curBoard = bestStrategy.getBoard();
      history.add(new History(bestStrategy.getPath(), curBoard, i));

      if (curBoard.state == Board.GameState.Win) break;

      if (curBoard.state == Board.GameState.Lose) backtrack();

      if (stuck()) backtrack();

      if (curBoard.lambdaPos.size() == 0 && curBoard.higherOrderCount == 0) {
        finish();
        break;
      }
    }

    StringBuilder s = new StringBuilder();
    for (History h : history) {
      s.append(h.path.toString());
    }
    return s.toString();
  }

  void backtrack() {
    History last = history.pollLast();
    History cur = history.peekLast();
    cur.triedChildren.add(last.strategyIndex);
    curBoard = cur.board;
  }

  boolean stuck() {
    return false;
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

      if (newBoard.robby.getScore() > curBoard.robby.getScore()) {
        history.add(new History(terminator.getPath(), newBoard, 0));
        curBoard = newBoard;
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
