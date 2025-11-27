public class Characters {
    //instance variables
    protected String name;
    protected String color;
    protected String speed;
    protected String handling;
    protected String description;
    protected int lives;
    protected int discs;
    protected int experiencePoints;
    protected int level;

    //constructor
    public Characters(String name, String color, String speed, String handling, String description, int lives, int discs, int experiencePoints) {
        this.name = name;
        this.color = color;
        this.speed = speed;
        this.handling = handling;
        this.description = description;
        this.lives = lives; //default lives
        this.discs = discs; //default discs
        this.experiencePoints = experiencePoints; //default experience points
        this.level = 1; //start at level 1
    }

    //leveling system
    public void levelUp(){
        this.level++;
        System.out.println(this.name + " leveled up to " + this.level + "!");
    }

    //not sure yet
    public void loseLife(){
        if(this.lives > 0){
            this.lives--;
            System.out.println(this.name + " lost a life. Lives remaining: " + this.lives);
        } else {
            System.out.println(this.name + " has no lives left!");
        }
    }

    public void gainExperience(int points){
        this.experiencePoints += points;
        System.out.println(this.name + " gained " + points + " experience points.");
    }

    //helper method to display character info
    public void displayStats(){
        System.out.println("===== " + name + " =====");
        System.out.println("Color: " + color);
        System.out.println("Speed: " + speed);
        System.out.println("Handling: " + handling);
        System.out.println("Description: " + description);
        System.out.println("Lives: " + lives);
        System.out.println("Discs: " + discs);
        System.out.println("Experience Points: " + experiencePoints);
        System.out.println("Level: " + level);
        System.out.println();
    }

}