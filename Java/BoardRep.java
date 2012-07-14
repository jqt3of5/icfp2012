

public class BoardRep {

	Cell[][] cells;

	public BoardRep(final int nrows, final int ncols) {
	  cells = new Cell[nrows][ncols];
	}


	public Board.CellTypes get(final int r, final int c) {
	  // TODO(jack): complete
	  return Board.CellTypes.Empty;
	}

	public void set(final int r, final int c, final Board.CellTypes newType) {
	  // TODO(jack): complete
	}

	// ----- Cell -----
	class Cell {

	}
}
