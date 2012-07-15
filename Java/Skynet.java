import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public abstract class Skynet {
  protected Board curBoard;

  public Skynet(final String mapStr) {
    curBoard = new Board(mapStr);
  }

  public abstract String plan();

  public int score() {
    return curBoard.robby.getScore();
  }

  public Queue<Point> findClosestLambdas() {
    final Queue<Point> retQueue = new LinkedList<Point>();

    final Queue<BfsNode> bfsQ = new LinkedList<BfsNode>();
    final Set<Point> visited = new HashSet<Point>();
    bfsQ.add(new BfsNode(curBoard.robby.position, 0));

    while (bfsQ.size() > 0) {
      final BfsNode curNode = bfsQ.remove();
      final Point curPoint = curNode.point;
      if (visited.contains(curPoint)) continue;
      visited.add(curPoint);

      // add lambda points to queue
      if (curBoard.map[curPoint.r][curPoint.c] == Board.CellTypes.Lambda) {
        retQueue.add(curPoint);
      }

      // add children
      final int[] dr = {-1, 0, 0, 1};
      final int[] dc = {0, -1, 1, 0};

      for (int i = 0; i < dr.length; i++) {
        final int r = curPoint.r + dr[i];
        final int c = curPoint.c + dc[i];

        if (0 <= r && r < curBoard.height && 0 <= c && c < curBoard.width &&
            curBoard.map[r][c] != Board.CellTypes.Wall) {
          final Point childPoint = new Point(r, c);
          if (! visited.contains(childPoint))
            bfsQ.add(new BfsNode(childPoint, curNode.dist+1));
        }
      }
    }

    return retQueue;
  }

  public Board getBoard() {
    return curBoard;
  }


  // ------------- AStarSkynet --------------

  public static abstract class AStarSkynet extends Skynet {
    AStar pathfinder;
    TerminationConditions.TerminationCondition terminator;
    public AStarSkynet(final String mapStr) {
      super(mapStr);
    }
  }


  public static class GreedySkynet extends AStarSkynet {
    public GreedySkynet(final String mapStr) {
      super(mapStr);
    }

    @Override
    public String plan() {
      final Path totalPath = new Path();

      while (curBoard.lambdaPos.size() > 0) {
        if (Main.gotSIGINT)
          return totalPath.toString();

        System.out.println("Pursuing lambda " + curBoard.lambdaPos.size());

        int bestLength = Integer.MAX_VALUE;
        Board bestBoard = curBoard;
        Path bestPath = null;
        for (final Point lambdaPt : curBoard.lambdaPos) {
          final Board newBoard = new Board(curBoard);
          terminator = new TerminationConditions.PointTermination(lambdaPt);
          pathfinder = new AStar(new CostFunctions.BoardSensingCost(),
                                 new CostFunctions.ManhattanCost(),
                                 terminator);
          pathfinder.findPath(newBoard, lambdaPt);
          if (Main.gotSIGINT)
            return totalPath.toString();
          final Path path = terminator.getPath();
          if (path.size() < bestLength) {
            bestBoard = terminator.getBoard();
            bestPath = terminator.getPath();
            bestLength = path.size();
          }
        }
        curBoard = bestBoard;
        totalPath.addAll(bestPath);
      }

      final Board newBoard = new Board(curBoard);
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

  public static class GreedierSkynet extends Skynet {
    public GreedierSkynet(String mapString) {
      super(mapString);
    }

    @Override
    public String plan() {
      return null;
    }
  }

  /*
   * DOES NOT WORK YET!
   */
  class AnnealingSkynet extends AStarSkynet {
    Random rand = new Random();
    static final int MAX_TIME = 1000;

    public AnnealingSkynet(final String mapStr) {
      super(mapStr);
    }

    @Override
    public String plan() {
      double curEnergy = energy(curBoard);
      for (int time = 0; time < MAX_TIME; time++) {
        final double temperature = temperature((double)time/MAX_TIME);
        final Board newBoard = pickNeighbor();
        final double newEnergy = energy(newBoard);
        if (evaluate(curEnergy, newEnergy, temperature) > rand.nextDouble()) {
          curBoard = newBoard;
          curEnergy = newEnergy;
        }
      }

      return curBoard.toString();
    }

    double energy(final Board b) {
      return 1.0;
    }

    double temperature(final double timeRatio) {
      return 1-timeRatio;
    }

    Board pickNeighbor() {
      final int i = rand.nextInt(curBoard.lambdaPos.size());

      pathfinder.findPath(curBoard, curBoard.lambdaPos.get(i));
      final Board newBoard = new Board(curBoard);
      //newBoard.move(path);
      return newBoard;
    }

    double evaluate(final double curEnergy, final double newEnergy, final double temperature) {
      return 1.0;
    }
  }


  // -------------- Util things ----------------
  private class BfsNode {
    public Point point;
    public double dist;
    public BfsNode(final Point point, final double dist) {
      this.point = point;
      this.dist = dist;
    }
  }
}
