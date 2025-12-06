class Kevin extends Characters {
    public Kevin(String name, String color, String speed, String handling, String description, int lives, int discs, int experiencePoints){
        //pass data up to parent class
        super(name, color, speed, handling, description, lives, discs, experiencePoints);
    }

    //Improved speed or turn rate.
    //Gains more handling precision and discsOwned per level.
    public void specialization(){
        this.handling += 0.5;
        this.discs += 1;
    }
}
