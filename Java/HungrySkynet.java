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
    double value = -1;
    Path stratPath = new Path();
    Board board;

    @Override
    public void init(Board currentBoard) {
      board = currentBoard;
      value = 0;
    }
    
    public void exec() {
      board.tick(Robot.Move.Shave);
    }
  }

  public class Greediest extends Strategy {
    static final int NUM_LAMBDAS_PER_ITERATION = 3;

    Board board;
    double value;

    public void init(Board init) {
      board = init;
      value = 1.0;
    }

    public void exec() {

    }
  }

  List<Strategy> strategies;
  final LinkedList<History> history = new LinkedList<History>();

  public HungrySkynet(String mapStr) {
    super(mapStr);
    strategies = new ArrayList<Strategy>();
    strategies.add(new Shave());
  }

  @Override
  public String plan() {
    Strategy bestStrategy = null;
    BoardState curState = null;
    History curHistory = history.peekLast();
    while(true)
    {
      double bestValue = Double.NEGATIVE_INFINITY;
      int i;
      for (i = 0; i < strategies.size(); i++) {
        if (curHistory.triedChildren.indexOf(i) != -1) {
          continue;
        }
        Strategy s = strategies.get(i);
        s.init(curBoard);
        if (s.getValue() > bestValue) {
          bestStrategy = s;
          bestValue = s.getValue();
        }
      }

      if (i == strategies.size()) {
        backtrack();
      }

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
