import java.util.Comparator;
import java.util.PriorityQueue;

public class HungrySkynet extends Skynet {

  public interface Strategy {
    public double value();
    public Path getPath();
    public double evaluate(BoardState currentBoard);
  }

  public class Shave implements Strategy
  {
    double value = -1;
    Path stratPath = new Path();
    BoardState state;
    @Override
    public double value() {
      return value;
    }

    @Override
    public Path getPath() {
      return stratPath;
    }

    @Override
    public double evaluate(BoardState currentBoard) {
      state = currentBoard;
      
      state.board.tick(Robot.Move.Shave);
      return 0;
    }
    
  }
  PriorityQueue<Strategy> strategies;

  public HungrySkynet(String mapStr) {
    super(mapStr);
    strategies = new PriorityQueue<Strategy>(11, new Comparator<Strategy>() {

      @Override
      public int compare(Strategy arg0, Strategy arg1) {
        return (arg0.value() - arg1.value() == 0 ? 0 : (int)(arg0.value()
                                                             - arg1.value()));
      }
    });
  }

  @Override
  public String plan() {
    
    while(true)
    {
      
      Strategy bestStrategy = strategies.poll();
      
      
      
      
      if (true) break; // termination condition?
    }
    return null;
  }

}
