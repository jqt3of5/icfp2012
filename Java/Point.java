public class Point{
    private int x;
    private int y;

    public Point(int a, int b) {
        x = a;
        y = b;
    }

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    private void increX() {
        x = x + 1;
    }

    private void decreX() {
        x = x - 1;
    }

    private void increY() {
        y = y + 1;
    }

    private void decreY() {
        y = y - 1;
    }
}
