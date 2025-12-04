package entities;

public class Clu extends Enemy {
    
    public Clu(String name, String colour, String difficulty, int xp, 
               double speed, double handling, double aggression, 
               int lives, int discsOwned) {
        
        super(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
    }

    // Implementing abstract methods from Character and/or Enemy
    @Override
    public void move(char direction) {
        // Clu's fast, strategic movement implementation
    }
    
    @Override
    public void throwDisc() {
        // Clu's high-precision disc throw logic
    }
    
    @Override
    public void determineNextAction(int playerX, int playerY) {
        // Clu's Brilliant AI strategy
    }
}