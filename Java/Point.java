public class Point implements Comparable<Point> {
  public int c;
  public int r;

  public Point(final int inR, final int inC) {
    c = inC;
    r = inR;
  }
    
  public Point(Point p) {
    c = p.c;
    r = p.r;
  }

  @Override
  public String toString() {
    return "(" + r + "," + c + ")";
  }

  public int getC() {
    return c;
  }

  public int getR() {
    return r;
  }
  public void setC(final int a) {
    c = a;
  }
  public void setR(final int b) {
    r = b;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + c;
    result = prime * result + r;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final Point other = (Point) obj;
    if (c != other.c)
      return false;
    if (r != other.r)
      return false;
    return true;
  }

  @Override
  public int compareTo(final Point otherPoint) {
    if (r == otherPoint.r)
      return c - otherPoint.c;
    else
      return r - otherPoint.r;
  }
}
