public class Player {
    private int x;
    private int y;
    private double lives;
    private char id; // e.g., '1', '2' for identification

    public Player(int startX, int startY, char id) {
        this.x = startX;
        this.y = startY;
        this.lives = 3.0; // Start with 3 lives
        this.id = id;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public double getLives() { return lives; }
    public char getId() { return id; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void loseLife(double amount) { this.lives -= amount; }

    /**
     * Attempts to move the player and returns the new position.
     * @param direction 'W', 'A', 'S', or 'D'
     * @return int[] {newX, newY}
     */
    public int[] getNextPosition(char direction) {
        int newX = this.x;
        int newY = this.y;

        switch (direction) {
            case 'W': // Up (Y decreases on typical screen coordinates)
                newY--;
                break;
            case 'S': // Down
                newY++;
                break;
            case 'A': // Left
                newX--;
                break;
            case 'D': // Right
                newX++;
                break;
        }
        return new int[]{newX, newY};
    }
}
