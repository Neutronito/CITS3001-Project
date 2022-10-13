import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

import java.io.File; 
import java.io.FileNotFoundException; 
import java.util.Scanner;

import javax.management.ValueExp;

import java.nio.file.*;
import java.io.IOException;
import java.io.FileWriter;

public class BlueAI {
    //First index is the map hash, the second index is the move the AI did and the third is the reward
    private ArrayList<String[]> currentMoves;

    //Stores all moves the AI has done, and their rewards
    //The first digit is the move, and the next left digits are the reward from -100 to 100
    private HashMap<String, Integer> allMoves;

    //Tuning Parameters for the AI
    private final int EXPLORATIONRATE = 30; //30%, so 0.3 chance of doing a random explore move instead of exploitation
    private final int REWARDDIFFERENCE = 25; //If the difference is above this value, it is considered big enough to choose the better option
    private final int WORSTMOVEMIN = -10; //If we have 2 bad moves, provided one of them is above this value, we will still choose it

    /*
     * TODO : Blue AI Option
     */
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
                
                allMoves.put(pair[0], Integer.parseInt(pair[1]));
            }
            myReader.close();
        } 
          
        //Doesnt exist, thats not a problem and we just continue
        catch (FileNotFoundException e) {
        }
    }

    //Returns a random number from 1 to 6
    public static int randomMove() {
        Random numGen = new Random();
        return numGen.nextInt(5) + 1;
    }

    public int chooseBlueOption() {
        return 1;
    }
    /**
     * Exectues reinforcement learning to decide the move to play at this current point in the game
     * @return The message potency to play
     */
    public int chooseMessagePotency(String currentGameHash) {
        Random numGen = new Random();
        //generate random number to see which method we use
        int randInt = numGen.nextInt(100);

        //Exploration, so pick a random move
        if (randInt < EXPLORATIONRATE) {
            return randomMove();
        } 
        
        //Use exploitation to pick the best move
        else {
            //check for this move in our moves table
            Integer mapValue = allMoves.get(currentGameHash);

            //We have a past move we can use
            if (mapValue != null) {
                
                int reward = mapValue / 10;
                int potency = Math.abs(mapValue) % 10;
                //We have a good move to consider
                if (reward > 0) {
                    //We will see if the move we did in the past was any good
                    String previousMove[];;

                    if (!currentMoves.isEmpty()) {
                        previousMove = currentMoves.get(currentMoves.size() - 1);
                    } 
                    //If no previous move exists, fill it with this placeholder 
                    else {
                        String newInt[] = {"0", "0", "-100"}; 
                        previousMove = newInt;
                    }
                    int previousPotency = Integer.parseInt(previousMove[1]);
                    int previousReward = Integer.parseInt(previousMove[2]);

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
                    String previousMove[];
                    if (!currentMoves.isEmpty()) {
                        previousMove = currentMoves.get(currentMoves.size() - 1);
                    } else {
                        String newInt[] = {"0", "0", "-100"}; 
                        previousMove = newInt;
                    }
                    int previousPotency = Integer.parseInt(previousMove[1]);
                    int previousReward = Integer.parseInt(previousMove[2]);

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
                            moveToDo = randomMove();
                        }
                        return moveToDo;
                    }
                }

            }
            //There is no move in our map, if our last move was good use it, else try a new move that wasn't the last move
            else {
                String previousMove[];
                if (!currentMoves.isEmpty()) {
                    previousMove = currentMoves.get(currentMoves.size() - 1);
                } else {
                    String newInt[] = {"0", "0", "-100"}; 
                    previousMove = newInt;
                }
                int previousPotency = Integer.parseInt(previousMove[1]);
                int previousReward = Integer.parseInt(previousMove[2]);

                //The previous move was good
                if (previousReward > 0) {
                    return previousPotency;
                } 
                //The previous move was bad, so pick a random one
                else {
                    int moveToDo = previousPotency;
                        while (moveToDo == previousPotency) {
                            moveToDo = randomMove();
                        }
                        return moveToDo;
                }
            }
        }
          
    }


    public void updateRewards(int reward, String mapHash, int previousMove) {
        //Store this move in our previous moves array
        String[] playedMove = new String[3];
        playedMove[0] = mapHash;
        playedMove[1] = Integer.toString(previousMove);
        playedMove[2] = Integer.toString(reward);
        
        currentMoves.add(playedMove);

        //Now update the hashmap
        Integer hashReward = allMoves.get(mapHash);
        
        //There is no entry, so we can just fill it
        if (hashReward == null) {
            int hashValue = Math.abs(reward * 10) + previousMove;
            if (reward < 0) {
                hashValue *= -1;
            }
            allMoves.put(mapHash, hashValue);
        } else {
            //There is something already here.
            //I want to try something, i want the algorithm to trust its previous moves more than the map,
            //so I am going to make it take the smallest value
            if (reward < hashReward) {
                int hashValue = Math.abs(reward * 10) + previousMove;
                if (reward < 0) {
                    hashValue *= -1;
                }
                allMoves.put(mapHash, hashValue);
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
            //If the file doesnt exist no need to do anything
        }

        //Now write the current hashMap into the file
        FileWriter fileWriter = null;
        try {
            //Creating File Object
            File file = new File("blueMap.txt");
            //Initializing filewriter object
            fileWriter = new FileWriter(file);

            //Loop through the hashmap, writing it into the file
            //Each line represents a key value pair, the key and the value seperated by a single whitespace, respectively
            for (HashMap.Entry<String, Integer> entry : allMoves.entrySet()){
                fileWriter.write(entry.getKey() + " " + Integer.toString(entry.getValue()) + "\n");
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
