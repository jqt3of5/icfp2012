
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

      Board prevBoard = null;
      if (boardState.parentState != null) {
        prevBoard = boardState.parentState.board;
      }

      if (board.state == Board.GameState.Lose)
        return Double.POSITIVE_INFINITY;

      if (board.state == Board.GameState.Win)
        return Double.NEGATIVE_INFINITY;

      double cost = 1;

      // Don't backtrack
      cost += boardState.visits[robotPos.r][robotPos.c];

      // Don't stay in water longer than necessary
      cost += 0.5*board.robby.waterTime;

      // Not moving (stuck?)
      if (boardState.visits[robotPos.r][robotPos.c] > 10) {
        cost += 100;
      }

      // Get a razor
      if (board.beards.size() > 0) {
        cost -= (5.0*board.beards.size())/(board.robby.razorCount+1);
      }

      if (prevBoard != null) {
        // score difference
        cost -= 50*(board.robby.lambdaCount - prevBoard.robby.lambdaCount);

        // Pushing rock
        if (prevBoard.get(robotPos) == Board.CellTypes.Rock) {
          cost += 5;
        }

        // Don't block the lift
        if (!BoardQuery.isLiftBlocked(prevBoard) && BoardQuery.isLiftBlocked(board)) {
          cost += 50;
        }

        // Shaving all of the beard
        if (prevBoard.beards.size() > 0 && board.beards.size() == 0)
          cost -= 100;

        if (prevBoard.beards.size() > board.beards.size())
          cost -= 10;

        // Jumped through a trampoline
        if (Math.abs(prevBoard.robby.position.r-robotPos.r) > 1
            || Math.abs(prevBoard.robby.position.c-robotPos.c) > 1)
          cost += 10;
      }

      return cost;
    }
  }
}
