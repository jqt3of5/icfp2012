import java.util.ArrayList;


public class Path {
  private ArrayList<Point> positions;

  public Path() {
    positions = new ArrayList<Point> ();
  }
	
  public void addPosition(Point currentPosition) {
    Point temp = new Point(currentPosition.getX(), currentPosition.getY());
    positions.add(temp);
  }
  
  public boolean inPosition(Point position) {
    for(Point p: positions) {
      if(position.getX() == p.getX() && position.getY() == p.getY())
        return true;
    }
    return false;
  }
  
  public boolean inPosition(int x, int y) {
    for(Point p: positions) {
      if(x == p.getX() && x == p.getY())
        return true;
    }
    return false;
  }
  
}
