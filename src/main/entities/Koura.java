package entities;


public class Koura extends Enemy {
    public Koura(String name, String colour, String difficulty, int xp, 
                   double speed, double handling, double aggression, 
                   int lives, int discsOwned) {
        super(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
    }
    
    @Override
    public void move(char direction) { /* Koura movement */ }
    @Override
    public void throwDisc() { /* Koura throw logic */ }
    @Override
    public void determineNextAction(int playerX, int playerY) {
        // Koura's Erratic, low AI strategy (Easy difficulty)
    }
}



