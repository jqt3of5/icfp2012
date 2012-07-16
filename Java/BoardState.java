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
  public Robot.Move move = null;
  public int pathLength = 0;
  public int visits[][];


  public BoardState(final Board board) {
    this.board = board;
    visits = new int[board.height][board.width];
  }

  @Override
  public String toString() {
    return board.toString();
  }

  public void copyVisits(final BoardState parent) {
    final int h = parent.visits.length;
    final int w = parent.visits[0].length;
    visits = new int[h][w];
    for (int r = 0; r < h; r++) {
      for (int c = 0; c < w; c++) {
        visits[r][c] = parent.visits[r][c];
      }
    }
  }
}
