import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class StoryEngine {

    private Map<String, List<String>> storyMap = new HashMap<>();
    private Set<String> unlocked = new HashSet<>();
    private Scanner sc = new Scanner(System.in); // For pausing between scenes

    // MAIN FOR TESTING (FULL PROGRESSION) 
    public static void main(String[] args) throws IOException {
        StoryEngine engine = new StoryEngine();

        engine.loadStory("story.txt");

        // Simulate leveling from 1 to 99
        for (int level = 1; level <= 99; level++) {
            engine.triggerProgression(level);
        }

        engine.presentEnding();
    }

    // LOAD STORY.TXT 
    public void loadStory(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);

            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                int idx = line.indexOf(":");
                if (idx < 0) continue;

                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();

                storyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }

            System.out.println("Story loaded successfully!");

        } catch (IOException e) {
            System.out.println("ERROR: Could not load story file\n" + e.getMessage());
        }
    }

    // TRIGGER PROGRESSION BY LEVEL 
    public void triggerProgression(int level) throws IOException {
        // Chapter progression
        if (level == 1)  playChapter(1);
        if (level == 10) playChapter(2);
        if (level == 19) playChapter(3);
        if (level == 28) playChapter(4);
        if (level == 37) playChapter(5);
        if (level == 46) playChapter(6);
        if (level == 55) playChapter(7);
        if (level == 64) playChapter(8);
        if (level == 73) playChapter(9);
        if (level == 82) playChapter(10);
        if (level == 91) playChapter(11);

        // Unlock logic
        if (level == 10) unlock("kevin");
        if (level == 30) unlock("iso_range");
        if (level == 45) unlock("disc_slot");
        if (level == 50) unlock("cooldown");
        if (level == 64) unlock("rinzler_choice");  
    }

    // PLAY A WHOLE CHAPTER 
    public void playChapter(int chapterNumber) throws IOException {
        System.out.println("\nCHAPTER " + chapterNumber + " START \n");

        for (int scene = 1; scene <= 10; scene++) {
            String key = "chapter" + chapterNumber + ".scene" + scene;

            if (storyMap.containsKey(key)) {
                playScene(key);
            }
        }

        System.out.println("CHAPTER " + chapterNumber + " END\n");
    }

    // PLAY A SCENE 
    public void playScene(String key) throws IOException {
        List<String> lines = storyMap.get(key);
        if (lines == null) return;

        // Scene header
        System.out.println(key);

        // Scene content
        for (String line : lines) {
            System.out.println(line);
        }

        // Pause
        pause();
    }

    // UNLOCK SYSTEM
    public void unlock(String id) throws IOException {
        if (unlocked.contains(id)) return;

        unlocked.add(id);
        System.out.println("[UNLOCKED] " + id);

        String key = "unlock." + id;

        if (storyMap.containsKey(key)) {
            playScene(key);
        }
    }

    // ENDING LOGIC
    public void presentEnding() throws IOException {
        String endingKey = "ending.order"; // default

        if (unlocked.contains("kevin"))
            endingKey = "ending.hero";

        if (unlocked.contains("rinzler_choice"))
            endingKey = "ending.legacy";

        playScene(endingKey);
    }

    // HELPERS 
    private void pause() {
        System.out.println("Press Enter to continue...");
        sc.nextLine(); // Wait for user to press Enter
    }
}
