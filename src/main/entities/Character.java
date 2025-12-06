import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Character {
    public static void main(String[] args) {
        try {
            File file = new File("characters.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] attributes = line.split(",");

                if (attributes.length == 8) {
                    String name = attributes[0];
                    String color = attributes[1];
                    String speed = attributes[2];
                    String handling = attributes[3];
                    String description = attributes[4];
                    int lives = Integer.parseInt(attributes[5]);
                    int discs = Integer.parseInt(attributes[6]);
                    int experiencePoints = Integer.parseInt(attributes[7]);
                    
                    if (name.equalsIgnoreCase("Tron")) {
                        Tron tron = new Tron(name, color, speed, handling, description, lives, discs, experiencePoints);
                        tron.displayStats();
                        tron.levelUp();
                    } else if (name.equalsIgnoreCase("Kevin")) {
                        Kevin kevin = new Kevin(name, color, speed, handling, description, lives, discs, experiencePoints);
                        kevin.displayStats();
                        kevin.levelUp();
                    } 
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: characters.txt file not found.");
            e.printStackTrace();
        }
    }
}