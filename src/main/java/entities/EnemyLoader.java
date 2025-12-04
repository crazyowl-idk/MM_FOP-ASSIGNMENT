package entities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnemyLoader {
    
    public static List<Enemy> loadEnemies(String filename) {
        List<Enemy> enemies = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            
            br.readLine(); 

            while ((line = br.readLine()) != null) { 
                
                if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                    continue;
                }
                
                String[] parts = line.split(",");
                
                if (parts.length != 9) {
                    System.err.println("Skipping malformed enemy data line (expected 9 parts): " + line);
                    continue;
                }

                String name = parts[0].trim();
                String colour = parts[1].trim();
                String difficulty = parts[2].trim();

                int xp = Integer.parseInt(parts[3].trim());
                double speed = Double.parseDouble(parts[4].trim());
                double handling = Double.parseDouble(parts[5].trim());
                double aggression = Double.parseDouble(parts[6].trim());
                int lives = Integer.parseInt(parts[7].trim());
                int discsOwned = Integer.parseInt(parts[8].trim());


                enemies.add(EnemyFactory.createEnemy(
                    name, colour, difficulty, xp, speed, handling, aggression, lives, discsOwned
                ));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: The enemy file '" + filename + "' was not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading enemy file: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number in file: " + e.getMessage());
            e.printStackTrace();
        }

        return enemies;
    }
}

