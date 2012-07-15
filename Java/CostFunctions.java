
public class CostFunctions {
  public static class TrivialCost implements AStar.CostFunction {
    @Override
    public double compute(final Board board, final Point start, final Point end) {
      return 1.0;
    }
  }

  public static class ManhattanCost implements AStar.CostFunction {
    @Override
    public double compute(final Board board, final Point start, final Point end) {
      return Math.abs(end.c-start.c) + Math.abs(end.r-start.r);
    }
  }

  public static class BoardSensingCost implements AStar.CostFunction {
    @Override
    public double compute(final Board board, final Point start, final Point end) {
      if (board.state == Board.GameState.Lose) {
        return Double.POSITIVE_INFINITY;
      } else {
        return -1 * board.robby.score;
      }
    }
  }
}
