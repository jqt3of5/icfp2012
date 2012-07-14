import java.util.Random;

abstract class Planner {
  protected Board curBoard;

  public Planner(String mapStr) {
    curBoard = new Board(mapStr);
  }

  public abstract String plan();
}

abstract class AStarPlanner extends Planner {
  AStar pathfinder;
  public AStarPlanner(String mapStr) {
    super(mapStr);
    pathfinder = new AStar(new CostFunctions.TrivialCost(), new CostFunctions.TrivialCost());
  }
}

class AnnealingPlanner extends Planner {
  Random rand = new Random();
  static final int MAX_TIME = 1000;

  public AnnealingPlanner(String mapStr) {
    super(mapStr);
  }

  public String plan() {
    double curEnergy = energy(curBoard);
    for (int time = 0; time < MAX_TIME; time++) {
      double temperature = temperature((double)time/MAX_TIME);
      Board newBoard = pickNeighbor();
      double newEnergy = energy(newBoard);
      if (evaluate(curEnergy, newEnergy, temperature) > rand.nextDouble()) {
        curBoard = newBoard;
        curEnergy = newEnergy;
      }
    }

    return curBoard.toString();
  }

  double energy(Board b) {
    return 1.0;
  }

  double temperature(double timeRatio) {
    return 1-timeRatio;
  }

  Board pickNeighbor() {
    int i = rand.nextInt(curBoard.lambdaPos.size());

    pathfinder.findPath(curBoard, curBoard.lambdaPos[i]);

  }

  double evaluate(double curEnergy, double newEnergy, double temperature) {
  }
}
