import java.util.*;

/*
 * Util class to query things about the board
 */

class BoardQuery {
  static boolean isLiftBlocked(Board b) {
    Point liftPos = b.liftLocation;
    int r = liftPos.r;
    int c = liftPos.c;

    if (r == 0) {
      // bottom of map

      if (b.map[1][c] == Board.CellTypes.Wall) {
        return true;
      }

      if (b.map[1][c] == Board.CellTypes.Rock) {
        if ((c-1 < 0 || b.map[1][c-1] == Board.CellTypes.Rock || b.map[1][c-1] == Board.CellTypes.Wall)
            || (c+1 >= b.width || b.map[1][c+1] == Board.CellTypes.Rock || b.map[1][c+1] == Board.CellTypes.Wall)) {
          return true;
        }
      }
    } else if (r == b.height-1) {
      // top

      if (b.map[r-1][c] == Board.CellTypes.Wall) {
        return true;
      }

    } else if (c == 0) {
      // left

      if (b.map[r][1] == Board.CellTypes.Wall) {
        return true;
      }

      boolean allBlocked = true;
      for (int i=r; i >= 0; i--) {
        if (b.map[i][1] != Board.CellTypes.Wall
            && b.map[i][1] != Board.CellTypes.Rock
            && b.map[i][1] != Board.CellTypes.HigherOrder
            && b.map[i][1] != Board.CellTypes.Beard
          ) {
          allBlocked = false;
          break;
        }
      }
      if (allBlocked)
        return true;

    } else if (c == b.width-1) {
      // right

      if (b.map[r][c-1] == Board.CellTypes.Wall) {
        return true;
      }

      boolean allBlocked = true;
      for (int i=r; i >= 0; i--) {
        if (b.map[i][c-1] != Board.CellTypes.Wall
            && b.map[i][c-1] != Board.CellTypes.Rock
            && b.map[i][c-1] != Board.CellTypes.HigherOrder
            && b.map[i][c-1] != Board.CellTypes.Beard
          ) {
          allBlocked = false;
          break;
        }
      }
      if (allBlocked)
        return true;
    }

    return false;
  }
}
