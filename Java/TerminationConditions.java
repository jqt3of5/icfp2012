import java.util.PriorityQueue;

public class TerminationConditions {

  public static abstract class TerminationCondition {
    private PriorityQueue<BoardState> finalCandidates;
    private BoardState finalLastState;

    public boolean isTrue(final PriorityQueue<BoardState> candidates, final BoardState boardState) {
      if (candidates.size() == 0 || concreteIsTrue(candidates, boardState)) {
        finalCandidates = candidates;
        finalLastState = boardState;
        return true;
      }

      return false;
    }

    protected abstract boolean concreteIsTrue(PriorityQueue<BoardState> candidates,
                                              BoardState boardState);

    public Path getPath() {
      final Path path = new Path();
      for (BoardState cur = finalLastState; cur != null; cur = cur.parentState) {
        path.add(cur.board.getRobotPosition(), cur.move);
      }
      path.reverse();
      return path;
    }

    public Board getBoard() {
      return finalLastState.board;
    }
  }

  public static class PointTermination extends TerminationCondition {
    Point destination;

    public PointTermination(final Point initDestination) {
      destination = initDestination;
    }

    @Override
    protected boolean concreteIsTrue(final PriorityQueue<BoardState> candidates, final BoardState boardState) {
      if (boardState.board.state == Board.GameState.Win)
        return true;
      return boardState.board.getRobotPosition().equals(destination);
    }
  }
}
