package minesweeper;

public class Difficulty {
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";

    public static int[] getSizeAndMines(String difficulty) {
        return switch (difficulty) {
            case EASY -> new int[]{10, 10};
            case MEDIUM -> new int[]{16, 40};
            case HARD -> new int[]{20, 70};
            default -> throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        };
    }
}
