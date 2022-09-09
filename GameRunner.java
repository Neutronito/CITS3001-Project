import java.util.Scanner;
import java.util.Random;

public class GameRunner {

    private Game gameInstance;
    private final String[][] redMessages =  {   {"1 version 1", "1 version 2"},
                                                {"2 version 1", "2 version 2"},
                                                {"3 version 1", "3 version 2"},
                                                {"4 version 1", "4 version 2"},
                                                {"5 version 1", "5 version 2"},
                                                {"6 version 1", "6 version 2"}   };
    private final String[][] blueMessages = {   {"1 version 1", "1 version 2"},
                                                {"2 version 1", "2 version 2"},
                                                {"3 version 1", "3 version 2"},
                                                {"4 version 1", "4 version 2"},
                                                {"5 version 1", "5 version 2"},
                                                {"6 version 1", "6 version 2"}   };

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
        boolean triggerGameEnd = false;         //True when game end is triggered, false when game is running

        while (!triggerGameEnd) {
            //Execute the red turn
            System.out.println("\nRED AGENT'S TURN");
            int redPotency = getMessagePotency("red");
            gameInstance.executeRedTurn(redPotency);

            //Execute the blue turn
            System.out.println("\nBLUE AGENT'S TURN");
            int userBlueChoice = getBlueOption();
            int bluePotency = getMessagePotency("blue");
            if (userBlueChoice == 1) {
                gameInstance.executeBlueTurn1(bluePotency, false);
            } else if (userBlueChoice == 2) {
                gameInstance.executeBlueTurn2(bluePotency);
            }
            
            //Execute the green turn
            System.out.println("\nGREEN TEAM");
            gameInstance.executeGreenTurn();

            //Print the metrics now
            gameInstance.printGreenAgents();
            gameInstance.printGreenStatistics();
            gameInstance.printBlueEnergyLevel();
            
            triggerGameEnd = gameInstance.triggerGameEnd();  //NOT DONE - triggerGameEnd becomes true when blue energy level is depleted 
        }
        scanner.close();
        if (gameInstance.blueWins()) {
            System.out.println("Blue Agent wins!");
        } else {
            System.out.println("Red Agent wins!");
        }
        return 0;
    }

    /**
     * Getter for the blue turn option via user input.
     * @return The blue turn option.
     */
    public int getBlueOption() {
        Scanner scanner = new Scanner(System.in);
        String userChoice = "blank";
        while (!correctOptionInputFormat(userChoice)) {
            System.out.println("Choose your action, 1 or 2.");
            System.out.println("Option 1 : Interact with Green team.");
            System.out.println("Option 2 : Let a Grey agent in the Green network.");
            userChoice = scanner.nextLine();
        }
        int blueOption = Integer.parseInt(userChoice);
        return blueOption;
    }

    /**
     * Getter for the message potency via user input.
     * @param team Blue or Red.
     * @return The message potency level.
     */
    public int getMessagePotency(String team) {
        
        Random rand = new Random();
        Scanner scanner = new Scanner(System.in);
        String userInput = "blank";
        String level1 = "", level2 = "", level3 = "", level4 = "", level5 = "", level6 = "";

        //Get red team messages
        if (team == "red") {
            level1 = redMessages[0][rand.nextInt(2)];
            level2 = redMessages[1][rand.nextInt(2)];
            level3 = redMessages[2][rand.nextInt(2)];
            level4 = redMessages[3][rand.nextInt(2)];
            level5 = redMessages[4][rand.nextInt(2)];
            level6 = redMessages[5][rand.nextInt(2)];
        } 
        //Get blue team messages
        else if (team == "blue") {
            level1 = blueMessages[0][rand.nextInt(2)];
            level2 = blueMessages[1][rand.nextInt(2)];
            level3 = blueMessages[2][rand.nextInt(2)];
            level4 = blueMessages[3][rand.nextInt(2)];
            level5 = blueMessages[4][rand.nextInt(2)];
            level6 = blueMessages[5][rand.nextInt(2)];
        }
          
        while (!correctMsgInputFormat(userInput)) {
            System.out.printf("\nChoose the %s level of potency, from 1 to 6.\n", team);
            System.out.printf("%s\n%s\n%s\n%s\n%s\n%s\n", level1, level2, level3, level4, level5, level6);
            //Wait for an input from the user
            userInput = scanner.nextLine();
        }

        int messagePotency = Integer.parseInt(userInput);
        return messagePotency;
    }

    /**
     * Returns true if the user input message potency is valid (between 1 and 6), false otherwise.
     * @param userInput The user input used for red or blue message potency.
     * @return True if the user input message potency is valid (between 1 and 6), false otherwise.
     */
    public boolean correctMsgInputFormat(String userInput) {
        //No input yet
        if (userInput == "blank") {
            return false;
        }
        //Input is not between 1 and 6
        if (!isNumeric(userInput) || userInput.length() != 1 || Integer.parseInt(userInput) < 1 || Integer.parseInt(userInput) > 6) {
            System.out.println("\nError, please enter a digit from 1 to 6, inclusively");
            return false;
        }
        return true;
    }

    /**
     * Returns true if the user input blue turn option is valid (between 1 and 6), false otherwise.
     * @param userInput The user input used for blue turn option.
     * @return True if the user input blue turn option is valid (between 1 and 6), false otherwise.
     */
    public boolean correctOptionInputFormat(String userChoice) {
        //No input yet
        if (userChoice == "blank") {
            return false;
        }
        //Length of input is not 1
        if (!isNumeric(userChoice) || userChoice.length() != 1 || Integer.parseInt(userChoice) < 1 || Integer.parseInt(userChoice) > 2) {
            System.out.println("\nError, please enter '1' or '2'.");
            return false;
        }
        //Check for escape integer
        if (Integer.parseInt(userChoice) == 0) {
            return false;
        }
        return true;

    }

    /**
     * Checks whether the given string is numeric or not.
     * @param str The given string to be checked.
     * @return True if the the given string is numeric.
     */
    public static boolean isNumeric(String str) { 
        try {  
            Integer.parseInt(str);  
          return true;
        } catch(NumberFormatException e){  
          return false;  
        }  
      }

    public static void main(String[] args) {
        double[] uncertaintyInterval = {-1.0, 0.4};
        GameRunner curRunner = new GameRunner(40, 0.4, 10, 40.0, uncertaintyInterval, 60.0);
        curRunner.playGame();
        System.out.println("The game is over");
    }
}