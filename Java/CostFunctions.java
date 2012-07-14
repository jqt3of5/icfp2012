import java.util.*;
import java.io.*;

public class CostFunctions {

  public static class TrivialCost implements AStar.CostFunction {
    public double compute(Board board, Point start, Point end) {
      return 1.0;
    }
  }
}