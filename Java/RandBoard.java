import java.util.*;

public class RandBoard extends Board {

  public RandBoard(int width, int height) {
    super(width, height);
    Random random = new Random();

    // Fill the map with walls
    for (int i = 0; i < super.layoutWidth; i++)
      for (int j = 0; j < super.layoutHeight; j++)
        layout[i][j] = Board.CellTypes.Wall;
  }

}
