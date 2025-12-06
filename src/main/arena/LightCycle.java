// LightCycle.java
import java.util.LinkedList;
import java.util.List;

public class LightCycle {
    // Enum for directions
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private int x, y; // Current position on the 40x40 grid (0-39)
    private Direction direction;
    private double lives;
    private final List<int[]> jetwall; // Stores [x, y] coordinates of the trail

    // Constructor
    public LightCycle(int startX, int startY, Direction startDir) {
        this.x = startX;
        this.y = startY;
        this.direction = startDir;
        this.lives = 3.0; // Start with a default number of lives (can be adjusted)
        this.jetwall = new LinkedList<>();
        // Add starting position to the wall
        this.jetwall.add(new int[]{startX, startY});
    }

    // --- Movement and Wall Logic ---

    // Move the cycle one step in its current direction
    public void move() {
        // Update current position based on direction
        switch (direction) {
            case UP: y--; break;
            case DOWN: y++; break;
            case LEFT: x--; break;
            case RIGHT: x++; break;
        }
        
        // Add the new position to the jetwall (the trail)
        jetwall.add(new int[]{x, y});
    }

    // Set a new direction, preventing immediate 180-degree turns
    public void setDirection(Direction newDir) {
        if ((direction == Direction.UP && newDir != Direction.DOWN) ||
            (direction == Direction.DOWN && newDir != Direction.UP) ||
            (direction == Direction.LEFT && newDir != Direction.RIGHT) ||
            (direction == Direction.RIGHT && newDir != Direction.LEFT)) {
            this.direction = newDir;
        }
    }

    // --- Collision and Life Logic ---
    
    // Reduces life by 0.5 (for hitting a wall/jetwall)
    public void loseHalfLife() {
        this.lives -= 0.5;
        if (this.lives < 0) {
            this.lives = 0;
        }
    }
    
    // Lose all lives (for falling off an open-type grid)
    public void loseAllLives() {
        this.lives = 0;
    }

    // --- Getters ---

    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return direction; }
    public double getLives() { return lives; }
    public List<int[]> getJetwall() { return jetwall; }
    public boolean isAlive() { return lives > 0; }
}