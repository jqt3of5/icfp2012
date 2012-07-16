import java.util.Comparator;
import java.util.PriorityQueue;

public class HungrySkynet extends Skynet {

  public interface Strategy {
    public int value();
    public Path getPath();
    public int evaluate(BoardState currentBoard);
  }

  public class Shave implements Strategy
  {
    int value = -1;
    Path stratPath = new Path();
    @Override
    public int value() {
      return value;
    }

    @Override
    public Path getPath() {
      return stratPath;
    }

    @Override
    public int evaluate(BoardState currentBoard) {
 
      return 0;
    }
    
  }
  PriorityQueue<Strategy> strategies;

  public HungrySkynet(String mapStr) {
    super(mapStr);
    strategies = new PriorityQueue<Strategy>(11, new Comparator<Strategy>() {

      @Override
      public int compare(Strategy arg0, Strategy arg1) {
        return (arg0.value() - arg1.value() == 0 ? 0 : arg0.value()
            - arg1.value());
      }
    });
  }

  @Override
  public String plan() {

    return null;
  }

}
