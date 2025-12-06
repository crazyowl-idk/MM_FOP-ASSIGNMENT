import java.util.ArrayList;
import java.util.List;
public class HUD {

    private double lives = 3;
    private int xp = 0;
    private int level = 1;
    private int discs = 1;
    private boolean cool_down_ready = true;

    private List<String> localEvents = new ArrayList<>();

    private final String BLUE = "\u001B[36m";
    private final String RED = "\u001B[31m";
    private final String GOLD = "\u001B[33m";
    private final String RESET = "\u001B[0m";

    public void reset(){
        lives = 3;
        xp = 0;
        level = 1;
        discs = 1;
        cool_down_ready = true;
        localEvents.clear();
    }

    public void addXP(int amount){
        xp += amount;
        check_level_up();
    }

    public void modifyLives(double delta){
        lives += delta;
        if (lives < 0) lives = 0;
    }

    public void set_cool_down_ready(boolean v){cool_down_ready = v;}

    public void set_discs(int d){discs  = d;}

    private void check_level_up(){
        while (xp >= 1000){
            xp -= 1000;
            level++;
            localEvents.add("Level UP! Now level " +level);
            if (level % 10 == 0) lives++;
        }
    }

    //
    public void logEvent(String msg){
        if (localEvents.size() > 6) 
            localEvents.remove(0);
        localEvents.add(msg);
    }

    public void display_with_log(EventLog eventlog){
        clearScreen();
        System.out.println(BLUE + "==============================" +RESET);
        System.out.println(GOLD + " LIVES: " + RESET + formatLives() + "   " + GOLD + "XP: " + RESET + xp + "   " + GOLD + "LEVEL: " + RESET + level);
        System.out.println(GOLD + " DISCS: " + RESET + discs + "   " + GOLD + "COOLDOWN: " + RESET + (cool_down_ready ? BLUE + "READY" + RESET : RED + "WAIT" + RESET ));
        System.out.println(BLUE + "------------------------------");
        System.out.println(BLUE + "LOCAL EVENTS: " +RESET);
        if(localEvents.isEmpty()) System.out.println(" - none");
        else for (String e : localEvents) System.out.println(" - " + e);
        System.out.println(BLUE + "------------------------------");
        if (eventLog != null) eventLog.display();
        System.out.println(BLUE + "==============================" +RESET);
    }  

    //
    private String formatLives(){
        StringBuilder sc = new StringBuilder();
        int full = (int)Math.floor(lives);
        for(int i=0; i<full; i++) sc.append("<3");
        if(lives - full >= 0.5) sc.append("<.5");
        return sc.toString(); 
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public int getLevel() { return level; }
    public double getLives() {return lives; }
    public int getXp() {return xp; }
    
}
