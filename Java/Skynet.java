import java.util.*;

public abstract class Skynet {
  protected Board curBoard;

  public Skynet(String mapStr) {
    curBoard = new Board(mapStr);
  }

  public abstract String plan();

  public int score() {
    return curBoard.robby.getScore();
  }

  public PriorityQueue<Point> findClosestLambdas(Point current) {
    PriorityQueue<Point> retQueue = new PriorityQueue<Point>();



    return retQueue;
  }

  public Board getBoard() {
    return curBoard;
  }


  // ------------- AStarSkynet --------------

  public static abstract class AStarSkynet extends Skynet {
    AStar pathfinder;
    TerminationConditions.TerminationCondition terminator;
    public AStarSkynet(String mapStr) {
      super(mapStr);
    }
  }


  public static class GreedySkynet extends AStarSkynet {
    public GreedySkynet(String mapStr) {
      super(mapStr);
    }

    public String plan() {
      Path totalPath = new Path();

      while (curBoard.lambdaPos.size() > 0) {
        if (Main.gotSIGINT)
          return totalPath.toString();

        System.out.println("Pursuing lambda " + curBoard.lambdaPos.size());

        int bestLength = Integer.MAX_VALUE;
        Board bestBoard = curBoard;
        Path bestPath = null;
        for (Point lambdaPt : curBoard.lambdaPos) {
          Board newBoard = new Board(curBoard);
          terminator = new TerminationConditions.PointTermination(lambdaPt);
          pathfinder = new AStar(new CostFunctions.BoardSensingCost(),
                                 new CostFunctions.ManhattanCost(),
                                 terminator);
          pathfinder.setTimeout(10000);
          boolean finished = pathfinder.findPath(newBoard, lambdaPt);

          if (Main.gotSIGINT)
            return totalPath.toString();
          if (finished) {
            Path path = terminator.getPath();
            if (path.size() < bestLength) {
              bestBoard = terminator.getBoard();
              bestPath = terminator.getPath();
              bestLength = path.size();
            }
          }
        }
        curBoard = bestBoard;

        if (bestPath == null)
          return "";

        totalPath.addAll(bestPath);
      }

      Board newBoard = new Board(curBoard);
      terminator = new TerminationConditions.PointTermination(curBoard.liftLocation);
      pathfinder = new AStar(new CostFunctions.BoardSensingCost(), new CostFunctions.ManhattanCost(), terminator);
      pathfinder.findPath(newBoard, curBoard.liftLocation);
      if (Main.gotSIGINT)
        return totalPath.toString();

      totalPath.addAll(terminator.getPath());
      curBoard = terminator.getBoard();
      return totalPath.toString();
    }
  }

  public static class GreedierSkynet implements Skynet {
    @Override
    public String plan() {

    }
  }

/*
 * DOES NOT WORK YET!
 */
  class AnnealingSkynet extends AStarSkynet {
    Random rand = new Random();
    static final int MAX_TIME = 1000;

    public AnnealingSkynet(String mapStr) {
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

      pathfinder.findPath(curBoard, curBoard.lambdaPos.get(i));
      Board newBoard = new Board(curBoard);
      //newBoard.move(path);
      return newBoard;
    }

    double evaluate(double curEnergy, double newEnergy, double temperature) {
      return 1.0;
    }
  }

}
