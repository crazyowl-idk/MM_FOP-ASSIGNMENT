class Tron extends Characters {
    public Tron(String name, String color, String speed, String handling, String description, int lives, int discs, int experiencePoints){
        //pass data up to parent class
        super(name, color, speed, handling, description, lives, discs, experiencePoints);
    }

    //Improved speed or turn rate.
    //Gains more speed and stability per level.
    //stability zai na li ??? 
    public void specialization(){
        this.speed += 0.5;
        
    }
}