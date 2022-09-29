import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

public class GameRunner {

    private Game gameInstance;              // Game
    private RedAI redAI;                    // Red AI
    private BlueAI blueAI;                  // Blue AI
    private boolean playAsRedAI;            // True if Red AI is playing, false otherwise
    private boolean playAsBlueAI;           // True if Blue AI is playing, false otherwise
    private final int maxIterations = 40;   // Maximum number of game simulations
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

    private boolean askForGreenGraph;
    private boolean showAverageUncertaintyPlot;

    private ArrayList<Double> listOfAverageUncertainty;

    

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
        redAI = new RedAI();    //Creates red AI
        blueAI = new BlueAI();  //Creates blue AI
        
        askForGreenGraph = getOption("\nDo you wish to be asked after every round to see the green network graph? Please type in y for yes or n for no.");
        showAverageUncertaintyPlot = getOption("\nDo you wish to see the line chart of the average uncertainty over time at the end of the game? Please type in y for yes or n for no.");

        //init the array list and record the round 0 uncertainty
        if (showAverageUncertaintyPlot) {
            listOfAverageUncertainty = new ArrayList<>();
            listOfAverageUncertainty.add(gameInstance.getAverageUncertainty());
        }
        
    }

    /**
     * Returns the game instance. It returns a shallow copy so TAKE CARE.
     * @return A shallow copy of the game instance, of the type Game class
     */
    public Game getGameInstance() {
        return gameInstance;
    }

    /**
     * Initialises whether the Red or Blue agent is played by a user or AI.
     * @param agent Red or Blue agent
     */
    public void playAsUser(String agent) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%s Agent : playing as user or AI?\n", agent.substring(0,1).toUpperCase() + agent.substring(1).toLowerCase());
        boolean playAsAI = false;
        boolean correctInput = false;
        
        while (!correctInput) {
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("user")) {
                correctInput = true;
                playAsAI = false;
            } else if (userInput.equalsIgnoreCase("ai")) {
                correctInput = true;
                playAsAI = true;
            } else {
                correctInput = false;
                System.out.println("\nError, enter \'user\' or \'ai\'");
            }
        }
        if (agent.equalsIgnoreCase("red")) {
            playAsRedAI = playAsAI;
        } else if (agent.equalsIgnoreCase("blue")) {
            playAsBlueAI = playAsAI;
        }
    }
    /**
     * A blocking function that executes the game, waiting for human inputs for the red turn. The red turn is executed before the green turn
     * Prints the game statistics after each turn.
     * The game ends once the player passes in the end game command.
     * @return 0 on game end, anything else denotes an error.
     */
    public int playGame() {
        int numIterations = 0;
        gameInstance.printGreenAgents();
        gameInstance.printGreenStatistics();
        Scanner scanner = new Scanner(System.in);
        //True when game end is triggered, false when game is running
        boolean triggerGameEnd = false;         

        while (!triggerGameEnd) {
            numIterations++;

            //Execute the red turn
            playRedTurn();

            //Execute the blue turn
            playBlueTurn();
            
            //Execute the green turn
            System.out.println("\nGREEN TEAM");
            gameInstance.executeGreenTurn();

            //Print the metrics now
            gameInstance.printGreenAgents();
            gameInstance.printGreenStatistics();
            gameInstance.printBlueEnergyLevel();
            gameInstance.printRedFollowerCount();

            //Ask the user if they wish to see the graph
            if (askForGreenGraph) {
                boolean displayGraph = getOption("\nDo you wish to view the green agents bar graph? Type in y for yes or n for no");

                if (displayGraph) {
                    ProcessBuilder processBuilder = new ProcessBuilder("python3", "./GreenGrapher.py", gameInstance.getFormattedGreenViews());
                    processBuilder.redirectErrorStream(true);
                    
                    try {
                        Process process = processBuilder.start();
                        process.waitFor();
                    } catch(Exception e) {
                        System.out.println("Error, unable to launch the python script.");
                        System.out.println(e);
                    }
                }
            }

            if (showAverageUncertaintyPlot) {
                listOfAverageUncertainty.add(gameInstance.getAverageUncertainty());
            }
            
            //Trigger game end is true if blue agent energy is depleted
            triggerGameEnd = gameInstance.triggerGameEnd();
            if (!triggerGameEnd) {
                triggerGameEnd = (numIterations == maxIterations);
            }
        }
        if (gameInstance.triggerGameEnd()) {
            System.out.println("Game is Over : Blue energy depleted.");
        } 
        else if (numIterations == maxIterations) {
            System.out.println("Game is Over : Max iterations reached.");
        }
        if (gameInstance.hasMoreCertainVoters()) {
            System.out.println("Blue Agent wins!");
        } else {
            System.out.println("Red Agent wins!");
        }
        
        //Print out the average uncertainty over time if it is required
        if (showAverageUncertaintyPlot) {
            boolean displayGraph = getOption("Would you like to see the graph of the green network average uncertainty over time? Type y for yes or n for no.");
        
            if (displayGraph) {
                String paramString = "";

                for (double curValue : listOfAverageUncertainty) {
                    paramString += Double.toString(curValue);
                    paramString += ",";
                }

                //cut of the last ,
                paramString = paramString.substring(0, paramString.length() - 1);

                ProcessBuilder processBuilder = new ProcessBuilder("python3", "./UncertaintyGrapher.py", paramString);
                processBuilder.redirectErrorStream(true);
                    
                try {
                    Process process = processBuilder.start();
                    process.waitFor();
                } catch(Exception e) {
                    System.out.println("Error, unable to launch the python script.");
                    System.out.println(e);
                }
            }
        }

        scanner.close();

        return 0;
    }

    /**
     * Executes the Red agent turn, depending on whether user or AI is playing.
     */
    public void playRedTurn() {
        int redPotency;
        System.out.println("\nRED AGENT'S TURN");

        //If red AI is playing
        if (playAsRedAI) {
            redPotency = redAI.chooseMessagePotency(gameInstance.getGreenAgentsList());
        }
        //If user is playing
        else {
            redPotency = getMessagePotency("red");
        }
        System.out.printf("Red AI chose Potency %d.\n", redPotency);
        gameInstance.executeRedTurn(redPotency, false);
    }

    /**
     * Executes the Blue agent turn, depending on whether user or AI is playing.
     */
    public void playBlueTurn() {
        int blueOption, bluePotency;
        System.out.println("\nBLUE AGENT'S TURN");

        //If blue AI is playing
        if (playAsBlueAI) {
            blueOption = blueAI.chooseBlueOption(gameInstance.hasMoreCertainVoters(), gameInstance.getBlueEnergyLevel(), gameInstance.getProportionCertain());
        } 
        //If user is playing
        else {
            blueOption = getBlueOption();
        }

        //If option 1 is chosen - interact with all green agents.
        if (blueOption == 1) {
            if (playAsBlueAI) {
                bluePotency = blueAI.chooseMessagePotency(gameInstance.getGreenAgentsList());
                System.out.printf("Blue AI chose Option %d and Potency %d.\n", blueOption, bluePotency);
            } else {
                bluePotency = getMessagePotency("blue");
            }
            gameInstance.executeBlueTurn1(bluePotency, false);
        } 
        //If option 2 is chosen - let grey agent into green network.
        else if (blueOption == 2) {
            System.out.printf("Blue AI chose Option %d.\n", blueOption);
            gameInstance.executeBlueTurn2();
        }
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


      /**
       * Asks the user for input and returns true or false whether they put in y or n
       * @param requestString The message to send the user before asking them to fill in the input
       * @return true if y was input or false if n was input
       */
    public boolean getOption(String requestString) {
        String userChoice = "blank";
        
        System.out.println(requestString);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            userChoice = scanner.nextLine();
            
            if (userChoice.length() != 1) {
                System.out.println("Error, your input was not understood. Please input either y or n.");
            }

            else if (userChoice.charAt(0) == 'y') {
                return true;
            }

            else if (userChoice.charAt(0) == 'n') {
                return false;
            }

            else {
                System.out.println("Error, your input was not understood. Please input either y or n.");
            }
        }
    }

    public static void main(String[] args) {
        double[] uncertaintyInterval = {-1.0, 0.4};
        GameRunner curRunner = new GameRunner(40, 0.4, 10, 40.0, uncertaintyInterval, 60.0);
        //Ask user if red agent is played by user or AI
        curRunner.playAsUser("red");
        //Ask user if blue agent is played by user or AI
        curRunner.playAsUser("blue");
        curRunner.playGame();
    }
}