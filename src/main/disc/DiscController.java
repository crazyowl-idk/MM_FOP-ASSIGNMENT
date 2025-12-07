package disc;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator; 
import java.util.Map;
import java.util.HashMap;
import java.awt.Color; 


class Vector2 {
    public float x, y;

    public Vector2(float x, float y) { this.x = x; this.y = y; }

    public Vector2 add(Vector2 other) { return new Vector2(this.x + other.x, this.y + other.y); }
    public Vector2 scale(float scalar) { return new Vector2(this.x * scalar, this.y * scalar); }
    public Vector2 normalize() {
        float len = (float) Math.sqrt(x*x + y*y);
        return len != 0 ? new Vector2(x/len, y/len) : new Vector2(0,0);
    }
}


class Disc {
    public Vector2 position;
    public Vector2 velocity;
    public String ownerId;
    public boolean isTraveling = true; 
    public Color color;
    
    // Stopping Logic
    public Vector2 startPosition; 
    public float maxDistance;

    public Disc(Vector2 pos, Vector2 vel, String ownerId, Color color, float maxDist) {
        this.position = pos;
        this.velocity = vel;
        this.ownerId = ownerId;
        this.color = color;
        this.startPosition = new Vector2(pos.x, pos.y);
        this.maxDistance = maxDist;
    }
}


class DiscController {
    private List<Disc> activeDiscs = new ArrayList<>();
    
    // Constants
    public static final float GRID_SIZE = 150.0f; 
    private static final float DISC_SPEED = 500.0f;
    private static final float LEFT=0, RIGHT=1000, TOP=0, BOTTOM=800, RADIUS=10;

    // --- Core Methods ---

    public void update(float deltaTime) {
        Iterator<Disc> it = activeDiscs.iterator();
        while (it.hasNext()) {
            Disc disc = it.next();
            
            if (disc.isTraveling) {
                // Move
                disc.position = disc.position.add(disc.velocity.scale(deltaTime));
                
                // Check Distance (Stop logic)
                float dist = (float) Math.sqrt(Math.pow(disc.position.x - disc.startPosition.x, 2) + 
                                             Math.pow(disc.position.y - disc.startPosition.y, 2));
                
                if (dist >= disc.maxDistance) {
                    disc.isTraveling = false; // Stop!
                    disc.velocity = new Vector2(0,0);
                } else {
                    checkWallCollision(disc);
                }
            }
        }
    }

    // Factory method called by the AbilityManager
    public void spawnDiscs(String ownerId, Vector2 startPos, Vector2 direction, Color color, int count, float maxDist) {
        float spreadAngle = 30.0f;
        float startAngle = (float) Math.toDegrees(Math.atan2(direction.y, direction.x)) - ((count-1)*spreadAngle)/2;

        for(int i=0; i<count; i++) {
            double rad = Math.toRadians(startAngle + (i*spreadAngle));
            Vector2 vel = new Vector2((float)Math.cos(rad), (float)Math.sin(rad)).scale(DISC_SPEED);
            activeDiscs.add(new Disc(new Vector2(startPos.x, startPos.y), vel, ownerId, color, maxDist));
        }
    }

    public boolean tryRetrieveDisc(String ownerId, Vector2 pos, float radius) {
        Iterator<Disc> it = activeDiscs.iterator();
        while(it.hasNext()){
            Disc disc = it.next();
            if(!disc.isTraveling && disc.ownerId.equals(ownerId)) {
                float dist = (float) Math.sqrt(Math.pow(pos.x - disc.position.x, 2) + Math.pow(pos.y - disc.position.y, 2));
                if(dist < radius + RADIUS) {
                    it.remove(); // Pick up
                    return true;
                }
            }
        }
        return false;
    }

    public List<Disc> getDiscs() { return activeDiscs; }

    private void checkWallCollision(Disc d) {
        if (d.position.x < LEFT+RADIUS) { d.velocity.x *= -1; d.position.x = LEFT+RADIUS; }
        if (d.position.x > RIGHT-RADIUS) { d.velocity.x *= -1; d.position.x = RIGHT-RADIUS; }
        if (d.position.y < TOP+RADIUS) { d.velocity.y *= -1; d.position.y = TOP+RADIUS; }
        if (d.position.y > BOTTOM-RADIUS) { d.velocity.y *= -1; d.position.y = BOTTOM-RADIUS; }
    }
}


class DiscAbilityManager {
    private DiscController controller;
    
    // Cooldown Tracker: Maps "OwnerID" -> "Last Throw Time (ms)"
    private Map<String, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 5000; // 5 Seconds

    public DiscAbilityManager(DiscController controller) {
        this.controller = controller;
    }

    public boolean attemptThrow(String ownerId, String charName, int level, Vector2 pos, Vector2 facingDir) {
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(ownerId)) {
            if (now - cooldowns.get(ownerId) < COOLDOWN_MS) {
                System.out.println(ownerId + " is on cooldown!");
                return false; // Failed to throw
            }
        }

        int discCount = 1;
        switch (charName.toLowerCase()) {
            //need to wait for the character names and level to set
            case "tron": discCount = 1; break;
            case "kevin": discCount = 3; break;
            default: discCount = 1; break; 
        }

        float gridUnits = (level > 5) ? 5.0f : 3.0f; //need to change according to our levelling (not in our story.txt yet)
        float maxDistance = gridUnits * DiscController.GRID_SIZE;

        Color color; // need to map ownerId to Color (if not done elsewhere)
        switch (ownerId) {
            case "Tron": color = Color.BLUE; break;
            case "Kevin": color = Color.WHITE; break;
            case "Clu": color = Color.ORANGE; break;
            case "Rinzler": color = Color.RED; break;
            case "Sark": color = Color.YELLOW; break;
            case "Koura": color = Color.GREEN; break;
            default: color = Color.BLACK; break;
        }

        controller.spawnDiscs(ownerId, pos, facingDir, color, discCount, maxDistance);

        cooldowns.put(ownerId, now);
        System.out.println(ownerId + " threw " + discCount + " discs (Dist: " + gridUnits + " grids)");
        return true;
    }
}