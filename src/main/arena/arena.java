import java.util.Arrays;
import java.util.Random;

public class Arena {
    private static final int SIZE = 40;
    private TileType[][] grid;
    private boolean isOpen; // True for open boundary, False for closed

    public Arena(boolean isOpen, int preset) {
        this.isOpen = isOpen;
        this.grid = new TileType[SIZE][SIZE];
        initializeArena(preset);
    }

    // --- 1. Arena Construction ---

    /**
     * Initializes the grid based on a preset or by generating a random one.
     * @param preset 0 for Random, 1-3 for custom layouts.
     */
    private void initializeArena(int preset) {
        if (preset == 0) {
            generateRandomArena();
        } else {
            // Fill with EMPTY tiles first
            for (int y = 0; y < SIZE; y++) {
                Arrays.fill(grid[y], TileType.EMPTY);
            }
            // Add boundary walls for closed arena
            if (!isOpen) {
                for (int i = 0; i < SIZE; i++) {
                    grid[0][i] = grid[SIZE - 1][i] = TileType.WALL; // Top/Bottom
                    grid[i][0] = grid[i][SIZE - 1] = TileType.WALL; // Left/Right
                }
            }

            // Implement Preset Logic (simple examples)
            switch (preset) {
                case 1: // Simple central cross
                    for (int i = 10; i < 30; i++) {
                        grid[20][i] = TileType.WALL;
                        grid[i][20] = TileType.WALL;
                    }
                    break;
                case 2: // Corner blocks
                    placeObstacle(5, 5, 5, 5, TileType.OBSTACLE);
                    placeObstacle(30, 30, 5, 5, TileType.OBSTACLE);
                    break;
                case 3: // Speed lanes
                    for (int i = 1; i < SIZE - 1; i++) {
                        if (i % 5 == 0) {
                            grid[i][15] = TileType.SPEED_RAMP;
                            grid[i][25] = TileType.SPEED_RAMP;
                        }
                    }
                    break;
            }
        }
    }
    
    // Helper for placing blocks
    private void placeObstacle(int startX, int startY, int width, int height, TileType type) {
        for (int y = startY; y < Math.min(startY + height, SIZE); y++) {
            for (int x = startX; x < Math.min(startX + width, SIZE); x++) {
                grid[y][x] = type;
            }
        }
    }

    // --- Random Arena Generator (Required for extra marks) ---
    private void generateRandomArena() {
        Random rand = new Random();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                // 15% chance for a WALL, 5% for an OBSTACLE
                int r = rand.nextInt(100);
                if (r < 15) {
                    grid[y][x] = TileType.WALL;
                } else if (r < 20) {
                    grid[y][x] = TileType.OBSTACLE;
                } else {
                    grid[y][x] = TileType.EMPTY;
                }
            }
        }
    }


    // --- 2. & 3. Player Movement & Jetwall Logic ---

    /**
     * Performs one movement tick for the player.
     * @param player The Player object
     * @param direction 'W', 'A', 'S', 'D'
     * @return true if the move was successful and the player is alive, false otherwise.
     */
    public boolean movePlayer(Player player, char direction) {
        // 3a. Leave a glowing trail (Jetwall) at the player's CURRENT position
        // Only set the jetwall if the player is still alive from the last tick
        grid[player.getY()][player.getX()] = TileType.PLAYER_JETWALL;

        // 2a. Calculate next position
        int[] nextPos = player.getNextPosition(direction);
        int newX = nextPos[0];
        int newY = nextPos[1];

        // --- 4. Boundary Rules Check ---
        if (newX < 0 || newX >= SIZE || newY < 0 || newY >= SIZE) {
            if (isOpen) { // Open arena: falling off = instant death
                player.loseLife(player.getLives()); // Set lives to 0 or equivalent
                System.out.println("Player " + player.getId() + " fell off the map! Instant Death.");
                return false;
            } else { // Closed arena: touching boundary wall = -0.5 lives
                player.loseLife(0.5);
                System.out.println("Player " + player.getId() + " hit the boundary wall (closed arena)! Lives: " + player.getLives());
                // Prevent movement: player stays in current position
                return player.getLives() > 0; 
            }
        }

        // 3b. Jetwall and Wall/Obstacle Collision Check (Illegal Moves)
        TileType nextTile = grid[newY][newX];
        
        // 2b. Prevent illegal moves (colliding into walls/obstacles)
        if (nextTile == TileType.WALL || nextTile == TileType.OBSTACLE) {
            player.loseLife(0.5);
            System.out.println("Player " + player.getId() + " hit a Wall/Obstacle! Lives: " + player.getLives());
            // Prevent movement: player stays in current position
            return player.getLives() > 0;
        } 
        
        // 3b. Contact with jetwall = -0.5 lives
        else if (nextTile == TileType.PLAYER_JETWALL) {
            player.loseLife(0.5);
            System.out.println("Player " + player.getId() + " hit a Jetwall! Lives: " + player.getLives());
            // Move anyway, but they lose life. The game logic will check if they are still alive.
        }

        // --- 2c. Smooth Movement Update ---
        player.setX(newX);
        player.setY(newY);
        // Mark the player's new position on the grid for display (not a permanent jetwall)
        grid[newY][newX] = getPlayerTileType(player.getId());


        // Optional: Speed Ramp Logic
        if (nextTile == TileType.SPEED_RAMP) {
            System.out.println("Player " + player.getId() + " hit a Speed Ramp! (Movement speed would increase here)");
            // In a real game loop, this would affect the number of steps or ticks per movement.
        }
        
        return player.getLives() > 0;
    }
    
    // Simple way to represent the player on the grid visually (not used for collision)
    private TileType getPlayerTileType(char id) {
        // In a real application, you'd handle drawing the player icon,
        // but for a TileType enum, we just mark it as EMPTY after they move.
        // For debugging, we can temporarily mark it:
        return TileType.EMPTY; // The jetwall is the only permanent marker.
    }


    // --- Display Method for Console Debugging ---
    public void displayArena(Player player1, Player player2) {
        System.out.println("\n--- Arena (P1: " + player1.getLives() + " | P2: " + player2.getLives() + ") ---");
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (player1.getX() == x && player1.getY() == y) {
                    System.out.print('1'); // Player 1 Icon
                } else if (player2.getX() == x && player2.getY() == y) {
                    System.out.print('2'); // Player 2 Icon
                } else {
                    switch (grid[y][x]) {
                        case EMPTY:
                            System.out.print('.');
                            break;
                        case WALL:
                            System.out.print('#');
                            break;
                        case OBSTACLE:
                            System.out.print('O');
                            break;
                        case SPEED_RAMP:
                            System.out.print('S');
                            break;
                        case PLAYER_JETWALL:
                            System.out.print('*');
                            break;
                    }
                }
            }
            System.out.println();
        }
        System.out.println("------------------------------------");
    }
}