import java.util.*;
import java.io.*;

public class AStar {

  public CostFuction g, h;

  public AStar(CostFunction g, CostFunction h) {
    this.g = g;
    this.h = h;
  }


  public void findPath(Board board, Point destination) {

  }

  public interface CostFunction {
    public int compute(Board board, Point start, Point end);
  }
}