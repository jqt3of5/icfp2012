/* -*- c-basic-offset: 2; -*- */


/**
 * Data object to hold the state of the board for A* search.
 */
public class BoardState {

  // ---- Set by Board ----
  public Point position;
  public Robot.Move move;
  public int deltaId;	// to somehow store history of the board for fast
                        // backtracking

  // ---- Set by AStar ----
  public BoardState parentState;
  public double score;

  public String toString() {
    return "BoardState: position=" + position.toString();
  }
}
