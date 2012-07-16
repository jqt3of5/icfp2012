import java.util.Comparator;
import java.util.PriorityQueue;

public class HungrySkynet extends Skynet {

  public interface Strategy {
    public int value();

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
