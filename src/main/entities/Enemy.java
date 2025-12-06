package entities;

public abstract class Enemy extends Characters {
    protected double aggression;
    protected String difficulty;
    protected int xp;

    public Enemy(String name, String colour, String difficulty, int xp, double speed, double handling, double aggression, int lives, int discsOwned) {
        super(name, colour, speed, handling, lives, discsOwned);
        this.aggression = aggression;
        this.difficulty = difficulty;
        this.xp = xp;
    }


    @Override
    public void determineNextAction(int playerX, int playerY) {
    if (aggression > 0.7) {
        // Move towards player
        if (playerX > getX()) move('R');
        else if (playerX < getX()) move('L');
        if (playerY > getY()) move('D');
        else if (playerY < getY()) move('U');
    } else {
        // Random wandering
        char[] directions = {'U','D','L','R'};
        move(directions[(int)(Math.random() * directions.length)]);
    }

    throwDisc();
}


    @Override
    public void throwDisc() {
          double chance = 0.0;

    switch (difficulty.toLowerCase()) {
        case "easy" -> chance = 0.2; // 20% chance to throw
        case "medium" -> chance = 0.5; // 50% chance
        case "hard" -> chance = 0.8; // 80% chance
    }

    chance *= aggression;

    if (Math.random() < chance) {
        System.out.println(getName() + " throws a disc!");
    }
}

    public void takeDamage(int damage, Player player) {
    lives -= damage;
    if (lives <= 0) {
        die(player);
    }
}

    private void die(Player player) {
    System.out.println(getName() + " has been defeated!");
    player.addXp(xp); // reward XP to player
}
}
