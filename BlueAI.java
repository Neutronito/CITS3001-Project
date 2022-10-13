import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

import java.io.File; 
import java.io.FileNotFoundException; 
import java.util.Scanner;

import java.nio.file.*;
import java.io.IOException;
import java.io.FileWriter;

public class BlueAI {

    //First index is the map hash
    //Second index is potency AI did
    //Third index is option AI did
    //Fourth index is the reward for option
    private ArrayList<String[]> currentMoves;

    //Stores all moves the AI has done, and their rewards
    //The first digit is the move, and the next left digits are the reward from -100 to 100
    //First index of the value is potency reward, second index is option reward
    private HashMap<String, Integer[]> allMoves;

    //Tuning Parameters for the AI
    private final int EXPLORATIONRATE = 30; //30%, so 0.3 chance of doing a random explore move instead of exploitation
    private final int REWARDDIFFERENCE = 25; //If the difference is above this value, it is considered big enough to choose the better option
    private final int WORSTMOVEMIN = -10; //If we have 2 bad moves, provided one of them is above this value, we will still choose it

    public BlueAI() {
        currentMoves = new ArrayList<>();
        allMoves = new HashMap<>();

        //Read from the txt file if it exists
        //Each line represents a key value pair, the key and the value seperated by a single whitespace, respectively
        try {
            File myObj = new File("blueMap.txt");
            Scanner myReader = new Scanner(myObj);
            
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] pair = data.split(" ");
                Integer[] rewards = {Integer.parseInt(pair[1]), Integer.parseInt(pair[2])};
                allMoves.put(pair[0], rewards);
            }
            myReader.close();
        } 
          
        //Doesnt exist, thats not a problem and we just continue
        catch (FileNotFoundException e) {
        }
    }

    /**
     * Returns a random number from 1 to 6
     * @return a random number from 1 to 6
     */
    public static int randomPotencyMove() {
        Random potencyGen = new Random();
        return potencyGen.nextInt(5) + 1;
    }

    /**
     * Returns a random number 1 or 2
     * @return a random number 1 or 2
     */
    public static int randomOptionMove() {
        Random optionGen = new Random();
        return optionGen.nextInt(2) + 1;
    }

    /**
     * Exectues reinforcement learning to decide the option to play at this current point in the game
     * @return The option to play (1 for interaction, 2 for grey agent)
     */
    public int chooseBlueOption(String currentGameHash) {
        //check for this move in our moves table
        Integer[] mapValue = allMoves.get(currentGameHash);

        //We have a past move we can use
        if (mapValue != null) {
            int reward = mapValue[1] / 10;
            int option = Math.abs(mapValue[1]) % 10;
            //We have a good move to consider
            if (reward > 0) {
                //We will see if the move we did in the past was any good
                int previousOption = getPreviousOption()[0];
                int previousReward = getPreviousOption()[1];

                //If one move is obviously better than the other, just take it
                int rewardDifference = Math.abs(previousReward - reward);

                if (rewardDifference > REWARDDIFFERENCE) {
                    if (previousReward > reward) {
                        return previousOption;
                    } else {
                        return option;
                    }
                }

                //This means the moves rewards are very close, and so we will trust the previous move is the best move, provided its reward was positive
                if (previousReward > 0) {
                    return previousOption;
                } else {
                    return option;
                }
            }
            //The reward is poor, so the move we did back then wasn't very good
            else {
                //We will see if the move we did in the past was any good
                int previousOption = getPreviousOption()[0];
                int previousReward = getPreviousOption()[1];

                //The previous move was at the very least ok, so we will do it again
                if (previousReward > 0) {
                    return previousOption;
                }

                //If either of the moves were not too terrible, pick them anyway
                if (previousReward > WORSTMOVEMIN) {
                    return previousOption;
                } 
                else if (reward > WORSTMOVEMIN) {
                    return option;
                }
                //They are both terrible moves, so just return a random move that isnt either of those two
                else {
                    int moveToDo = previousOption;
                    while (moveToDo == previousOption || moveToDo == option) {
                        moveToDo = randomOptionMove();
                    }
                    return moveToDo;
                }
            }
        }
        //There is no move in our map, if our last move was good use it, else try a new move that wasn't the last move
        else {
            int previousOption = getPreviousOption()[0];
            int previousReward = getPreviousOption()[1];

            //The previous option was good
            if (previousReward > 0) {
                return previousOption;
            } 
            //The previous option was bad, so pick the other one
            else {
                int moveToDo = previousOption;
                while (moveToDo == previousOption) {
                    moveToDo = randomOptionMove();
                }
                return moveToDo;
            }
        }
    }

    /**
     * Exectues reinforcement learning to decide the potency to play at this current point in the game
     * @return The message potency to play
     */
    public int chooseMessagePotency(String currentGameHash) {
        Random numGen = new Random();
        //generate random number to see which method we use
        int randInt = numGen.nextInt(100);

        //Exploration, so pick a random move
        if (randInt < EXPLORATIONRATE) {
            return randomPotencyMove();
        } 
        
        //Use exploitation to pick the best move
        else {
            //check for this move in our moves table
            Integer[] mapValue = allMoves.get(currentGameHash);

            //We have a past move we can use
            if (mapValue != null) {
                int reward = mapValue[0] / 10;
                int potency = Math.abs(mapValue[0]) % 10;
                //We have a good move to consider
                if (reward > 0) {
                    //We will see if the move we did in the past was any good
                    int previousPotency = getPreviousPotency()[0];
                    int previousReward = getPreviousPotency()[1];

                    //If one move is obviously better than the other, just take it
                    int rewardDifference = Math.abs(previousReward - reward);

                    if (rewardDifference > REWARDDIFFERENCE) {
                        if (previousReward > reward) {
                            return previousPotency;
                        } else {
                            return potency;
                        }
                    }

                    //This means the moves rewards are very close, and so we will trust the previous move is the best move, provided its reward was positive
                    if (previousReward > 0) {
                        return previousPotency;
                    } else {
                        return potency;
                    }
                }
                //The reward is poor, so the move we did back then wasn't very good
                else {
                    //We will see if the move we did in the past was any good
                    int previousPotency = getPreviousPotency()[0];
                    int previousReward = getPreviousPotency()[1];

                    //The previous move was at the very least ok, so we will do it again
                    if (previousReward > 0) {
                        return previousPotency;
                    }

                    //If either of the moves were not too terrible, pick them anyway
                    if (previousReward > WORSTMOVEMIN) {
                        return previousPotency;
                    } 
                    else if (reward > WORSTMOVEMIN) {
                        return potency;
                    }
                    //They are both terrible moves, so just return a random move that isnt either of those two
                    else {
                        int moveToDo = previousPotency;
                        while (moveToDo == previousPotency || moveToDo == potency) {
                            moveToDo = randomPotencyMove();
                        }
                        return moveToDo;
                    }
                }
            }
            //There is no move in our map, if our last move was good use it, else try a new move that wasn't the last move
            else {
                int previousPotency = getPreviousPotency()[0];
                int previousReward = getPreviousPotency()[1];

                //The previous move was good
                if (previousReward > 0) {
                    return previousPotency;
                } 
                //The previous move was bad, so pick a random one
                else {
                    int moveToDo = previousPotency;
                    while (moveToDo == previousPotency) {
                        moveToDo = randomPotencyMove();
                    }
                    return moveToDo;
                }
            }
        }
    }

    /**
     * Get the previous potency move and its corresponding reward
     * @return An array containing the previous potency move and its corresponding reward
     */
    public int[] getPreviousPotency() {
        //We will see if the move we did in the past was any good
        String[] previousMove;
        if (!currentMoves.isEmpty()) {
            previousMove = currentMoves.get(currentMoves.size() - 1);
        } 
        //If no previous move exists, fill it with this placeholder 
        else {
            String[] newInt = {"0", "0", "0", "-100"}; 
            previousMove = newInt;
        }
        int previousPotency = Integer.parseInt(previousMove[1]);
        int previousReward = Integer.parseInt(previousMove[3]);
        int[] output = {previousPotency, previousReward};
        return output;
    }

    /**
     * Get the previous option move and its corresponding reward
     * @return An array containing the previous option move and its corresponding reward
     */
    public int[] getPreviousOption() {
        //We will see if the move we did in the past was any good
        String[] previousMove;
        if (!currentMoves.isEmpty()) {
            previousMove = currentMoves.get(currentMoves.size() - 1);
        } 
        //If no previous move exists, fill it with this placeholder 
        else {
            String[] newInt = {"0", "0", "0", "-100"}; 
            previousMove = newInt;
        }
        int previousOption = Integer.parseInt(previousMove[2]);
        int previousReward = Integer.parseInt(previousMove[3]);
        int[] output = {previousOption, previousReward};
        return output;
    }

    /**
     * Updates the allPotencyMoves with the current hash state and its corresponding reward
     * @param reward The reward for current hash state
     * @param mapHash The current hash state
     * @param previousPotency The previous potency move played
     * @param previousOption The previous option move played
     */
    public void updateRewards(int reward, String mapHash, int previousPotency, int previousOption) {
        //Store this move in our previous moves array
        String[] playedMove = new String[5];
        playedMove[0] = mapHash;
        playedMove[1] = Integer.toString(previousPotency);
        playedMove[2] = Integer.toString(previousOption);
        playedMove[3] = Integer.toString(reward);
        
        currentMoves.add(playedMove);

        //Now update the hashmap
        Integer[] hashReward = allMoves.get(mapHash);
        int hashPotency = Math.abs(reward * 10) + previousPotency;
        int hashOption  = Math.abs(reward * 10) + previousOption;

        //There is no entry, so we can just fill it
        if (hashReward == null) {
            if (reward < 0) {
                hashPotency *= -1;
            }
            if (reward < 0) {
                hashOption *= -1;
            }
            Integer[] hashValue = {hashPotency, hashOption};
            allMoves.put(mapHash, hashValue);
        } else {
            if (reward < hashReward[0]) {
                if (reward < 0) {
                    hashPotency *= -1;
                }
                Integer[] hashValue = {hashPotency, hashReward[1]};
                allMoves.put(mapHash, hashValue);
            } 

            if (reward < hashReward[1]) {
                if (reward < 0) {
                    hashOption *= -1;
                }
                if (hashReward[0] == allMoves.get(mapHash)[0]) {
                    Integer[] hashValue = {hashReward[0], hashOption};
                    allMoves.put(mapHash, hashValue);
                } else {
                    Integer[] hashValue = {hashPotency, hashOption};
                    allMoves.put(mapHash, hashValue);
                }
            } 
        }
    }

    /**
     * Must be called when the game ends, saves the updated hashmap into the txt file
     */
    public void endGame() {
        //If the file exists delete it
        Path path = FileSystems.getDefault().getPath("./src/test/resources/blueMap.txt");
        try {
            Files.delete(path);
        } catch(Exception e) {
            ; //If the file doesnt exist no need to do anything
        }

        //Now write the current hashMap into the file
        FileWriter fileWriter = null;
        try {
            //Creating File Object
            File file = new File("blueMap.txt");
            //Initializing filewriter object
            fileWriter = new FileWriter(file);

            //Loop through the hashmap, writing it into the file
            for (HashMap.Entry<String, Integer[]> entry : allMoves.entrySet()){
                fileWriter.write(entry.getKey() + " " + Integer.toString(entry.getValue()[0]) + " " + Integer.toString(entry.getValue()[1]) + "\n");
            }
        } 
        catch (IOException iOException) {
            System.out.println("Error : " + iOException.getMessage());
        } 
        finally {
            if (fileWriter != null) {
                try {
                    //Closing file writer object
                    fileWriter.close();
                } catch (IOException iOException) {
                    System.out.println("Error : " + iOException.getMessage());
                }
            }
        }
    }
}
