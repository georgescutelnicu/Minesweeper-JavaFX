package minesweeper;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Cell {
    private int row;
    private int col;
    private boolean hasMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int neighborMineCount;
    private Button btn;
    private Board board;

    public Cell(Board board, int row, int col) {
        this.row = row;
        this.col = col;
        this.hasMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.neighborMineCount = 0;
        this.btn = new Button();
        this.board = board;
    }

    public void revealCell() {
        if (!this.isRevealed && this.board.getGameIsOn() == 1) {
            this.isRevealed = true;

            if (this.hasMine) {
                this.btn.setGraphic(new ImageView((Image) this.board.getImages().get("bomb")));

            } else {
                this.btn.setGraphic(new ImageView((Image) this.board.getImages().get(Integer.toString(this.neighborMineCount))));

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

    public void flag() {
        if (this.board.getGameIsOn() == 1) {
            if (!this.isRevealed && this.isFlagged) {
                this.btn.setGraphic(new ImageView((Image) this.board.getImages().get("tile")));
                this.isFlagged = false;
                this.board.updateMinesLabel(1);
            } else if (Integer.parseInt(this.board.getMinesLabel().trim()) <= 0) {

            } else if (!this.isRevealed && !this.isFlagged) {
                this.btn.setGraphic(new ImageView((Image) this.board.getImages().get("flag")));
                this.isFlagged = true;
                this.board.updateMinesLabel(-1);
            }
        }
    }

    public Button getBtn() {
        return this.btn;
    }

    public void setHasMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public void incrementNeighborMineCount() {
        this.neighborMineCount++;
    }

    public void resetCell() {
        this.isRevealed = false;
        this.hasMine = false;
        this.neighborMineCount = 0;
        this.isFlagged = false;
        btn.setGraphic(new ImageView((Image) this.board.getImages().get("tile")));
    }

    public boolean getIsRevealed() {
        return this.isRevealed;
    }

    public boolean getHasMine() {
        return this.hasMine;
    }
}
