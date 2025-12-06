package entities;

public class Sark extends Enemy {
    public Sark(String name, String colour, String difficulty, int xp, 
                   double speed, double handling, double aggression, 
                   int lives, int discsOwned) {
        super(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
    }
    
    @Override
    public void move(char direction) { /* Sark movement */ }
    @Override
    public void throwDisc() { /* Sark throw logic */ }
    @Override
    public void determineNextAction(int playerX, int playerY) {
        // Sark's Predictable, moderate AI strategy (Medium difficulty)
    }
}