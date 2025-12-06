// Arena.java
import java.util.Random;

public class Arena {
    private final int size = 40;
    // 0: Empty, 1: Static Wall/Boundary, 2: Player 1 Wall, 3: Player 2 Wall (etc.)
    private int[][] grid; 
    private final boolean isOpenType;
    private final Random random = new Random();

    // Enum for predesigned arenas
    public enum ArenaType {
        PREDESIGNED_A, PREDESIGNED_B, PREDESIGNED_C, RANDOMLY_GENERATED
    }

    // Constructor initializes the grid based on the chosen type
    public Arena(ArenaType type, boolean isOpenType) {
        this.grid = new int[size][size];
        this.isOpenType = isOpenType;
        generateArena(type);
    }

    // --- Arena Generation Logic ---

    private void generateArena(ArenaType type) {
        // Initialize an empty grid
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = 0;
            }
        }
        
        if (!isOpenType) {
            // Add boundary walls for closed arenas
            for (int i = 0; i < size; i++) {
                grid[0][i] = 1; // Top
                grid[size - 1][i] = 1; // Bottom
                grid[i][0] = 1; // Left
                grid[i][size - 1] = 1; // Right
            }
        }

        switch (type) {
            case PREDESIGNED_A:
                // Simple inner box obstacle (Example)
                for (int i = 10; i < 30; i++) {
                    grid[10][i] = 1; 
                    grid[29][i] = 1;
                    grid[i][10] = 1;
                    grid[i][29] = 1;
                }
                break;
            case PREDESIGNED_B:
                // Cross pattern obstacle (Example)
                for (int i = 5; i < 35; i++) {
                    grid[20][i] = 1; 
                    grid[i][20] = 1;
                }
                break;
            case PREDESIGNED_C:
                // Complex design (Similar to the image provided) - Simplified Example
                for (int i = 5; i < 35; i++) {
                    grid[5][i] = 1;
                    grid[34][i] = 1;
                }
                for (int i = 5; i < 15; i++) {
                    grid[i][5] = 1;
                    grid[i][34] = 1;
                }
                for (int i = 25; i < 35; i++) {
                    grid[i][5] = 1;
                    grid[i][34] = 1;
                }
                break;
            case RANDOMLY_GENERATED:
                // Generates random static walls (1)
                int numObstacles = random.nextInt(40) + 20; // 20 to 59 obstacles
                for (int k = 0; k < numObstacles; k++) {
                    // Ensure random walls aren't on the outer boundary or near the center start
                    int wallX = random.nextInt(size - 2) + 1;
                    int wallY = random.nextInt(size - 2) + 1;
                    grid[wallY][wallX] = 1;
                }
                break;
        }
    }

    // --- Grid Interaction ---

    public int getCell(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return -1; // Out of bounds indicator
        }
        return grid[y][x];
    }

    public void setCell(int x, int y, int value) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            grid[y][x] = value;
        }
    }

    // --- Getters ---

    public int getSize() { return size; }
    public boolean isOpenType() { return isOpenType; }
}