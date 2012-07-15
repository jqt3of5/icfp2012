import java.util.*;

public class BoardRep {

	private CellTree[][] cellTrees;
  private ArrayList<Robot> robotStates;
	public int globalDeltaId;

	public BoardRep(final Board.CellTypes[][] layout, Point robotPosition) {
	  globalDeltaId = 0;

	  // initialize board
	  final int nrows = layout.length;
	  final int ncols = layout[0].length;
	  cellTrees = new CellTree[nrows][ncols];

	  for (int r = 0; r < nrows; r++) {
	    for (int c = 0; c < ncols; c++) {
	      cellTrees[r][c] = new CellTree(layout[r][c], globalDeltaId);
	    }
	  }

    robotStates = new ArrayList<Robot>();
    robotStates.add(new Robot(robotPosition.y, robotPosition.x));
	}

  public BoardRep(BoardRep oldRep) {
    globalDeltaId = oldRep.globalDeltaId;

	  final int nrows = oldRep.cellTrees.length;
	  final int ncols = oldRep.cellTrees[0].length;
    
	  cellTrees = new CellTree[nrows][ncols];
	  for (int r = 0; r < nrows; r++) {
	    for (int c = 0; c < ncols; c++) {
	      cellTrees[r][c] = new CellTree(oldRep.cellTrees[r][c]);
	    }
	  }

    robotStates = new ArrayList<Robot>();
    for (Robot r : oldRep.robotStates) {
      robotStates.add(new Robot(r));
    }
  }

	public Board.CellTypes get(final int r, final int c) {
	  return cellTrees[r][c].get(globalDeltaId);
	}

	public int set(final int r, final int c, final Board.CellTypes newType) {
	  cellTrees[r][c].addNewCell(globalDeltaId, newType, ++globalDeltaId);
	  return globalDeltaId;
	}

	public int set(final int[] r, final int[] c, final Board.CellTypes[] newTypes, Point robotPosition) {
	  assert(r.length == c.length && c.length == newTypes.length);
	  globalDeltaId++;
	  for (int i = 0; i < r.length; i++) {
	    cellTrees[r[i]][c[i]].addNewCell(globalDeltaId-1, newTypes[i], globalDeltaId);
	  }
	  return globalDeltaId;
	}

	public void revert(final int deltaId) {
	  globalDeltaId = deltaId;
	}
    

	// --------------- Cells ---------------
	class CellTree {
	  private Cell root;     // Root of the cell tree.
	  private Cell current;  // Cache of the current cell in the cell tree; if current.deltaId !=
	                         // query deltaId, current is stale.

	  public CellTree(final Board.CellTypes rootCellType, final int rootDeltaId) {
	    root = new Cell(rootCellType, rootDeltaId);
	    current = root;
	  }

    public CellTree(CellTree oldTree) {
      root = new Cell(oldTree.root);
      current = root;
    }

	  public Board.CellTypes get(final int queryDeltaId) {
	    if (current.deltaId != queryDeltaId) {
	      current = findCell(queryDeltaId);
	    }
	    return current.cellType;
	  }

	  public void addNewCell(final int oldDeltaId,
	      final Board.CellTypes cellType, final int newDeltaId) {
	    if (current.deltaId != oldDeltaId) {
	      current = findCell(oldDeltaId);
	    }
	    final Cell newCell = new Cell(cellType, newDeltaId);
	    current.children.add(newCell);
	    current = newCell;
	  }

    // Potentially stupid algorithm? May optimize later
	  public Cell findCell(final int queryDeltaId) {
      Queue<Cell> curCells = new LinkedList<Cell>();
      curCells.add(root);
      Cell max = root;
      Cell cur;
      while (curCells.size() > 0) {
        cur = curCells.remove();
        if (cur.deltaId < queryDeltaId) {
          curCells.addAll(cur.children);
          if (cur.deltaId > max.deltaId) {
            max = cur;
          }
        }
      }

      return max;
	  }
	}

	private class Cell {
	  public Board.CellTypes cellType;
	  public int deltaId;
	  public List<Cell> children;
	  // public Cell parent;

	  public Cell(final Board.CellTypes inCellType, final int inDeltaId) {
	    cellType = inCellType;
	    deltaId = inDeltaId;
	    children = new ArrayList<Cell>(11);
	    // parent = null;
	  }

    public Cell(Cell oldCell) { // can pass parent here later
      cellType = oldCell.cellType;
      deltaId = oldCell.deltaId;
      children = new ArrayList<Cell>();
      for (Cell c : oldCell.children) {
        children.add(new Cell(c));
      }
    }
	}
}
