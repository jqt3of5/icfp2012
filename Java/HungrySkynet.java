import java.util.*;

public class HungrySkynet extends Skynet {

  abstract class Strategy implements Comparable<Strategy> {
    Board board;

    public abstract double getValue();
    public abstract Path getPath();
    public abstract double evaluate(Board currentBoard);

    public Board getBoard() { return board; }

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
    public double getValue() {
      return value;
    }

    @Override
    public Path getPath() {
      return stratPath;
    }

    @Override
    public double evaluate(Board currentBoard) {
      board = currentBoard;
      
      board.tick(Robot.Move.Shave);
      return 0;
    }
  }
  List<Strategy> strategies;

  public HungrySkynet(String mapStr) {
    super(mapStr);
    strategies = new ArrayList<Strategy>();
  }

  @Override
  public String plan() {
    final LinkedList<Path> totalPath = new LinkedList<Path>();
    
    Strategy bestStrategy = null;
    BoardState curState = null;
    while(true)
    {
      curBoard = bestStrategy.getBoard();
      totalPath.add(bestStrategy.getPath());

      double bestValue = Double.NEGATIVE_INFINITY;
      for (Strategy s : strategies) {
        double value = s.evaluate(curBoard);
        if (value > bestValue) {
          bestStrategy = s;
          bestValue = value;
        }
      }
      
      if (true) break; // termination condition?
    }
    return null;
  }
}
