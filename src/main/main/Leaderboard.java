import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Leaderboard {

    /* -----------------------
       Domain: Player (game state)
       ----------------------- */
    public static class Player {
        public String playerName;
        public String characterChosen;
        public int level;
        public int xp;
        public int lives;
        public int discsUpgrades;
        public String currentChapter;
        public List<String> unlockedArenas = new ArrayList<>();
        public Set<String> achievements = new LinkedHashSet<>();
        public LocalDate lastSaved;

        // runtime flag to indicate unsaved changes (useful for UI)
        public transient boolean dirty = false;

        public Player(String name, String character) {
            this.playerName = name;
            this.characterChosen = character;
            this.level = 1;
            this.xp = 0;
            this.lives = 3;
            this.discsUpgrades = 0;
            this.currentChapter = "Prologue";
            this.lastSaved = LocalDate.now();
            this.dirty = true;
        }

        public int totalScore() {
            // scoring formula used for leaderboard: tweak as required
            return level * 100 + xp + achievements.size() * 50;
        }

        @Override
        public String toString() {
            return String.format("Player{name=%s, char=%s, lvl=%d, xp=%d, lives=%d, discs=%d, chapter=%s, arenas=%s, ach=%s, lastSaved=%s}",
                    playerName, characterChosen, level, xp, lives, discsUpgrades, currentChapter,
                    unlockedArenas, achievements, lastSaved);
        }
    }

    /* -----------------------
       Save format enum
       ----------------------- */
    public enum SaveFormat { TEXT, CSV, JSON }

    /* -----------------------
       SaveManager: save/load Player in different formats
       ----------------------- */
    public static class SaveManager {
        private static final Path SAVE_DIR = Paths.get("saves");

        static {
            try { if (!Files.exists(SAVE_DIR)) Files.createDirectories(SAVE_DIR); }
            catch (IOException e) { /* if directory creation fails, operations will throw later */ }
        }

        // safe filename derived from player name + extension
        public static String saveFilePath(Player p, SaveFormat fmt) {
            String safe = p.playerName.replaceAll("[^a-zA-Z0-9_\\- ]", "_").replace(' ', '_');
            String ext = fmt == SaveFormat.JSON ? "json" : (fmt == SaveFormat.CSV ? "csv" : "txt");
            return SAVE_DIR.resolve(safe + "." + ext).toString();
        }

        // Save with option to append (useful for CSV/text logging)
        public static void save(Player p, SaveFormat fmt, boolean append) throws IOException {
            if (!Files.exists(SAVE_DIR)) Files.createDirectories(SAVE_DIR);
            p.lastSaved = LocalDate.now();
            p.dirty = false;
            String path = saveFilePath(p, fmt);
            switch (fmt) {
                case TEXT: writeText(path, p, append); break;
                case CSV:  writeCsv(path, p, append); break;
                case JSON: writeJson(path, p, append); break;
            }
        }

        private static void writeText(String path, Player p, boolean append) throws IOException {
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(path),
                    StandardOpenOption.CREATE, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING)) {
                w.write("playerName=" + p.playerName); w.newLine();
                w.write("characterChosen=" + p.characterChosen); w.newLine();
                w.write("level=" + p.level); w.newLine();
                w.write("xp=" + p.xp); w.newLine();
                w.write("lives=" + p.lives); w.newLine();
                w.write("discsUpgrades=" + p.discsUpgrades); w.newLine();
                w.write("currentChapter=" + p.currentChapter); w.newLine();
                w.write("unlockedArenas=" + String.join(";", p.unlockedArenas)); w.newLine();
                w.write("achievements=" + String.join(";", p.achievements)); w.newLine();
                w.write("lastSaved=" + p.lastSaved); w.newLine();
                w.write("#---"); w.newLine();
            }
        }

        private static void writeCsv(String path, Player p, boolean append) throws IOException {
            boolean exists = Files.exists(Paths.get(path));
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(path),
                    StandardOpenOption.CREATE, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING)) {
                if (!exists || !append) {
                    w.write("playerName,characterChosen,level,xp,lives,discsUpgrades,currentChapter,unlockedArenas,achievements,lastSaved");
                    w.newLine();
                }
                String unlocked = escapeCsv(String.join(";", p.unlockedArenas));
                String ach = escapeCsv(String.join(";", p.achievements));
                w.write(escapeCsv(p.playerName) + "," + escapeCsv(p.characterChosen) + "," + p.level + "," + p.xp + "," + p.lives + "," +
                        p.discsUpgrades + "," + escapeCsv(p.currentChapter) + "," + unlocked + "," + ach + "," + p.lastSaved);
                w.newLine();
            }
        }

        private static void writeJson(String path, Player p, boolean append) throws IOException {
            // simple JSON writer (no libs)
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append(jsonKV("playerName", p.playerName)).append(",\n");
            sb.append(jsonKV("characterChosen", p.characterChosen)).append(",\n");
            sb.append(jsonKV("level", p.level)).append(",\n");
            sb.append(jsonKV("xp", p.xp)).append(",\n");
            sb.append(jsonKV("lives", p.lives)).append(",\n");
            sb.append(jsonKV("discsUpgrades", p.discsUpgrades)).append(",\n");
            sb.append(jsonKV("currentChapter", p.currentChapter)).append(",\n");
            sb.append("\"unlockedArenas\":").append(jsonArray(p.unlockedArenas)).append(",\n");
            sb.append("\"achievements\":").append(jsonArray(new ArrayList<>(p.achievements))).append(",\n");
            sb.append(jsonKV("lastSaved", p.lastSaved.toString())).append("\n");
            sb.append("}\n");
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(path),
                    StandardOpenOption.CREATE, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING)) {
                w.write(sb.toString());
            }
        }

        // Load: auto-detect format from extension if you like; here we require format input
        public static Player load(String filepath, SaveFormat fmt) throws IOException {
            Path p = Paths.get(filepath);
            if (!Files.exists(p)) throw new FileNotFoundException("Save file not found: " + filepath);
            switch (fmt) {
                case TEXT: return loadFromText(p);
                case CSV:  return loadFromCsv(p);
                case JSON: return loadFromJson(p);
                default: throw new IllegalArgumentException("Unsupported format");
            }
        }

        private static Player loadFromText(Path path) throws IOException {
            Map<String, String> map = new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(path)) {
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#")) continue;
                    int idx = line.indexOf('=');
                    if (idx > 0) {
                        String k = line.substring(0, idx).trim();
                        String v = line.substring(idx + 1).trim();
                        map.put(k, v);
                    }
                }
            }
            return mapToPlayer(map);
        }

        private static Player loadFromCsv(Path path) throws IOException {
            try (BufferedReader r = Files.newBufferedReader(path)) {
                String header = r.readLine();
                if (header == null) throw new IOException("Empty CSV");
                String lastData = null;
                String line;
                while ((line = r.readLine()) != null) {
                    if (!line.trim().isEmpty()) lastData = line;
                }
                if (lastData == null) throw new IOException("No data rows in CSV");
                String[] parts = splitCsvLine(lastData);
                Map<String, String> map = new HashMap<>();
                map.put("playerName", unescapeCsv(parts[0]));
                map.put("characterChosen", unescapeCsv(parts[1]));
                map.put("level", parts[2]);
                map.put("xp", parts[3]);
                map.put("lives", parts[4]);
                map.put("discsUpgrades", parts[5]);
                map.put("currentChapter", unescapeCsv(parts[6]));
                map.put("unlockedArenas", unescapeCsv(parts[7]));
                map.put("achievements", unescapeCsv(parts[8]));
                map.put("lastSaved", parts.length > 9 ? parts[9] : LocalDate.now().toString());
                return mapToPlayer(map);
            }
        }

        private static Player loadFromJson(Path path) throws IOException {
            String content = new String(Files.readAllBytes(path));
            int start = content.indexOf('{');
            int end = content.indexOf('}', start);
            if (start < 0 || end < 0) throw new IOException("Invalid JSON save");
            String obj = content.substring(start + 1, end);
            Map<String, String> map = new HashMap<>();
            List<String> tokens = splitTopLevel(obj, ',');
            for (String t : tokens) {
                int colon = t.indexOf(':');
                if (colon < 0) continue;
                String k = t.substring(0, colon).trim().replaceAll("^\"|\"$", "");
                String v = t.substring(colon + 1).trim();
                if (v.startsWith("\"") && v.endsWith("\"")) v = v.substring(1, v.length() - 1);
                // arrays handled by joining values with ;
                if (v.startsWith("[") && v.endsWith("]")) {
                    String inner = v.substring(1, v.length() - 1).trim();
                    if (inner.isEmpty()) map.put(k, "");
                    else {
                        List<String> arr = splitTopLevel(inner, ',');
                        List<String> out = new ArrayList<>();
                        for (String it : arr) out.add(it.trim().replaceAll("^\"|\"$", ""));
                        map.put(k, String.join(";", out));
                    }
                } else map.put(k, v);
            }
            return mapToPlayer(map);
        }

        // Helpers: convert key-value map into Player
        private static Player mapToPlayer(Map<String, String> map) {
            String name = map.getOrDefault("playerName", "Unknown");
            String ch = map.getOrDefault("characterChosen", "Default");
            Player pl = new Player(name, ch);
            try { pl.level = Integer.parseInt(map.getOrDefault("level","1")); } catch (Exception ignored) {}
            try { pl.xp = Integer.parseInt(map.getOrDefault("xp","0")); } catch (Exception ignored) {}
            try { pl.lives = Integer.parseInt(map.getOrDefault("lives","3")); } catch (Exception ignored) {}
            try { pl.discsUpgrades = Integer.parseInt(map.getOrDefault("discsUpgrades","0")); } catch (Exception ignored) {}
            pl.currentChapter = map.getOrDefault("currentChapter", pl.currentChapter);
            String arenas = map.getOrDefault("unlockedArenas", "");
            pl.unlockedArenas = arenas.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(arenas.split(";", -1)));
            String ach = map.getOrDefault("achievements", "");
            pl.achievements = ach.isEmpty() ? new LinkedHashSet<>() : new LinkedHashSet<>(Arrays.asList(ach.split(";", -1)));
            try { pl.lastSaved = LocalDate.parse(map.getOrDefault("lastSaved", LocalDate.now().toString())); } catch (Exception ignored) {}
            pl.dirty = false;
            return pl;
        }

        // CSV helpers
        private static String escapeCsv(String s) {
            if (s == null) return "";
            if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
                s = s.replace("\"", "\"\"");
                return "\"" + s + "\"";
            }
            return s;
        }
        private static String unescapeCsv(String s) {
            if (s == null) return "";
            s = s.trim();
            if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
                s = s.substring(1, s.length()-1).replace("\"\"", "\"");
            }
            return s;
        }
        private static String[] splitCsvLine(String line) {
            // very simple CSV splitter that handles quoted cells
            List<String> parts = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            boolean inQuotes = false;
            for (int i=0;i<line.length();i++){
                char c = line.charAt(i);
                if (c == '"' && (i==0 || line.charAt(i-1)!='\\')) inQuotes = !inQuotes;
                if (c==',' && !inQuotes) { parts.add(cur.toString()); cur.setLength(0); }
                else cur.append(c);
            }
            parts.add(cur.toString());
            return parts.toArray(new String[0]);
        }

        private static String jsonKV(String key, Object val) {
            if (val instanceof Number) return "\"" + key + "\":" + val;
            return "\"" + key + "\":\"" + escapeJson(val == null ? "" : val.toString()) + "\"";
        }
        private static String escapeJson(String s) { return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r"); }
        private static String jsonArray(List<String> items) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (String it : items) { if (!first) sb.append(","); sb.append("\"").append(escapeJson(it)).append("\""); first = false; }
            sb.append("]"); return sb.toString();
        }
        private static List<String> splitTopLevel(String s, char sep) {
            List<String> out = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            boolean inQuotes = false;
            for (int i=0;i<s.length();i++){
                char c = s.charAt(i);
                if (c == '"' && (i==0 || s.charAt(i-1)!='\\')) inQuotes = !inQuotes;
                if (c == sep && !inQuotes) { out.add(cur.toString()); cur.setLength(0); }
                else cur.append(c);
            }
            if (cur.length()>0) out.add(cur.toString());
            return out;
        }
    }

    /* -----------------------
       Leaderboard manager (CSV)
       ----------------------- */
    public static class LeaderboardManager {
        private final Path path = Paths.get("leaderboard.csv");

        public LeaderboardManager() throws IOException {
            ensureExists();
        }

        private void ensureExists() throws IOException {
            if (!Files.exists(path)) {
                try (BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
                    w.write("playerName,highestLevel,totalXP,dateOfCompletion"); w.newLine();
                }
            }
        }

        // Read all leaderboard entries
        public List<LeaderboardEntry> readAll() throws IOException {
            List<LeaderboardEntry> out = new ArrayList<>();
            if (!Files.exists(path)) return out;
            try (BufferedReader r = Files.newBufferedReader(path)) {
                String header = r.readLine(); // skip
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = splitCsvLine(line);
                    if (parts.length < 4) continue;
                    String name = unescapeCsv(parts[0]);
                    int lvl = parseIntSafe(parts[1], 1);
                    int total = parseIntSafe(parts[2], 0);
                    String date = parts[3];
                    out.add(new LeaderboardEntry(name, lvl, total, date));
                }
            }
            return out;
        }

        // Add or update using Player: keep best stats (highestLevel, highest totalXP)
        public void addOrUpdateFromPlayer(Player p) throws IOException {
            List<LeaderboardEntry> list = readAll();
            boolean found = false;
            for (LeaderboardEntry e : list) {
                if (e.playerName.equalsIgnoreCase(p.playerName)) {
                    found = true;
                    if (p.level > e.highestLevel) e.highestLevel = p.level;
                    if (p.totalScore() > e.totalScore) e.totalScore = p.totalScore();
                    e.dateOfCompletion = LocalDate.now().toString();
                    break;
                }
            }
            if (!found) {
                list.add(new LeaderboardEntry(p.playerName, p.level, p.totalScore(), LocalDate.now().toString()));
            }
            // sort by totalScore desc then level desc
            list.sort((a,b) -> {
                int c = Integer.compare(b.totalScore, a.totalScore); if (c!=0) return c;
                return Integer.compare(b.highestLevel, a.highestLevel);
            });
            // write back (keep all, but UI shows top 10)
            try (BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                w.write("playerName,highestLevel,totalXP,dateOfCompletion"); w.newLine();
                for (LeaderboardEntry e : list) {
                    w.write(escapeCsv(e.playerName) + "," + e.highestLevel + "," + e.totalScore + "," + e.dateOfCompletion);
                    w.newLine();
                }
            }
        }

        // Show top N
        public void printTopN(int n) throws IOException {
            List<LeaderboardEntry> list = readAll();
            System.out.println("=== Leaderboard Top " + n + " ===");
            int rank = 1;
            for (LeaderboardEntry e : list) {
                if (rank > n) break;
                System.out.printf("%2d. %s — Level %d — Score %d — %s%n", rank++, e.playerName, e.highestLevel, e.totalScore, e.dateOfCompletion);
            }
            if (list.isEmpty()) System.out.println("(no entries)");
        }

        // small helpers & nested class
        private static class LeaderboardEntry {
            String playerName; int highestLevel; int totalScore; String dateOfCompletion;
            LeaderboardEntry(String pn,int hl,int ts,String d){ playerName=pn; highestLevel=hl; totalScore=ts; dateOfCompletion=d; }
        }
        private static int parseIntSafe(String s,int fallback){ try{return Integer.parseInt(s);}catch(Exception ex){return fallback;} }
    }

    /* -----------------------
       Simple CLI demo & notes for integration
       ----------------------- */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Player current = null;
        SaveFormat lastFormat = SaveFormat.CSV; // default format for saves
        LeaderboardManager lb;
        try { lb = new LeaderboardManager(); } catch (IOException e) { System.err.println("Failed to init leaderboard: "+e.getMessage()); return; }

        while (true) {
            System.out.println("\n--- Tron Save / Leaderboard Demo ---");
            System.out.println("1) Create player");
            System.out.println("2) Load player from file");
            System.out.println("3) Save current player");
            System.out.println("4) Simulate progress (gain xp/achievements)");
            System.out.println("5) View Top 10 Leaderboard");
            System.out.println("6) Quit");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            try {
                if (c.equals("1")) {
                    System.out.print("Name: "); String name = sc.nextLine().trim();
                    System.out.print("Character: "); String ch = sc.nextLine().trim();
                    current = new Player(name.isEmpty()? "Player":name, ch.isEmpty()? "Tron":ch);
                    System.out.println("Created: " + current);
                } else if (c.equals("2")) {
                    System.out.println("Format: 1) TEXT 2) CSV 3) JSON"); String f = sc.nextLine().trim();
                    SaveFormat fmt = f.equals("1")? SaveFormat.TEXT : (f.equals("3")? SaveFormat.JSON : SaveFormat.CSV);
                    System.out.print("Enter filename (or playerName to load from saves/): ");
                    String fp = sc.nextLine().trim();
                    String path = fp.contains(File.separator) || fp.contains(".") ? fp : "saves"+File.separator+fp + (fmt==SaveFormat.JSON? ".json":(fmt==SaveFormat.CSV? ".csv":".txt"));
                    current = SaveManager.load(path, fmt);
                    System.out.println("Loaded: " + current);
                    lastFormat = fmt;
                } else if (c.equals("3")) {
                    if (current == null) { System.out.println("No current player."); continue; }
                    System.out.println("Save format: 1) TEXT 2) CSV 3) JSON"); String f2 = sc.nextLine().trim();
                    SaveFormat fmt = f2.equals("1")? SaveFormat.TEXT : (f2.equals("3")? SaveFormat.JSON : SaveFormat.CSV);
                    System.out.print("Append to existing file? (y/N): "); boolean append = sc.nextLine().trim().toLowerCase().startsWith("y");
                    SaveManager.save(current, fmt, append);
                    // update leaderboard immediately
                    lb.addOrUpdateFromPlayer(current);
                    System.out.println("Saved -> " + SaveManager.saveFilePath(current, fmt));
                    lastFormat = fmt;
                } else if (c.equals("4")) {
                    if (current == null) { System.out.println("Create or load a player first."); continue; }
                    // simulate progress
                    current.xp += 250;
                    current.level = Math.max(current.level, 1 + current.xp / 100);
                    current.achievements.add("DemoAchievement");
                    current.unlockedArenas.add("Arena I");
                    current.dirty = true;
                    System.out.println("After progress: " + current);
                } else if (c.equals("5")) {
                    lb.printTopN(10);
                } else if (c.equals("6")) {
                    if (current != null && current.dirty) {
                        System.out.print("You have unsaved progress. Save before exit? (Y/n): ");
                        String yn = sc.nextLine().trim().toLowerCase();
                        if (!yn.equals("n")) {
                            SaveManager.save(current, lastFormat, false);
                            lb.addOrUpdateFromPlayer(current);
                            System.out.println("Saved before exit.");
                        }
                    }
                    System.out.println("Bye.");
                    break;
                } else {
                    System.out.println("Unknown choice.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }
        sc.close();
    }

    /* -----------------------
       Small static helpers used internally
       ----------------------- */
    private static String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"","\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
    private static String unescapeCsv(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length()-1).replace("\"\"", "\"");
        }
        return s;
    }
    private static String[] splitCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i=0;i<line.length();i++){
            char c = line.charAt(i);
            if (c == '"' && (i==0 || line.charAt(i-1)!='\\')) inQuotes = !inQuotes;
            if (c==',' && !inQuotes) { parts.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
