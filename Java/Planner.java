abstract class Planner {
  protected Board curBoard;

  public Planner(String mapStr) {
    curBoard = new Board(mapStr);
  }

  public abstract String plan();
}
