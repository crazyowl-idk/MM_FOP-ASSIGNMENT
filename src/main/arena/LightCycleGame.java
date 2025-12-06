// LightCycleGame.java
import java.util.Scanner;
import LightCycle.Direction;
import Arena.ArenaType;

public class LightCycleGame {
    private final Arena arena;
    private final LightCycle player1;
    private boolean gameOver = false;
    private Scanner scanner;
    
    // Player ID constants for the grid
    private static final int PLAYER_1_WALL = 2;

    public LightCycleGame(ArenaType type, boolean isOpenType) {
        // 1. Initialize Arena
        this.arena = new Arena(type, isOpenType);
        
        // 2. Initialize Light Cycle (Starting near the center)
        int startPos = arena.getSize() / 4;
        this.player1 = new LightCycle(startPos, startPos, Direction.RIGHT);
        
        // 3. Setup Input
        this.scanner = new Scanner(System.in);
    }

    // --- Input Handling ---
    
    private void handleInput() {
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine().toUpperCase();
            if (!input.isEmpty()) {
                char key = input.charAt(0);
                switch (key) {
                    case 'W': player1.setDirection(Direction.UP); break;
                    case 'S': player1.setDirection(Direction.DOWN); break;
                    case 'A': player1.setDirection(Direction.LEFT); break;
                    case 'D': player1.setDirection(Direction.RIGHT); break;
                    case 'Q': gameOver = true; break; // Quit game
                }
            }
        }
    }

    // --- Game Logic ---
    
    private void update() {
        // 1. Check if the player is alive
        if (!player1.isAlive()) {
            gameOver = true;
            System.out.println("GAME OVER! Your cycle derezzed.");
            return;
        }

        // 2. Move the cycle and update its jetwall
        player1.move();

        int newX = player1.getX();
        int newY = player1.getY();
        
        // 3. Collision Check
        int cellValue = arena.getCell(newX, newY);
        
        if (cellValue == -1) { // Out of bounds
            if (arena.isOpenType()) {
                player1.loseAllLives();
                System.out.println("Fell off the open grid! Instant derezz.");
            } else {
                player1.loseHalfLife();
                System.out.println("Hit boundary wall! (-0.5 lives)");
            }
            // Simple logic: if hit, reset position or end turn (for simplicity here, we'll end the game)
            if (!player1.isAlive()) {
                gameOver = true;
            }
        } else if (cellValue == 1 || cellValue == PLAYER_1_WALL) { // Hit static wall or own jetwall
            player1.loseHalfLife();
            System.out.println("Hit jetwall/obstacle! (-0.5 lives)");
            if (!player1.isAlive()) {
                gameOver = true;
            }
        }

        // 4. Update the arena grid with the new jetwall position
        if (player1.isAlive()) {
            arena.setCell(newX, newY, PLAYER_1_WALL);
        }
    }

    // --- Rendering (Text-based for this example) ---

    private void render() {
        System.out.println("\n--- The Grid (" + arena.getSize() + "x" + arena.getSize() + ") ---");
        System.out.println("Lives: " + player1.getLives() + " | Use W/A/S/D to turn. Press Q to quit.");
        
        for (int y = 0; y < arena.getSize(); y++) {
            for (int x = 0; x < arena.getSize(); x++) {
                int cell = arena.getCell(x, y);
                char symbol = ' ';
                if (x == player1.getX() && y == player1.getY()) {
                    symbol = '@'; // The cycle itself
                } else if (cell == 1) {
                    symbol = '#'; // Static wall/Boundary
                } else if (cell == PLAYER_1_WALL) {
                    symbol = '*'; // Player 1 jetwall
                }
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }

    // --- Game Loop ---

    public void startGame() {
        System.out.println("Welcome to the Light Cycle Grid!");
        
        // In a real game, this would be a high-speed loop (e.g., a Timer or Thread)
        // For a console simulation, we'll prompt for input to advance
        while (!gameOver) {
            render();
            handleInput(); // Get direction change
            update();      // Process movement and collision
            
            // Wait for user to press enter to advance the cycle (simplified game speed)
            // System.out.println("Press Enter to advance...");
            // if (scanner.hasNextLine()) { scanner.nextLine(); } 
        }
        
        System.out.println("\nGame over. Final score/lives: " + player1.getLives());
        scanner.close();
    }

    public static void main(String[] args) {
        // Example setup: Predesigned Arena C, Closed-type grid (has boundaries)
        LightCycleGame game = new LightCycleGame(ArenaType.PREDESIGNED_C, false);
        game.startGame();
    }
}