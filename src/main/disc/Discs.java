public class Discs {
    //instance variables
    protected String discName;
    protected String discType;
    protected String speed;
    protected String glide;
    protected String turn;
    protected String fade;
    protected String description;

    //constructor
    public Discs(String discName, String discType, String speed, String glide, String turn, String fade, String description) {
        this.discName = discName;
        this.discType = discType;
        this.speed = speed;
        this.glide = glide;
        this.turn = turn;
        this.fade = fade;
        this.description = description;
    }

    //helper method to display disc info
    public void displayDiscInfo(){
        System.out.println("===== " + discName + " =====");
        System.out.println("Type: " + discType);
        System.out.println("Speed: " + speed);
        System.out.println("Glide: " + glide);
        System.out.println("Turn: " + turn);
        System.out.println("Fade: " + fade);
        System.out.println("Description: " + description);
        System.out.println();
    }
}
