package minesweeper;

/**
 * The Difficulty class provides static methods to get size and number of mines based on the game difficulty.
 */
public class Difficulty {
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";

    /**
     * Returns an array containing the size and number of mines based on the specified difficulty level.
     *
     * @param difficulty The difficulty level of the game.
     * @return An array where the first element represents the size of the game board
     * and the second element represents the number of mines.
     * @throws IllegalArgumentException if the specified difficulty level is unknown.
     */
    public static int[] getSizeAndMines(String difficulty) {
        return switch (difficulty) {
            case EASY -> new int[]{10, 10};
            case MEDIUM -> new int[]{16, 40};
            case HARD -> new int[]{20, 70};
            default -> throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        };
    }
}
