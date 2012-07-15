/* -*- c-basic-offset: 2; -*- */

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStar {

  public static final int INITIAL_PQ_CAPACITY = 59;
  protected CostFunction g, h;
  protected TerminationConditions.TerminationCondition termCond;

  public AStar(final CostFunction g, final CostFunction h, final TerminationConditions.TerminationCondition termCond) {
    this.g = g;
    this.h = h;
    this.termCond = termCond;
    // init PriorityQueue
    candidates = new PriorityQueue<BoardState>(
      INITIAL_PQ_CAPACITY, new Comparator<BoardState>() {
        @Override
        public int compare(final BoardState s, final BoardState t) {
          if (s.score == t.score) return 0;
          else return (s.score < t.score) ? -1 : 1;
        }
      });
  }

  private PriorityQueue<BoardState> candidates;

  public void findPath(final Board board, final Point destination) {
    candidates.clear();
    candidates.add(board.getBoardState());

    while (true) {
      // A* main
      final BoardState nextMove = candidates.poll();
      System.out.println(nextMove.position);
      board.revert(nextMove);
      for (final BoardState candMove : board.getAvailableMoves()) {
        candMove.parentState = nextMove;
        candMove.score =
          g.compute(board, board.getRobotPosition(), candMove.position) +
          h.compute(board, candMove.position, destination);
        candidates.add(candMove);
      }

      // Termination condition
      if (termCond.isTrue(candidates, board, nextMove)) break;
    }
  }

  /**
   * Interface for defining a cost function, e.g., g and h.
   */
  public interface CostFunction {
    public double compute(Board board, Point start, Point end);
  }
}
