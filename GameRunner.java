import java.util.Scanner;

public class GameRunner {

    private Game gameInstance;

    /**
     * This creates the game instance, it takes a clone of the Game parameters to feed into the Game class it will create.
     * @param greenAgentCount The number of green nodes to exist in the nework
     * @param probabilityOfConnection The probability of a connection between two green nodes
     * @param greyCount The number of grey nodes
     * @param greyEvilProportion The proportion of grey nodes on the red team, as a percentage
     * @param greenUncertaintyInterval The uncertainty interval as an array, the first index is lower bound and the second is upperbound
     * @param greenVotePercent The proportion of green nodes who wish to vote (at the start of the game) as a percentage
     */
    public GameRunner(int greenAgentCount, double probabilityOfConnection, int greyCount, double greyEvilProportion, double[] greenUncertaintyInterval, double greenVotePercent) {
        //There is no error checking because the Game constructor deals with this.
        gameInstance = new Game(greenAgentCount, probabilityOfConnection, greyCount, greyEvilProportion, greenUncertaintyInterval, greenVotePercent);
    }

    /**
     * Returns the game instance. It returns a shallow copy so TAKE CARE.
     * @return A shallow copy of the game instance, of the type Game class
     */
    public Game getGameInstance() {
        return gameInstance;
    }

    /**
     * A blocking function that executes the game, waiting for human inputs for the red turn. The red turn is executed before the green turn
     * Prints the game statistics after each turn.
     * The game ends once the player passes in the end game command.
     * @return 0 on game end, anything else denotes an error.
     */
    public int playGame() {
        
        gameInstance.printGreenAgents();
        gameInstance.printGreenStatistics();

        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n \n \n Choose the red level of potency, from 1 to 6");
            //Wait for an input from the user
            String userInput = scanner.nextLine();
            
            //Error check and parse the input
            if (userInput.length() != 1) {
                System.out.println("Error, please enter a single digit.");
                continue;
            }

            int redPotency = Integer.parseInt(userInput);

            //Check for escape integer
            if (redPotency == 0) {
                break;
            }

            if (redPotency < 1 || redPotency > 6) {
                System.out.println("Error, please enter a digit from 1 to 6, inclusively");
                continue;
            }

            //Execute the red turn
            gameInstance.executeRedTurn(redPotency);

            //Execute the green turn
            gameInstance.executeGreenTurn();

            //Print the metrics now
            gameInstance.printGreenAgents();
            gameInstance.printGreenStatistics();
        }
        scanner.close();
        return 0;
    }

    public static void main(String[] args) {
        double[] uncertaintyInterval = {-1.0, 0.4};
        GameRunner curRunner = new GameRunner(40, 0.4, 10, 40.0, uncertaintyInterval, 60.0);
        curRunner.playGame();
        System.out.println("The game is over");
    }
}