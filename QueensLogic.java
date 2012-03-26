/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.util.*;

import net.sf.javabdd.*;
import net.sf.javabdd.BDD.BDDIterator;
import net.sf.javabdd.BDD.BDDToString;
import net.sf.javabdd.BDDFactory.BDDOp;
import net.sf.javabdd.MicroFactory.bdd;
import net.sf.javabdd.TryVarOrder.BDDOperation;

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
	private boolean[][] auxBoard;
	private BDDFactory bddFactory;
	private BDD rules;

	public QueensLogic() {
		// constructor
	}

	public void initializeGame(int size) {
		this.x = size;
		this.y = size;
		this.board = new int[x][y];
		this.auxBoard = new boolean[x][y];
		// Initialize the BDD
		this.bddFactory = JFactory.init(2000000, 200000);
		this.bddFactory.setVarNum(x * y);
		this.rules = this.bddFactory.one();
		this.constructRules();

		/* Print the results */
		System.out.println("There are " + (long) this.rules.satCount()
				+ " solutions.");
		BDD solution = this.rules.satOne();
		System.out.println("Here is " + (long) solution.satCount()
				+ " solution:");
		for (int i = 0; i < this.rules.satCount(); i++) {
			BDD solution2 = this.rules.satOne();
			System.out.println("The solution number: " + solution2.satCount());
			solution2.printSet();
		}
		solution.printSet();
		System.out.println();
		// this.checkFuture();
	}

	public int[][] getGameBoard() {
		return board;
	}

	public boolean insertQueen(int column, int row) {

		if (board[column][row] == -1 || board[column][row] == 1) {
			return true;
		}

		board[column][row] = 1;
		// for (int i = 0; i < x; i++) {
		// for (int j = 0; j < y; j++) {
		// System.out.print("" + this.board[j][i] + "|");
		// }
		// System.out.println();
		// }
		this.checkRow(column, row);
		this.checkColumn(column, row);
		this.checkDiagonal(column, row);
		this.checkRules();
		// this.checkFuture();
		// for (int i = 0; i < this.x; i++) {
		// for (int j = 0; j < this.y; j++) {
		// if (this.auxBoard[i][j] == false && this.board[i][j] != 1) {
		// this.board[i][j] = -1;
		// }
		// }
		// }
		return true;
	}

	private boolean checkFuture() {
		if (this.checkRules()) {
			return true;
		}
		boolean result = false;
		for (int k = 0; k < x; k++) {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < x; j++) {
					if (this.board[i][j] == 0) {
						this.board[i][j] = 1;
						if (this.checkFuture()) {
							result = true;
							this.auxBoard[i][j] |= true;
						}
						this.board[i][j] = 0;
					}
				}
			}
		}
		return result;
	}

	private boolean checkColumn(final int column, final int row) {
		// Constructs the rule
		for (int j = 0; j < x; j++) {
			if (this.board[column][j] == 0) {
				this.board[column][j] = -1;
			}
		}
		return false;
	}

	private boolean checkRow(final int column, final int row) {
		// Constructs the rule
		for (int j = 0; j < x; j++) {
			if (this.board[j][row] == 0) {
				this.board[j][row] = -1;
			}
		}
		return false;
	}

	private void constructRules() {
		BDD rule = null;
		BDD rule2 = null;
		// Constructs rules for the rows
		for (int i = 0; i < x; i++) {
			rule2 = this.bddFactory.zero();
			for (int k = 0; k < x; k++) {
				rule = this.bddFactory.ithVar(x * k + i);
				for (int j = 0; j < x; j++) {
					if (k == j) {
						continue;
					}
					rule = rule.and(this.bddFactory.nithVar(x * j + i));
				}
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
		// Constructs rules for the columns
		for (int i = 0; i < x; i++) {
			rule2 = this.bddFactory.zero();
			for (int k = 0; k < x; k++) {
				rule = this.bddFactory.ithVar(x * i + k);
				for (int j = 0; j < x; j++) {
					if (k == j) {
						continue;
					}
					rule = rule.and(this.bddFactory.nithVar(x * i + j));
				}
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
		// Constructs rules for the diagonals from top to bottom
		for (int ii = 0; ii < x; ii++) {
			rule2 = this.bddFactory.zero();
			for (int jj = 0; jj < x; jj++) {
				rule = this.bddFactory.ithVar(x * ii + jj);
				for (int i = ii, j = jj; i < x & j < x; i++, j++) {
					if (ii == i & jj == j) {
						continue;
					}
					rule = rule.and(this.bddFactory.nithVar(x * i + j));
				}
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
		// Constructs rules for the diagonal from bottom to top
		for (int ii = x - 1; ii >= 0; ii--) {
			rule2 = this.bddFactory.zero();
			for (int jj = 0; jj < x; jj++) {
				rule = this.bddFactory.ithVar(x * ii + jj);
				for (int i = ii, j = jj; i >= 0 & j < x; i--, j++) {
					if (ii == i & jj == j) {
						continue;
					}
					rule = rule.and(this.bddFactory.nithVar(x * i + j));
				}
				rule2 = rule2.or(rule);
			}
			this.rules = this.rules.and(rule2);
		}
	}

	private boolean checkRules() {
		BDD state = this.bddFactory.one();
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < x; j++) {
				state = state
						.and(this.board[i][j] == 1 ? this.bddFactory.ithVar(x
								* i + j) : this.bddFactory.nithVar(x * i + j));
			}
		}

		// Checks if it is true or not
		BDD restricted = this.rules.restrict(state);
		if (restricted.isOne()) {
			// System.out.println("It is true rules.");
			return true;
		} else if (restricted.isZero()) {
			// System.out.println("It is false rules");
			return false;
		}
		return false;
	}

	private void checkDiagonal(final int column, final int row) {
		int rowStart = row - Math.min(row, column);
		int columnStart = column - Math.min(row, column);

		for (int i = rowStart, j = columnStart; i < this.x & j < this.y; i++, j++) {
			if (this.board[j][i] == 0) {
				this.board[j][i] = -1;
			}
		}

		// From bottom up
		rowStart = row + Math.min(column, this.x - 1 - row);
		columnStart = column - Math.min(column, this.y - 1 - row);

		for (int i = rowStart, j = columnStart; i >= 0 & j < this.y; i--, j++) {
			if (this.board[j][i] == 0) {
				this.board[j][i] = -1;
			}
		}
	}
}