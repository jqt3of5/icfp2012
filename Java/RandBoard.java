import java.util.Random;
import java.util.*;

public class RandBoard{

  static char[] chars = {'*', '.', '#', '\\', ' ', 'W', '!', '@'};
  static int[] weights = {20, 60, 20, 20, 20, 3, 3, 0};
  static double[] mulfactor = {0.2, 0.2, 3.0, 0, 0, 0, 0, 0};

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
	int i = -1;
	int baseVal;
	do {
	  i++;
	  baseVal = cumsum[i];
	  int[] dr = {r-1, r-1, r};
	  int[] dc = {c, c-1, c-1};

	  for (int j=0; j<dr.length; j++) {
	    if ((dr[j] > 1 || dr[j] > 1) &&
		layout[dr[j]][dc[j]] == chars[i])
	      baseVal += weights[i] * mulfactor[i];
	  }
	} while (i < cumsum.length && randInt >= baseVal);

	layout[r][c] = chars[i];
      }
    }

    Set<Point> usedPoints = new HashSet<Point>();
    for (int i=1; i<=1+random.nextInt(9); i++) {
      int r = random.nextInt(height);
      int c = random.nextInt(width);
      if (! usedPoints.contains(new Point(r,c))) {
	// TODO: add trampoline here
      }
    }

    // random robot and lift
    final int row1 = 1+random.nextInt(height-2),
      col1 = 1+random.nextInt(width-2);
    int row2 = 0, col2 = 0;

    while ((row2 == 0 && col2 == 0) ||
	   (row2 == height-1 && col2 == width-1) ||
	   (row2 == height-1 && col2 == 0) ||
	   (row2 == 0 && col2 == width-1)) {
      row2 = random.nextInt(height);
      col2 = random.nextInt(width);
    }

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
