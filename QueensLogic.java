/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.util.*;

import net.sf.javabdd.*;

/**
 * Before column and afte row.
 * 
 * @author Juan
 * 
 */
public class QueensLogic {
	private int x = 0;
	private int y = 0;
	private int[][] board;
	private BDDFactory bddFactory;
	private boolean[] column_values;

	public QueensLogic() {
		// constructor
	}

	public void initializeGame(int size) {
		this.x = size;
		this.y = size;
		column_values = new boolean[size];
		this.board = new int[x][y];
		// Initialize the BDD
		this.bddFactory = JFactory.init(2000000, 200000);
		this.bddFactory.setVarNum(x * y);
	}

	public int[][] getGameBoard() {
		return board;
	}

	public boolean insertQueen(int column, int row) {

		if (board[column][row] == -1 || board[column][row] == 1) {
			return true;
		}

		board[column][row] = 1;

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				System.out.print("" + this.board[j][i] + "|");
			}
			System.out.println();
		}
		this.checkRow(column, row);
		this.checkColumn(column, row);
		return true;
	}

	private boolean checkColumn(final int column, final int row) {
		// Constructs the rule
		BDD rule = this.bddFactory.ithVar(column * x + row);
		for (int j = 0; j < x; j++) {
			if (row == j) {
				continue;
			}
			rule = rule.and(this.bddFactory.nithVar(column * x + j));
		}

		// Stores the state of column with the new queen
		BDD state = this.board[column][row] == 1 ? this.bddFactory
				.ithVar(column * x + row) : this.bddFactory.nithVar(column * x
				+ row);
		for (int j = 0; j < x; j++) {
			if (j == row) {
				continue;
			}
			state = state.and(this.board[column][j] == 1 ? this.bddFactory
					.ithVar(column * x + j) : this.bddFactory.nithVar(column
					* x + j));
		}

		// Checks if it is true or not
		BDD restricted = rule.restrict(state);

		if (restricted.isOne()) {
			System.out.println("It is true");
			return true;
		} else if (restricted.isZero()) {
			System.out.println("It is false");
			return false;
		}
		return false;
	}

	private boolean checkRow(final int column, final int row) {
		// Constructs the rule
		BDD rule = this.bddFactory.ithVar(column * x + row);
		for (int j = 0; j < x; j++) {
			if (column == j) {
				continue;
			}
			rule = rule.and(this.bddFactory.nithVar(x * j + row));
		}

		// Stores the state of column with the new queen
		// System.out.println("dfsd"+this.board[column][row]);
		BDD state = this.board[column][row] == 1 ? this.bddFactory
				.ithVar(column * x + row) : this.bddFactory.nithVar(column * x
				+ row);
		for (int j = 0; j < x; j++) {
			if (j == column) {
				continue;
			}
			state = state
					.and(this.board[j][row] == 1 ? this.bddFactory.ithVar(x * j
							+ row) : this.bddFactory.nithVar(x * j + row));
		}

		// Checks if it is true or not
		BDD restricted = rule.restrict(state);

		if (restricted.isOne()) {
			System.out.println("It is true");
			return true;
		} else if (restricted.isZero()) {
			System.out.println("It is false");
			return false;
		}
		return false;
	}
}
