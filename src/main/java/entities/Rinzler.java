package entities;

public class Rinzler extends Enemy {
    public Rinzler(String name, String colour, String difficulty, int xp, 
                   double speed, double handling, double aggression, 
                   int lives, int discsOwned) {
        super(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
    }
    
    @Override
    public void move(char direction) { /* Rinzler movement */ }
    @Override
    public void throwDisc() { /* Rinzler throw logic */ }
    @Override
    public void determineNextAction(int playerX, int playerY) {
        // Rinzler's Clever, tactical AI strategy (Hard difficulty)
    }
}
