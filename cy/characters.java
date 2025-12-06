package com.mycompany.tron_game.main;

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


    
    //leveling system
    public void levelUp(){
        this.level++;
        System.out.println(this.name + " leveled up to " + this.level + "!");
        

    }

    //Story Trigger Method
    public void triggerStory() {
        // Use the instance level to look up the event
        StoryEvent storyEvent = STORY_MAP.get(this.level);

        // Check if an event exists for the current level
        if (storyEvent != null) {
            // Using String concatenation instead of template literals
            System.out.println("\n--- STORY TRIGGER: " + storyEvent.title + " (Level " + this.level + ") ---");
            
            // 1. Check for Cutscene Requirement
            if (storyEvent.isCutscene) {
                // Call the cutscene method
                this.playCutscene(storyEvent);
            } else {
                // 2. Default to simple text/dialogue
                System.out.println("[" + storyEvent.type + "] " + storyEvent.content);
            }
        }
    }

    //Play Cutscene
    // Note: The parameter must be type-defined as StoryEvent, not 'story'
    public void playCutscene(StoryEvent storyEvent) {
        System.out.println("* Playing cutscene: " + storyEvent.title + " *");
        // Placeholder for cutscene logic
        System.out.println("[Cutscene Content]: " + storyEvent.content);
    }

    // --- Demonstration (main method) ---
    public static void main(String[] args) {
        Character player = new Character(1);
        player.triggerStory(); // Triggers Lvl 1 Dialogue

        // Simulate leveling up to a milestone
        player.level = 10;
        player.triggerStory(); // Triggers Lvl 10 Cutscene
    }


    //Special unlocks
    public void specialUnlock(){
        //every 10 levels = + 1 life
        if(this.level % 10 == 0){
            this.lives += 1;
            System.out.println(this.name + " unlocked an extra life! Total lives: " + this.lives);
        }

        //every 15 levels = + 1 disc
        if(this.level % 15 == 0){
            this.discs += 1;
            System.out.println(this.name + " unlocked an extra disc! Total discs: " + this.discs);
        }
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

    

}