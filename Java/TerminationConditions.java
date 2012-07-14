import java.util.*;

public class TerminationConditions {

  public abstract class TerminationCondition {
    private PriorityQueue<BoardState> finalCandidates;
    private Board finalBoard;
    private BoardState finalLastMove;

    public boolean isTrue(PriorityQueue<BoardState> candidates, Board board,
                          BoardState lastMove) {
      if (concreteIsTrue(candidates, board, lastMove)) {
        finalCandidates = candidates;
        finalBoard = board;
        finalLastMove = lastMove;
        return true;
      }

      return false;
    }

    protected abstract boolean concreteIsTrue(PriorityQueue<BoardState> candidates,
                                              Board board, BoardState lastMove);

    public List<Robot.Move> getPath() {
      List<Robot.Move> moves = new LinkedList<Robot.Move>();
      for (BoardState cur = finalLastMove; cur != null; cur = cur.parentState) {
        moves.add(cur.move);
      }
      Collections.reverse(moves);
      return moves;
    }

    public Board getBoard() {
      return finalBoard;
    }
  }

  public class PointTermination extends TerminationCondition {
    Point destination;

    public PointTermination(Point initDestination) {
      destination = initDestination;
    }

    protected boolean concreteIsTrue(PriorityQueue<BoardState> candidates, Board board, BoardState lastMove) {
      return board.robot.getPosition().equals(destination);
    }
  }
}
