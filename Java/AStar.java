/* -*- c-basic-offset: 2; -*- */

import java.util.*;
import java.io.*;

public class AStar {

  public CostFuction g, h;

  public AStar(CostFunction g, CostFunction h) {
    this.g = g;
    this.h = h;
  }


  List<BoardState> candidates = new ArrayList<BoardState>();
  Board board;
  Point destination;

  public void findPath(Board board, Point destination) {
    this.board = board;
    this.destination = destination;
    candidates.add(b.getBoardState());
    while (true) {
      // Termination condition
      // TODO: fill in with something better

      // A* main
      BoardState nextMove = computeBestNextMove();
      board.update(nextMove);
      candidates.addAll(board.getAvailableMoves());
    }
  }

  /**
   * Interface for defining a cost function, e.g., g and h.
   */
  public interface CostFunction {
    public int compute(Board board, Point start, Point end);
  }
}