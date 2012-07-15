/* -*- c-basic-offset: 2; -*- */


/**
 * Data object to hold the state of the board for A* search.
 */
public class BoardState {

  // ---- Set by Board ----
  public Board board;

  // ---- Set by AStar ----
  public BoardState parentState;
  public double score;

  public String toString() {
    return board.toString();
  }
}
