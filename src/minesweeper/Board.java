package minesweeper;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;

public class Board {
    private int size;
    private int mines;
    private int gameIsOn;
    private List<List<Cell>> buttons;
    private HashMap<String, Image> images;

    private Label minesLabel;
    private Label timerLabel;
    private int timerCount;

    private Button btnImg;

    private int[] safeTile;

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

                cell.getBtn().setMaxSize(images.get("tile").getWidth(), images.get("tile").getHeight());
                cell.getBtn().setMinSize(images.get("tile").getWidth(), images.get("tile").getHeight());


                if (safeTile[0] == row && safeTile[1] == col) {
                    cell.getBtn().setGraphic(new ImageView(images.get("safeTile")));
                } else {
                    cell.getBtn().setGraphic(new ImageView(images.get("tile")));
                }

                cell.getBtn().setOnMouseClicked((e) -> {
                    if (((MouseEvent) e).getButton() == MouseButton.PRIMARY) {
                        cell.revealCell();
                    } else if (((MouseEvent) e).getButton() == MouseButton.SECONDARY) {
                        cell.flag();
                    }
                });
                grid.add(cell.getBtn(), row, col);
            }
            this.buttons.add(cellRow);
        }

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

    public void generateMines() {
        List<int[]> minesGenerated = new ArrayList<>();
        Random random = new Random();

        while (minesGenerated.size() < this.mines) {
            int[] mine = {random.nextInt(size), random.nextInt(size)};
            int[] rowRange = {this.safeTile[0] - 1, this.safeTile[0] + 2};
            int[] colRange = {this.safeTile[1] - 1, this.safeTile[1] + 2};
            boolean adjacentMine = false;

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

            boolean containsMine = false;
            for (int[] existingMine : minesGenerated) {
                if (Arrays.equals(existingMine, mine)) {
                    containsMine = true;
                    break;
                }
            }

            if (!adjacentMine && !containsMine) {
                minesGenerated.add(mine);
            }
        }

        for (int[] mine : minesGenerated) {
            int mineRow = mine[0];
            int mineCol = mine[1];
            this.buttons.get(mineRow).get(mineCol).setHasMine(true);

            for (int i = mineRow - 1; i <= mineRow + 1; i++) {
                for (int j = mineCol - 1; j <= mineCol + 1; j++) {
                    if (0 <= i && i < this.size && 0 <= j && j < this.size) {
                        this.buttons.get(i).get(j).incrementNeighborMineCount();
                    }
                }
            }
        }
    }

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

    public void restartGame() {
        this.gameIsOn = 1;
        this.timerCount = 0;

        this.timerLabel.setText("000");
        this.minesLabel.setText(" " + this.mines);

        for (List<Cell> row : buttons) {
            for (Cell cell : row) {
                cell.resetCell();
            }
        }

        Random random = new Random();
        this.safeTile = new int[]{random.nextInt(size), random.nextInt(size)};
        this.buttons.get(safeTile[0]).get(safeTile[1]).getBtn().setGraphic(new ImageView(images.get("safeTile")));

        generateMines();
        btnImg.setGraphic(new ImageView(images.get("yellow")));
    }


    public int getGameIsOn() {
        return this.gameIsOn;
    }

    public String getMinesLabel() {
        return this.minesLabel.getText();
    }

    public void updateMinesLabel(int num) {
        int currentMinesCount = Integer.parseInt(this.minesLabel.getText().trim());
        currentMinesCount += num;
        if (currentMinesCount < 10) {
            this.minesLabel.setText(" 0" + currentMinesCount);
        } else {
            this.minesLabel.setText(" " + currentMinesCount);
        }
    }

    public Map getImages() {
        return this.images;
    }

    @SuppressWarnings("ConstantConditions")
    private void loadImages() {
        try {
            this.images.put("safeTile", new Image(getClass().getResourceAsStream("images/safe.png")));
            this.images.put("bomb", new Image(getClass().getResourceAsStream("images/bomb.png")));
            this.images.put("flag", new Image(getClass().getResourceAsStream("images/flag.png")));
            this.images.put("tile", new Image(getClass().getResourceAsStream("images/tile.png")));
            this.images.put("yellow", new Image(getClass().getResourceAsStream("images/yellow.png")));
            this.images.put("green", new Image(getClass().getResourceAsStream("images/green.png")));
            this.images.put("red", new Image(getClass().getResourceAsStream("images/red.png")));
            this.images.put("timer", new Image(getClass().getResourceAsStream("images/timer.png")));

            for (int i = 0; i <= 8; i++) {
                this.images.put(String.valueOf(i), new Image(getClass().getResourceAsStream("images/" + i + ".png")));
            }
        } catch (NullPointerException e) {
            System.err.println("Failed to load image resources: " + e.getMessage());
        }
    }
}
