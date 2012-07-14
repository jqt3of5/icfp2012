/* -*- c-basic-offset: 2; -*- */

import java.util.*;
import java.io.*;

public class AStar {

  public CostFunction g, h;

  public AStar(CostFunction g, CostFunction h) {
    this.g = g;
    this.h = h;
  }


  PriorityQueue<BoardState> candidates = new PriorityQueue<BoardState>();
  Board board;
  Point destination;

  public List<Robot.Move> findPath(Board board, Point destination) {
    this.board = board;
    this.destination = destination;
    candidates.add(board.getBoardState());
    while (true) {
      // Termination condition
      // TODO: fill in with something better

      // A* main
      BoardState nextMove = candidates.poll();
      board.update(nextMove);
      candidates.addAll(board.getAvailableMoves());
    }
  }

  /**
   * Interface for defining a cost function, e.g., g and h.
   */
  public interface CostFunction {
    public double compute(Board board, Point start, Point end);
  }

  public static class TrivialCost implements CostFunction {
    public double compute(Board board, Point start, Point end) {
      return 1.0;
    }
  }
}
