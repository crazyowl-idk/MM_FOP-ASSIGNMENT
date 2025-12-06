public class Main {
    public static void main(String[] args) {
        // Setup: Closed Arena, Preset 1
        // Change 'false' to 'true' for Open Arena
        // Change '1' to '0' for Random Arena
        Arena gameArena = new Arena(false, 1); 

        // Initial positions (start them away from the walls for closed arena)
        Player player1 = new Player(10, 10, '1');
        Player player2 = new Player(30, 30, '2');
        
        // Simulating the game ticks and WASD input
        char[] p1Moves = {'D', 'D', 'D', 'D', 'D', 'S', 'S', 'S', 'S', 'A', 'A', 'A', 'W', 'W', 'W', 'W'};
        char[] p2Moves = {'A', 'A', 'A', 'A', 'A', 'W', 'W', 'W', 'W', 'D', 'D', 'D', 'S', 'S', 'S', 'S'};
        
        int maxTicks = Math.min(p1Moves.length, p2Moves.length);
        int tick = 0;
        boolean p1Alive = true;
        boolean p2Alive = true;

        System.out.println("--- Starting Arena Game Simulation ---");
        
        while (tick < maxTicks && (p1Alive || p2Alive)) {
            System.out.println("\n*** TICK " + (tick + 1) + " ***");
            
            // Player 1 Move
            if (p1Alive) {
                p1Alive = gameArena.movePlayer(player1, p1Moves[tick]);
            }
            
            // Player 2 Move (moves after P1, which is important for the jetwall logic)
            if (p2Alive) {
                p2Alive = gameArena.movePlayer(player2, p2Moves[tick]);
            }

            // Display
            gameArena.displayArena(player1, player2);

            if (player1.getLives() <= 0) p1Alive = false;
            if (player2.getLives() <= 0) p2Alive = false;
            
            tick++;

            if (!p1Alive && !p2Alive) {
                System.out.println("\n*** GAME OVER: Both players are defeated! ***");
            } else if (!p1Alive) {
                System.out.println("\n*** GAME OVER: Player 2 Wins! ***");
            } else if (!p2Alive) {
                System.out.println("\n*** GAME OVER: Player 1 Wins! ***");
            }
        }
        
        if (p1Alive && p2Alive) {
            System.out.println("\n*** Simulation Ended. Both players survived the moves. ***");
        }
    }
}
