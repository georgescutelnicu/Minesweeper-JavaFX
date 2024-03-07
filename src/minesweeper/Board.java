package minesweeper;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.*;

/**
 * Represents the game board for Minesweeper.
 */
public class Board {
    private final int size;   // Size of the board
    private final int mines;  // Number of mines on the board
    private int gameIsOn;   // Current game state
    private final List<List<Cell>> buttons;   // Grid of cells representing the board
    private final HashMap<String, Image> images;  // Images for different cell states

    private final Label minesLabel;   // Label to display remaining mines count
    private final Label timerLabel;   // Label to display elapsed time
    private int timerCount; // Counter for elapsed time
    private final Button btnImg;  // Button to restart the game

    private int[] safeTile; // Coordinates of the safe tile

    /**
     * Constructs a Minesweeper board with the specified size and number of mines.
     *
     * @param size  The size of the board.
     * @param mines The number of mines on the board.
     */
    public Board(int size, int mines) {
        this.size = size;
        this.mines = mines;
        this.gameIsOn = 1;

        this.buttons = new ArrayList<>();
        this.images = new HashMap<>();
        this.loadImages();

        this.minesLabel = new Label(" 10");
        this.timerLabel = new Label("0   ");
        this.timerCount = 0;
        this.btnImg = new Button();

        this.safeTile = new int[2];
    }

    /**
     * Creates the graphical representation of the game board.
     *
     * @return The BorderPane containing the game board.
     */
    public BorderPane create_board() {
        updateTimer();

        Random random = new Random();
        this.safeTile = new int[]{random.nextInt(size), random.nextInt(size)};

        BorderPane layout = new BorderPane();
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);

        for (int row = 0; row < size; row++) {
            List<Cell> cellRow = new ArrayList<>();
            for (int col = 0; col < size; col++) {
                Cell cell = new Cell(this, row, col);
                cellRow.add(cell);

                // Set cell button size and graphics
                cell.getBtn().setMaxSize(images.get("tile").getWidth(), images.get("tile").getHeight());
                cell.getBtn().setMinSize(images.get("tile").getWidth(), images.get("tile").getHeight());
                if (safeTile[0] == row && safeTile[1] == col) {
                    cell.getBtn().setGraphic(new ImageView(images.get("safeTile")));
                } else {
                    cell.getBtn().setGraphic(new ImageView(images.get("tile")));
                }

                // Handle mouse clicks on cell buttons
                cell.getBtn().setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        cell.revealCell();
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        cell.flag();
                    }
                });
                grid.add(cell.getBtn(), row, col);
            }
            this.buttons.add(cellRow);
        }

        // Create header with mines label, timer label and restart button
        BorderPane header = new BorderPane();
        HBox minesBox = new HBox();
        HBox timerBox = new HBox();

        Label minesLabelImage = new Label();
        minesLabelImage.setGraphic(new ImageView(images.get("bomb")));
        this.minesLabel.setFont(Font.font(20));
        minesBox.getChildren().addAll(minesLabelImage, minesLabel);

        Label timerLabelImage = new Label();
        timerLabelImage.setGraphic(new ImageView(images.get("timer")));
        this.timerLabel.setFont(Font.font(20));
        timerBox.getChildren().addAll(timerLabel, timerLabelImage);
        HBox.setMargin(timerLabel, new Insets(0, 10, 0, 0));


        this.btnImg.setGraphic(new ImageView(images.get("yellow")));
        this.btnImg.setMaxSize(images.get("yellow").getWidth(), images.get("yellow").getHeight());
        this.btnImg.setMinSize(images.get("yellow").getWidth(), images.get("yellow").getHeight());
        this.btnImg.setOnAction(e -> restartGame());

        minesBox.setStyle("-fx-padding: 0 15 0 0;");
        minesBox.setAlignment(Pos.CENTER_LEFT);
        timerBox.setAlignment(Pos.CENTER_RIGHT);

        header.setLeft(minesBox);
        header.setRight(timerBox);
        header.setCenter(this.btnImg);

        header.setMinHeight(50);
        header.setStyle("-fx-border-color: gray; -fx-border-width: 2px; -fx-border-style: solid;");
        header.setPadding(new Insets(3, 3, 3, 3));
        BorderPane.setMargin(header, new Insets(5, 0, 5, 0));

        layout.setTop(header);
        layout.setCenter(grid);

        return layout;
    }

    /**
     * Randomly generates mines on the game board and update neighbor mine counts.
     */
    public void generateMines() {
        List<int[]> minesGenerated = new ArrayList<>();
        Random random = new Random();

        // Generate mines until the specified number is reached
        while (minesGenerated.size() < this.mines) {
            int[] mine = {random.nextInt(size), random.nextInt(size)};
            int[] rowRange = {this.safeTile[0] - 1, this.safeTile[0] + 2};
            int[] colRange = {this.safeTile[1] - 1, this.safeTile[1] + 2};
            boolean adjacentMine = false;

            // Check if the mine is adjacent to the safe tile
            for (int i = rowRange[0]; i < rowRange[1]; i++) {
                for (int j = colRange[0]; j < colRange[1]; j++) {
                    if (0 <= i && i < this.size && 0 <= j && j < this.size) {
                        if (mine[0] == i && mine[1] == j) {
                            adjacentMine = true;
                            break;
                        }
                    }
                }
                if (adjacentMine) {
                    break;
                }
            }

            // Check if the mine is already generated
            boolean containsMine = false;
            for (int[] existingMine : minesGenerated) {
                if (Arrays.equals(existingMine, mine)) {
                    containsMine = true;
                    break;
                }
            }

            // Add the mine if it meets the conditions
            if (!adjacentMine && !containsMine) {
                minesGenerated.add(mine);
            }
        }

        // Place mines on the board and update neighboring cell counts
        for (int[] mine : minesGenerated) {
            int mineRow = mine[0];
            int mineCol = mine[1];
            this.buttons.get(mineRow).get(mineCol).setHasMine(true);

            // Update neighboring cell counts
            for (int i = mineRow - 1; i <= mineRow + 1; i++) {
                for (int j = mineCol - 1; j <= mineCol + 1; j++) {
                    if (0 <= i && i < this.size && 0 <= j && j < this.size) {
                        this.buttons.get(i).get(j).incrementNeighborMineCount();
                    }
                }
            }
        }
    }

    /**
     * Recursively reveal neighboring cells when a cell with no neighboring mines is revealed.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    public void revealNeighbors(int row, int col) {
        int[] dx = {-1, 0, 1};
        int[] dy = {-1, 0, 1};

        for (int i : dx) {
            for (int j : dy) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int r = row + i;
                int c = col + j;
                if (r >= 0 && r < size && c >= 0 && c < size) {
                    Cell neighborCell = this.buttons.get(r).get(c);
                    if (!neighborCell.getIsRevealed()) {
                        neighborCell.revealCell();
                    }
                }
            }
        }
    }

    /**
     * Checks if the player has lost the game.
     */
    public void checkLoss() {
        for (List<Cell> row : this.buttons) {
            for (Cell cell : row) {
                if (cell.getHasMine() && cell.getIsRevealed()) {
                    this.gameIsOn = 0;
                    return;
                }
            }
        }
    }

    /**
     * Checks if the player has won the game.
     */
    public void checkWin() {
        int squaresDiscovered = 0;

        for (List<Cell> row : buttons) {
            for (Cell cell : row) {
                if (!cell.getHasMine() && cell.getIsRevealed()) {
                    squaresDiscovered++;
                    if (squaresDiscovered == (this.size * this.size - mines)) {
                        this.gameIsOn = 2;
                    }
                }
            }
        }
    }

    /**
     * Check the current state of the game (ongoing(1), lost(0), or won(2)) and update UI accordingly.
     */
    public void isGameInProgress() {
        this.checkLoss();
        this.checkWin();

        if (this.gameIsOn == 0) {
            this.btnImg.setGraphic(new ImageView(images.get("red")));
            this.minesLabel.setText(" " + this.mines);
            for (List<Cell> row : this.buttons) {
                for (Cell cell: row) {
                    if (cell.getHasMine()) {
                        cell.getBtn().setGraphic(new ImageView(this.images.get("bomb")));
                    }
                }
            }

        } else if (this.gameIsOn == 2) {
            this.btnImg.setGraphic(new ImageView(images.get("green")));
            this.minesLabel.setText(" 00");
            for (List<Cell> row : this.buttons) {
                for (Cell cell: row) {
                    if (cell.getHasMine()) {
                        cell.getBtn().setGraphic(new ImageView(this.images.get("flag")));
                    }
                }
            }
        }
    }

    /**
     * Updates the timer label to display elapsed time.
     */
    public void updateTimer() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameIsOn == 1) {
                    timerCount++;
                    Platform.runLater(() -> {
                        String formattedTime = String.format("%03d", timerCount);
                        timerLabel.setText(formattedTime);
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    /**
     * Restarts the game.
     */
    public void restartGame() {
        this.gameIsOn = 1;
        this.timerCount = 0;

        this.timerLabel.setText("000");
        this.minesLabel.setText(" " + this.mines);

        // Reset all cells on the board
        for (List<Cell> row : buttons) {
            for (Cell cell : row) {
                cell.resetCell();
            }
        }

        // Generate new safe tile and mines
        Random random = new Random();
        this.safeTile = new int[]{random.nextInt(size), random.nextInt(size)};
        this.buttons.get(safeTile[0]).get(safeTile[1]).getBtn().setGraphic(new ImageView(images.get("safeTile")));

        generateMines();
        btnImg.setGraphic(new ImageView(images.get("yellow")));
    }

    /**
     * Gets the current state of the game.
     *
     * @return An integer representing the current state of the game.
     *         0: Game lost, 1: Game in progress, 2: Game won.
     */
    public int getGameIsOn() {
        return this.gameIsOn;
    }

    /**
     * Gets the text content of the mines label.
     *
     * @return The text content of the mines label.
     */
    public String getMinesLabel() {
        return this.minesLabel.getText();
    }

    /**
     * Retrieves the map containing images for various game elements.
     *
     * @return A map containing images for different game elements.
     *         The keys represent the names of the game elements,
     *         and the values represent the corresponding images.
     */
    public Map<String, Image> getImages() {
        return this.images;
    }

    /**
     * Updates the mines label with the given number.
     *
     * @param num The number to update the mines label with.
     */
    public void updateMinesLabel(int num) {
        int currentMinesCount = Integer.parseInt(this.minesLabel.getText().trim());
        currentMinesCount += num;
        if (currentMinesCount < 10) {
            this.minesLabel.setText(" 0" + currentMinesCount);
        } else {
            this.minesLabel.setText(" " + currentMinesCount);
        }
    }

    /**
     * Loads images for various game elements.
     */
    @SuppressWarnings("ConstantConditions")
    private void loadImages() {
        try {
            // Load images for different game elements
            this.images.put("safeTile", new Image(getClass().getResourceAsStream("images/safe.png")));
            this.images.put("bomb", new Image(getClass().getResourceAsStream("images/bomb.png")));
            this.images.put("flag", new Image(getClass().getResourceAsStream("images/flag.png")));
            this.images.put("tile", new Image(getClass().getResourceAsStream("images/tile.png")));
            this.images.put("yellow", new Image(getClass().getResourceAsStream("images/yellow.png")));
            this.images.put("green", new Image(getClass().getResourceAsStream("images/green.png")));
            this.images.put("red", new Image(getClass().getResourceAsStream("images/red.png")));
            this.images.put("timer", new Image(getClass().getResourceAsStream("images/timer.png")));

            // Load number images
            for (int i = 0; i <= 8; i++) {
                this.images.put(String.valueOf(i), new Image(getClass().getResourceAsStream(
                        "images/" + i + ".png")));
            }
        } catch (NullPointerException e) {
            // Handle image loading failure
            System.err.println("Failed to load image resources: " + e.getMessage());
        }
    }
}
