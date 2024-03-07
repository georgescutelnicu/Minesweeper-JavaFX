package minesweeper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage window) {
        BorderPane layout = new BorderPane();

        ToggleGroup difficultyGroup = new ToggleGroup();
        RadioButton[] difficultyButtons = new RadioButton[]{
                new RadioButton("Easy"),
                new RadioButton("Medium"),
                new RadioButton("Hard")
        };

        for (RadioButton button : difficultyButtons) {
            button.setToggleGroup(difficultyGroup);
            button.setStyle("-fx-font-size: 12px;");
        }

        difficultyButtons[0].setSelected(true);

        Button startButton = new Button("Start");

        startButton.setOnAction(e -> {
            String selectedDifficulty = "";
            if (difficultyButtons[0].isSelected()) {
                selectedDifficulty = Difficulty.EASY;
            } else if (difficultyButtons[1].isSelected()) {
                selectedDifficulty = Difficulty.MEDIUM;
            } else if (difficultyButtons[2].isSelected()) {
                selectedDifficulty = Difficulty.HARD;
            }

            int[] sizeAndMines = Difficulty.getSizeAndMines(selectedDifficulty);
            int size = sizeAndMines[0];
            int mines = sizeAndMines[1];

            Board minesweeper = new Board(size, mines);
            BorderPane board = minesweeper.create_board();
            minesweeper.generateMines();

            Scene scene = new Scene(board);
            window.setScene(scene);
            window.setTitle("Minesweeper");
            window.getIcons().clear();
            window.getIcons().add(new Image(getClass().getResourceAsStream("images/icon.png")));
        });

        HBox radioButtons = new HBox(10);
        radioButtons.setAlignment(Pos.CENTER);
        radioButtons.getChildren().addAll(difficultyButtons);
        BorderPane.setMargin(radioButtons, new Insets(25, 0, 0, 0));

        layout.setTop(radioButtons);
        layout.setCenter(startButton);

        Scene scene = new Scene(layout, 250, 100);
        window.setTitle("Difficulty");
        window.setScene(scene);
        window.setResizable(false);
        window.getIcons().add(new Image(getClass().getResourceAsStream("images/settings.png")));
        window.show();
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}
