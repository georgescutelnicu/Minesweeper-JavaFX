package minesweeper;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * The Cell class represents a single cell in the Minesweeper game board.
 */
public class Cell {
    private final int row;
    private final int col;
    private boolean hasMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int neighborMineCount;
    private final Button btn;
    private final Board board;

    /**
     * Constructs a Cell object with the specified row and column indices.
     *
     * @param board The Minesweeper game board.
     * @param row   The row index of the cell.
     * @param col   The column index of the cell.
     */
    public Cell(Board board, int row, int col) {
        this.board = board;
        this.row = row;
        this.col = col;
        this.hasMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.neighborMineCount = 0;
        this.btn = new Button();
    }

    /**
     * Reveals the cell and updates its appearance.
     * If the cell contains a mine, it will display the mine; otherwise, it displays the
     * number of neighboring mines. If the cell has no neighboring mines, it reveals
     * adjacent cells as well.
     */
    public void revealCell() {
        if (!this.isRevealed && this.board.getGameIsOn() == 1) {
            this.isRevealed = true;

            if (this.hasMine) {
                this.btn.setGraphic(new ImageView(this.board.getImages().get("bomb")));

            } else {
                this.btn.setGraphic(new ImageView(this.board.getImages().get(
                        Integer.toString(this.neighborMineCount))));

                if (this.neighborMineCount == 0) {
                    this.board.revealNeighbors(row, col);
                }

                if (this.isFlagged) {
                    this.board.updateMinesLabel(1);
                }

            }
            this.board.isGameInProgress();
        }
    }

    /**
     * Toggles the flag on the cell.
     * If the cell is not revealed and not already flagged, it flags the cell.
     * If the cell is already flagged, it unflags it.
     */
    public void flag() {
        if (this.board.getGameIsOn() == 1) {
            if (!this.isRevealed && this.isFlagged) {
                this.btn.setGraphic(new ImageView(this.board.getImages().get("tile")));
                this.isFlagged = false;
                this.board.updateMinesLabel(1);
            } else if (!this.isRevealed && Integer.parseInt(this.board.getMinesLabel().trim()) > 0) {
                this.btn.setGraphic(new ImageView(this.board.getImages().get("flag")));
                this.isFlagged = true;
                this.board.updateMinesLabel(-1);
            }
        }
    }

    /**
     * Gets the button associated with the cell.
     *
     * @return The button representing the cell.
     */
    public Button getBtn() {
        return this.btn;
    }

    /**
     * Checks if the cell is revealed.
     *
     * @return True if the cell is revealed, false otherwise.
     */
    public boolean getIsRevealed() {
        return this.isRevealed;
    }

    /**
     * Checks if the cell has a mine.
     *
     * @return True if the cell has a mine, false otherwise.
     */
    public boolean getHasMine() {
        return this.hasMine;
    }

    /**
     * Sets whether the cell has a mine.
     *
     * @param hasMine True if the cell has a mine, false otherwise.
     */
    public void setHasMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    /**
     * Increments the count of neighboring mines for the cell.
     */
    public void incrementNeighborMineCount() {
        this.neighborMineCount++;
    }

    /**
     * Resets the cell to its initial state.
     * Clears any revealed state, mine flag, and neighboring mine count.
     */
    public void resetCell() {
        this.isRevealed = false;
        this.hasMine = false;
        this.neighborMineCount = 0;
        this.isFlagged = false;
        this.btn.setGraphic(new ImageView(this.board.getImages().get("tile")));
    }
}
