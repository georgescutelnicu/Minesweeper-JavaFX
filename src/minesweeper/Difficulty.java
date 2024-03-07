package minesweeper;

public class Difficulty {
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";

    public static int[] getSizeAndMines(String difficulty) {
        switch (difficulty) {
            case EASY:
                return new int[]{10, 10};
            case MEDIUM:
                return new int[]{16, 40};
            case HARD:
                return new int[]{20, 70};
            default:
                return new int[]{10, 10};
        }
    }
}
