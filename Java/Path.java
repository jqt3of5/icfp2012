import java.util.ArrayList;
import java.util.Collections;


public class Path {
  private ArrayList<Point> positions;

  public Path() {
    positions = new ArrayList<Point> ();
  }

  public void addPosition(final Point currentPosition) {
    final Point temp = new Point(currentPosition.getR(), currentPosition.getC());
    positions.add(temp);
  }

  public boolean inPosition(final Point position) {
    for(final Point p: positions) {
      if(position.getC() == p.getC() && position.getR() == p.getR())
        return true;
    }
    return false;
  }

  public boolean inPosition(final int x, final int y) {
    for(final Point p: positions) {
      if(x == p.getC() && x == p.getR())
        return true;
    }
    return false;
  }

  public void reverse() {
    Collections.reverse(positions);
  }

  public void addAll(final Path newPath) {
    positions.addAll(newPath.positions);
  }

  public int size() {
    return positions.size();
  }
}
