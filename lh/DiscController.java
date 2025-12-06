import java.util.ArrayList;
import java.util.List;
import java.util.Iterator; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple 2D Vector class to handle position and velocity.
 */
class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** Returns a new Vector2 that is the sum of this vector and another. */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    /** Returns a new Vector2 scaled by a scalar value. */
    public Vector2 scale(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    /** Normalizes the vector (makes its length 1) for pure direction. */
    public Vector2 normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        if (length != 0) {
            return new Vector2(x / length, y / length);
        }
        return new Vector2(0, 0);
    }
}

/**
 * Represents a Disc thrown by a player or bot.
 */
public class DiscController {

    private static final float DISC_SPEED = 500.0f; // Pixels per second
    private static final float MAX_LIFETIME = 5.0f; // Seconds
    
    public static final float DISC_RADIUS = 10f; // Disc size for collision

    private List<Disc> activeDiscs;
    private List<Player> gamePlayers; // List of all targets (players/bots)

    public DiscController(List<Player> players) {
        this.activeDiscs = new ArrayList<>();
        this.gamePlayers = players;
    }

    /**
     * Getter method to expose the list of active discs to the drawing component.
     * This is how the View gets the Model's data.
     * @return The list of currently flying Disc objects.
     */
    public List<Disc> getActiveDiscs() {
        return activeDiscs;
    }

    /**
     * Getter method to expose the list of players to the drawing component.
     * @return The list of players in the game.
     */
    public List<Player> getGamePlayers() {
        return gamePlayers;
    }

    /**
     * Creates a new Disc and launches it into the game world.
     * This handles the "Throwing Logic" (A) from the plan.
     * @param owner The Player/Bot who threw the disc.
     * @param startPos The initial position.
     * @param direction A raw Vector2 input indicating the throw direction.
     */
    public void throwDisc(Player owner, Vector2 startPos, Vector2 direction) {
        // 1. Calculate the final launch velocity (normalize direction and multiply by speed)
        Vector2 normalizedDirection = direction.normalize();
        Vector2 initialVelocity = normalizedDirection.scale(DISC_SPEED);

        // 2. Create the Disc object (Initialization)
        Disc newDisc = new Disc(startPos, initialVelocity, owner.id, MAX_LIFETIME, owner.color);

        // 3. Activation
        activeDiscs.add(newDisc);
        System.out.println("Disc thrown by " + owner.id + " at " + startPos.x + ", " + startPos.y);
    }

    /**
     * The core game loop update for all discs.
     * This handles the "Continuous Update Logic" (B) from the plan.
     * @param deltaTime The time elapsed since the last frame (in seconds).
     */
    public void update(float deltaTime) {
        // Use an Iterator for safe removal while looping (common Java pattern)
        Iterator<Disc> iterator = activeDiscs.iterator();

        while (iterator.hasNext()) {
            Disc disc = iterator.next();

            // 1. Check Lifetime (B.3)
            disc.lifetime -= deltaTime;
            if (disc.lifetime <= 0) {
                System.out.println("Disc lifetime expired. Despawned.");
                iterator.remove(); // Safely remove the disc from the list
                continue; // Skip the rest of the logic for this disc
            }

            // 2. Move Disc (B.2)
            Vector2 displacement = disc.velocity.scale(deltaTime);
            disc.position = disc.position.add(displacement);
            
            // 3. Check Wall Collision (B.4 / C)
            checkWallCollision(disc);

            // 4. Check Player Collision (B.4 / D)
            if (checkPlayerCollision(disc)) {
                iterator.remove(); // Disc hit a player and must be removed
            }
        }
    }

    /**
     * Handles disc interaction with the game boundaries, causing a bounce.
     * This implements the "Wall Collision Logic" (C).
     */
    private void checkWallCollision(Disc disc) {
        boolean bounced = false;

        // Horizontal boundaries (Left and Right)
        if (disc.position.x - DISC_RADIUS < LEFT_BOUNDARY) {
            disc.velocity.x *= -1; // Reverse horizontal direction
            // Self-Correction: Nudge the disc back into the boundary
            disc.position.x = LEFT_BOUNDARY + DISC_RADIUS;
            bounced = true;
        } else if (disc.position.x + DISC_RADIUS > RIGHT_BOUNDARY) {
            disc.velocity.x *= -1; // Reverse horizontal direction
            disc.position.x = RIGHT_BOUNDARY - DISC_RADIUS;
            bounced = true;
        }

        // Vertical boundaries (Top and Bottom)
        if (disc.position.y - DISC_RADIUS < TOP_BOUNDARY) {
            disc.velocity.y *= -1; // Reverse vertical direction
            disc.position.y = TOP_BOUNDARY + DISC_RADIUS;
            bounced = true;
        } else if (disc.position.y + DISC_RADIUS > BOTTOM_BOUNDARY) {
            disc.velocity.y *= -1; // Reverse vertical direction
            disc.position.y = BOTTOM_BOUNDARY - DISC_RADIUS;
            bounced = true;
        }

        if (bounced) {
             // System.out.println("Disc bounced at: " + disc.position.x + ", " + disc.position.y);
        }
    }

    /**
     * Checks if the disc has hit any player or bot.
     * This implements the "Player Collision Logic" (D).
     * @return true if the disc hit a valid target and should be removed, false otherwise.
     */
    private boolean checkPlayerCollision(Disc disc) {
        for (Player target : gamePlayers) {
            // D.1 Hit Check: Simplified Circular Collision (Distance formula)
            float dx = disc.position.x - target.position.x;
            float dy = disc.position.y - target.position.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float minDistance = DISC_RADIUS + target.hitboxRadius;

            if (distance < minDistance) {
                // D.2 Owner Check: Ensure the disc didn't hit the player who threw it
                if (!target.id.equals(disc.ownerId)) {
                    // Apply damage
                    target.takeDamage(disc.damageAmount);
                    System.out.println("Disc hit target " + target.id + "!");
                    return true; // Collision occurred and the disc should be removed
                }
            }
        }
        return false; // No collision or only hit the owner
    }
    

}