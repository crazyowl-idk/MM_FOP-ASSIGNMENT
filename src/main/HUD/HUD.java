import java.util.ArrayList;
import java.util.List;

public class HUD {

    private double lives = 3;
    private int xp = 0;
    private int level = 1;
    private int discs = 1;
    private boolean cooldownReady = true;

    // ONE list for both HUD events + story events
    private List<String> events = new ArrayList<>();

    private final String BLUE = "\u001B[36m";
    private final String RED = "\u001B[31m";
    private final String GOLD = "\u001B[33m";
    private final String RESET = "\u001B[0m";

    // RESET
    public void reset() {
        lives = 3;
        xp = 0;
        level = 1;
        discs = 1;
        cooldownReady = true;
        events.clear();
    }

    // XP + LEVEL SYSTEM
    public void addXP(int amount) {
        xp += amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (xp >= 1000) {
            xp -= 1000;
            level++;
            log("Level Up! Now level " + level);

            if (level % 10 == 0) lives++;
        }
    }

    // LIFE / DISCS / COOLDOWN
    public void modifyLives(double delta) {
        lives += delta;
        if (lives < 0) lives = 0;
    }

    public void setCooldownReady(boolean value) { cooldownReady = value; }

    public void setDiscs(int d) { discs = d; }

    // EVENT LOG (combined HUD + Story events)
    public void log(String msg) {
        if (events.size() > 10) events.remove(0);
        events.add(msg);
    }

    // DISPLAY (HUD + EventLog combined)
    public void display() {
        clearScreen();

        System.out.println(BLUE + "==============================" + RESET);
        System.out.println(GOLD + " LIVES: " + RESET + formatLives() +
                "   " + GOLD + "XP: " + RESET + xp +
                "   " + GOLD + "LEVEL: " + RESET + level);

        System.out.println(GOLD + " DISCS: " + RESET + discs +
                "   " + GOLD + "COOLDOWN: " + RESET +
                (cooldownReady ? BLUE + "READY" + RESET : RED + "WAIT" + RESET));

        System.out.println(BLUE + "------------------------------" + RESET);
        System.out.println(BLUE + " EVENTS:" + RESET);

        if (events.isEmpty()) System.out.println(" - none");
        else for (String e : events) System.out.println(" - " + e);

        System.out.println(BLUE + "==============================" + RESET);
    }

    // HELPERS
    private String formatLives() {
        StringBuilder sc = new StringBuilder();
        int full = (int) Math.floor(lives);

        for (int i = 0; i < full; i++) sc.append("<3");

        if (lives - full >= 0.5) sc.append("<.5");

        return sc.toString();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // ============================================================
    // GETTERS
    // ============================================================
    public int getLevel() { return level; }
    public double getLives() { return lives; }
    public int getXp() { return xp; }
}
