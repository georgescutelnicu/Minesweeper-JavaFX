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

/**
 * The Main class represents the entry point of the Minesweeper game application.
 * It provides a graphical user interface for selecting the difficulty level
 * and starting the game.
 */
public class Main extends Application {

    /**
     * Starts the Minesweeper game application.
     *
     * @param window The primary stage for the application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage window) {
        // Create main layout
        BorderPane layout = new BorderPane();

        // Create radio buttons for selecting difficulty level
        ToggleGroup difficultyGroup = new ToggleGroup();
        RadioButton[] difficultyButtons = new RadioButton[]{
                new RadioButton("Easy"),
                new RadioButton("Medium"),
                new RadioButton("Hard")
        };

        // Configure radio buttons
        for (RadioButton button : difficultyButtons) {
            button.setToggleGroup(difficultyGroup);
            button.setStyle("-fx-font-size: 12px;");
        }

        // Set the default selected difficulty button
        difficultyButtons[0].setSelected(true);

        // Create and handle start button
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

            // Get the size and number of mines based on the selected difficulty
            int[] sizeAndMines = Difficulty.getSizeAndMines(selectedDifficulty);
            int size = sizeAndMines[0];
            int mines = sizeAndMines[1];

            // Create a new Minesweeper board
            Board minesweeper = new Board(size, mines);
            BorderPane board = minesweeper.create_board();
            minesweeper.generateMines();

            // Create a scene and set it to the window
            Scene scene = new Scene(board);
            window.setScene(scene);
            window.setTitle("Minesweeper");
            window.getIcons().clear();
            window.getIcons().add(new Image(getClass().getResourceAsStream("images/icon.png")));
        });

        // Create a horizontal box for radio buttons
        HBox radioButtons = new HBox(10);
        radioButtons.setAlignment(Pos.CENTER);
        radioButtons.getChildren().addAll(difficultyButtons);
        BorderPane.setMargin(radioButtons, new Insets(25, 0, 0, 0));

        // Set the layout
        layout.setTop(radioButtons);
        layout.setCenter(startButton);

        // Create a scene and set it to the window
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
