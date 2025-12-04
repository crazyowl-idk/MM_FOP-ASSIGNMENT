package entities;


public abstract class Character {
   protected String name;
    protected String colour;
    protected double speed;
    protected double handling;
    protected int lives;
    protected int discsOwned;

    public Character(String name, String colour, double speed, double handling, int lives, int discsOwned) {
        this.name = name;
        this.colour = colour;
        this.speed = speed;
        this.handling = handling;
        this.lives = lives;
        this.discsOwned = discsOwned;
    }


    public abstract void move(char direction);
    public abstract void throwDisc();
    
    public void loseLife(double damage) {
        this.lives -= damage;
        if (this.lives <= 0) {
            System.out.println(this.name + " DIE!");
        }
    }

     public String getName() {
        return name;
    }

    // Optionally, a setter if you want to change it later
    public void setName(String name) {
        this.name = name;
    }


    protected int x;
    protected int y;

     public int getX() { return x; }
     public int getY() { return y; }
}



