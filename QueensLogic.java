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
 * Solves the problems of the n-queens.
 * 
 * 
 */
public class QueensLogic {
	/**
	 * The height of the board.
	 */
	private int x = 0;
	/**
	 * The width of the board.
	 */
	private int y = 0;
	/**
	 * The board containing the queens.
	 */
	private int[][] board;
	/**
	 * Creates new BDD and other useful stuff.
	 */
	private BDDFactory bddFactory;
	/**
	 * Contains the rules of the n-queens problem.
	 */
	private BDD rules;

	public QueensLogic() {
		// constructor
	}

	public void initializeGame(int size) {
		this.x = size;
		this.y = size;
		this.board = new int[x][y];
		// Initialize the BDD
		this.bddFactory = JFactory.init(40000000, 4000000);
		this.bddFactory.setVarNum(x * y);
		this.rules = this.bddFactory.one();
		this.constructRules();
		this.board = this.helpUser();
	}

	public int[][] getGameBoard() {
		return this.board;
	}

	public boolean insertQueen(int column, int row) {

		if (this.board[column][row] == -1 || this.board[column][row] == 1) {
			return true;
		}

		this.board[column][row] = 1;
		this.board = this.helpUser();
		return true;
	}

	/**
	 * Calculates the invalid positions where isn't possible put a queen and
	 * copy them to the board. Furthermore calculates if there is only one
	 * solution.
	 * 
	 * @return An updated board with the new state.
	 */
	private int[][] helpUser() {
		int[][] resultBoard = new int[this.x][this.y];
		BDD state = this.bddFactory.one();
		// Copy the state of the board to a BDD
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < x; j++) {
				if (this.board[i][j] == 1) {
					state = state
							.and(this.bddFactory.ithVar(this.bddPos(i, j)));
				}
			}
		}
		// Apply the state to the rules
		BDD restricted = this.rules.restrict(state);
		int[][] auxBoard = new int[this.x][this.y];
		Iterator<byte[]> it = restricted.allsat().iterator();
		int numberSolutions = 0;
		// Count the solutions and stored the valid positions to put a queen
		while (it.hasNext()) {
			numberSolutions++;
			byte[] a = it.next();
			for (int i = 0; i < a.length; i++) {
				if (a[i] == 1) {
					auxBoard[i / this.x][i % this.x] = 1;
				}
			}
		}
		System.out.println(numberSolutions);
		// Translate the results to the board
		for (int i = 0; i < this.x; i++) {
			for (int j = 0; j < this.y; j++) {
				// If there is only one solution just copy the result
				if (this.board[i][j] == 1
						| (numberSolutions == 1 & auxBoard[i][j] == 1)) {
					resultBoard[i][j] = 1;
				}
				// Put a cross in the invalid positions
				else if (this.board[i][j] == -1
						| (this.board[i][j] == 0 & auxBoard[i][j] != 1)) {
					resultBoard[i][j] = -1;
				}
			}
		}
		return resultBoard;
	}

	/**
	 * Constructs the rules than check if the game is finished or it isn't. It
	 * checks than there is only one and at least one queen in each row, column
	 * and diagonal.
	 */
	private void constructRules() {
		BDD rule = null;
		BDD rule2 = null;
		// Constructs rules for the rows
		for (int i = 0; i < x; i++) {
			rule2 = this.bddFactory.zero();
			for (int k = 0; k < x; k++) {
				// The value than must be true
				rule = this.bddFactory.ithVar(this.bddPos(k, i));
				for (int j = 0; j < x; j++) {
					if (k == j) {
						continue;
					}
					// The values than must be false
					rule = rule.and(this.bddFactory.nithVar(this.bddPos(j, i)));
				}
				// Only one combination true in each row
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
		// Constructs rules for the columns
		for (int i = 0; i < x; i++) {
			rule2 = this.bddFactory.zero();
			for (int k = 0; k < x; k++) {
				// The value than must be true
				rule = this.bddFactory.ithVar(this.bddPos(i, k));
				for (int j = 0; j < x; j++) {
					if (k == j) {
						continue;
					}
					// The values than must be false
					rule = rule.and(this.bddFactory.nithVar(this.bddPos(i, j)));
				}
				// Only one combination true in each column
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
		// Constructs rules for the diagonals from top to bottom
		for (int ii = 0; ii < x; ii++) {
			rule2 = this.bddFactory.zero();
			for (int jj = 0; jj < x; jj++) {
				// The value than must be true
				rule = this.bddFactory.ithVar(this.bddPos(ii, jj));
				for (int i = ii, j = jj; i < x & j < x; i++, j++) {
					if (ii == i & jj == j) {
						continue;
					}
					// The values than must be false
					rule = rule.and(this.bddFactory.nithVar(this.bddPos(i, j)));
				}
				// Only one combination true in each diagonal
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
		// Constructs rules for the diagonal from bottom to top
		for (int ii = x - 1; ii >= 0; ii--) {
			rule2 = this.bddFactory.zero();
			for (int jj = 0; jj < x; jj++) {
				// The value than must be true
				rule = this.bddFactory.ithVar(this.bddPos(ii, jj));
				for (int i = ii, j = jj; i >= 0 & j < x; i--, j++) {
					if (ii == i & jj == j) {
						continue;
					}
					// The values than must be false
					rule = rule.and(this.bddFactory.nithVar(this.bddPos(i, j)));
				}
				// Only one combination true in each diagonal
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
	}

	/**
	 * Translates a position in the matrix to its value in the BDD.
	 * 
	 * @param column
	 *            The column in the matrix.
	 * @param row
	 *            The row in the matrix.
	 * @return The equivalent position in the array of the BDD.
	 */
	private int bddPos(final int column, final int row) {
		return this.x * column + row;
	}

}
