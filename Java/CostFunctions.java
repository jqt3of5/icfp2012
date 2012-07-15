
public class CostFunctions {
  public static class TrivialCost implements AStar.CostFunction {
    @Override
    public double compute(final BoardState boardState, final Point start, final Point end) {
      return 1.0;
    }
  }

  public static class ManhattanCost implements AStar.CostFunction {
    @Override
    public double compute(final BoardState boardState, final Point start, final Point end) {
      return Math.abs(end.c-start.c) + Math.abs(end.r-start.r);
    }
  }

  public static class BoardSensingCost implements AStar.CostFunction {
    @Override
    public double compute(final BoardState boardState, final Point start, final Point end) {
      Board board = boardState.board;
      Point robotPos = board.getRobotPosition();

      if (board.state == Board.GameState.Lose)
        return Double.POSITIVE_INFINITY;

      if (board.state == Board.GameState.Win)
        return Double.NEGATIVE_INFINITY;

      double cost = -1 * board.robby.score;

      // Pushing rock
      if (boardState.parentState != null) {
        Board prevBoard = boardState.parentState.board;

        if (prevBoard.get(robotPos) == Board.CellTypes.Rock) {
          cost += 5;
        }
      }

      // Not moving (stuck?)
      if (boardState.parentState != null) {
        Board prevBoard = boardState.parentState.board;
        if (robotPos.equals(prevBoard.getRobotPosition())) {
          cost += 10;
        }
      }

      // Don't backtrack
      cost += 5*boardState.visits[robotPos.r][robotPos.c];

      return cost;
    }
  }
}
