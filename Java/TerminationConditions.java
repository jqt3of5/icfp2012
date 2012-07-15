import java.util.PriorityQueue;

public class TerminationConditions {

  public static abstract class TerminationCondition {
    private PriorityQueue<BoardState> finalCandidates;
    private Board finalBoard;
    private BoardState finalLastMove;

    public boolean isTrue(final PriorityQueue<BoardState> candidates, final Board board,
                          final BoardState lastMove) {
      if (candidates.size() == 0 || concreteIsTrue(candidates, board, lastMove)) {
        finalCandidates = candidates;
        finalBoard = board;
        finalLastMove = lastMove;
        return true;
      }

      return false;
    }

    protected abstract boolean concreteIsTrue(PriorityQueue<BoardState> candidates,
                                              Board board, BoardState lastMove);

    public Path getPath() {
      final Path path = new Path();
      for (BoardState cur = finalLastMove; cur != null; cur = cur.parentState) {
        path.addPosition(cur.position);
      }
      path.reverse();
      return path;
    }

    public Board getBoard() {
      return finalBoard;
    }
  }

  public static class PointTermination extends TerminationCondition {
    Point destination;

    public PointTermination(final Point initDestination) {
      destination = initDestination;
    }

    @Override
    protected boolean concreteIsTrue(final PriorityQueue<BoardState> candidates, final Board board, final BoardState lastMove) {
      return board.robot.getPosition().equals(destination);
    }
  }
}
