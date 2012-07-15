import java.util.*;
import java.io.*;

public class CostFunctions {
  public static class TrivialCost implements AStar.CostFunction {
    public double compute(Board board, Point start, Point end) {
      return 1.0;
    }
  }

  public static class ManhattanCost implements AStar.CostFunction {
    public double compute(Board board, Point start, Point end) {
      return Math.abs(end.x-start.x) + Math.abs(end.y-start.y);
    }
  }
}
