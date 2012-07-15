import java.util.Random;

public class RandBoard{

  static char[] chars = {'*', '.', '#', '\\', ' ', 'W', '!', '.', '.'};
  static int[] weights = {20, 100, 10, 20, 3, 3, 5, 5};
  static double[] mulfactor = {0.1, 0.1, 0.2, 0, 0, 0, 0, 0};

  public static String getNewMap(final int width, final int height) {
    final char[][] layout = new char[height][width];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        layout[r][c] = '#';
      }
    }

    // generating
    final int[] cumsum = new int[weights.length];
    cumsum[0] = weights[0];
    for (int i=1; i<cumsum.length; i++)
      cumsum[i] = cumsum[i-1] + weights[i];
    final int totalWeight = cumsum[cumsum.length - 1];

    final Random random = new Random();
    for (int r = 1; r < height-1; r++) {
      for (int c = 1; c < width-1; c++) {
	final int randInt = random.nextInt(totalWeight);
	int i = 0;
	for (; i < cumsum.length && randInt >= cumsum[i]; i++);
	layout[r][c] = chars[i];
      }
    }

    // random robot and lift
    final int row1 = random.nextInt(height), row2 = random.nextInt(height);
    final int col1 = random.nextInt(width), col2 = random.nextInt(width);

    layout[row1][col1] = 'R';
    layout[row2][col2] = 'L';


    return arrayToString(layout);
  }

  public static String arrayToString(final char[][] layout) {
    final StringBuilder sb = new StringBuilder();
    final int height = layout.length;
    final int width = layout[0].length;

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        sb.append(layout[r][c]);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public static void main(final String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java RandBoard <width> <height>");
      System.exit(1);
    }

    int width = Integer.parseInt(args[0]);
    int height = Integer.parseInt(args[1]);

    System.out.println(RandBoard.getNewMap(width, height));
  }
}
