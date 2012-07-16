import java.util.*;

public class HungrySkynet extends Skynet {

  abstract class Strategy implements Comparable<Strategy> {
    Board board;
    Path path;
    double value;

    public abstract void init(Board currentBoard);
    public abstract boolean exec();
    public abstract void vary();

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
      value = -2;
    }
    
    public boolean exec() {
      board.tick(Robot.Move.Shave);
      return true;
    }

    public void vary() {
      value = Double.NEGATIVE_INFINITY;
    }
  }

  public class RandomLambda extends Strategy {
    public void init(Board init) {
      board = init;
      path = null;
      value = -1;
    }

    public boolean exec() {
      Random rand = new Random();
      System.err.println(board.lambdaPos.size());
      Point lambdaPt = board.lambdaPos.get(rand.nextInt(board.lambdaPos.size()));
      TerminationConditions.PointTermination terminator
        = new TerminationConditions.PointTermination(lambdaPt);
      AStar pathfinder = new AStar(new CostFunctions.BoardSensingCost(),
                                   new CostFunctions.ManhattanCost(),
                                   terminator);
      pathfinder.setTimeout(3000);
      final boolean finished = pathfinder.findPath(board, lambdaPt);

      // save path
      if (finished &&
          terminator.getBoard().robby.getScore() > bestScore) {
        board = terminator.getBoard();
        path = terminator.getPath();
      }

      return finished;
    }

    public void vary() {
    }
  }
      

  public class Greediest extends Strategy {
    static final int NUM_LAMBDAS_PER_ITERATION = 3;
    Queue<Point> nearestPoints;
    int numIterations;

    public void init(Board init) {
      board = init;
      path = null;
      value = 2;
      numIterations = NUM_LAMBDAS_PER_ITERATION;
    }

    public boolean exec() {
      // System.err.println("Starting Greediest");
      nearestPoints = findClosestLambdas(numIterations);

      double bestScore = board.robby.getScore();
      Board bestBoard = board;

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

    public void vary() {
      numIterations += NUM_LAMBDAS_PER_ITERATION;
      value -= 5;
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
    strategies.add(new RandomLambda());
    history.add(new History(new Path(), new Board(curBoard), -1));
  }

  @Override
  public String plan() {
    while(true)
    {
      if (Main.gotSIGINT)
        break;

      Strategy bestStrategy = null;
      History curHistory = history.peekLast();
      // if (curHistory != null)
      //   System.err.println(curHistory.path);

      double bestValue = Double.NEGATIVE_INFINITY;
      int bestIndex = -1;
      for (int i = 0; i < strategies.size(); i++) {
        Strategy s = strategies.get(i);
        s.init(curBoard);
        if (curHistory != null
            && curHistory.triedChildren.contains(i)) {
          continue;
        }
        // System.err.println(s + " " + s.getValue());
        if (s.getValue() > bestValue) {
          bestStrategy = s;
          bestValue = s.getValue();
          bestIndex = i;
        }
      }

      boolean finished = bestStrategy.exec();
      // System.err.println(finished);
      System.err.println(bestStrategy);

      if (Main.gotSIGINT) {
        break;
      }

      curBoard = bestStrategy.getBoard();

      System.err.println(curBoard);

      history.add(new History(bestStrategy.getPath(), new Board(curBoard), bestIndex));
      if (curBoard.robby.getScore() > bestScore) {
        bestPath = new Path();
        for (History h : history) {
          bestPath.addAll(h.path);
        }
        bestScore = curBoard.robby.getScore();
        bestBoard = curBoard;
      }

      if (Main.gotSIGINT)
        break;

      if (curBoard.lambdaPos.size() == 0 && curBoard.higherOrderCount == 0) {
        System.err.println("Finishing");
        finish();
      }

      if (curBoard.state == Board.GameState.Win) break;

      if (curBoard.state == Board.GameState.Lose) {
        if (!backtrack())
          break;
      }

      if (stuck()) {
        System.err.println("Hey, I'm stuck!");
        if (!backtrack())
          break;
      }
    }

    if (bestPath != null) {
      curBoard = bestBoard;
      return bestPath.toString();
    } else {
      return "A";
    }
  }

  boolean backtrack() {
    History last = history.pollLast();
    History cur = history.peekLast();
    if (cur != null) {
      cur.triedChildren.add(last.strategyIndex);
      curBoard = cur.board;
      System.err.println(cur.triedChildren.size());
      if (cur.triedChildren.size() == strategies.size()) {
        return backtrack();
      }
      return true;
    }
    System.err.println("no history");
    return false;
  }

  boolean stuck() {
    if (history.size() == 1) return false;
    History last = history.get(history.size()-2);
    return last.board.getRobotPosition().equals(curBoard.getRobotPosition());
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
