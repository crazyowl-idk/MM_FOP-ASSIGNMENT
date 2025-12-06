package entities;

public class EnemyFactory {
    public static Enemy createEnemy(String name, String colour, String difficulty, int xp,
                                    double speed, double handling, double aggression,
                                    int lives, int discsOwned) {
        return switch (name.toLowerCase()) {
            case "clu" -> new Clu(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
            case "rinzler" -> new Rinzler(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
            case "sark" -> new Sark(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
            case "koura" -> new Koura(name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned);
            default -> throw new IllegalArgumentException("Unknown enemy type: " + name);
        };
    }
}

