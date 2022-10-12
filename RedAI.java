import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

public class RedAI {

    //First index is the map hash, the second index is the move the AI did and the third is the reward
    private ArrayList<Integer[]> currentMoves;

    //Stores all moves the AI has done, and their rewards
    //The first digit is the move, and the two next left digits are the reward from -100 to 100
    private HashMap<Integer, Integer> allMoves;

    //Tuning Parameters for the AI
    private final int EXPLORATIONRATE = 30; //30%, so 0.3 chance of doing a random explore move instead of exploitation
    private final int REWARDDIFFERENCE = 25; //If the difference is above this value, it is considered big enough to choose the better option
    private final int WORSTMOVEMIN = -10; //If we have 2 bad moves, provided one of them is above this value, we will still choose it

    public RedAI() {
        currentMoves = new ArrayList<>();
        allMoves = new HashMap<>();
    }

    /**
     * Exectues reinforcement learning to decide the move to play at this current point in the game
     * @return The message potency to play
     */
    public int chooseMessagePotency(int currentGameHash) {
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
                int potency = mapValue % 10;
                //We have a good move to consider
                if (reward > 0) {
                    //We will see if the move we did in the past was any good
                    Integer previousMove[];

                    if (!currentMoves.isEmpty()) {
                        previousMove = currentMoves.get(currentMoves.size() - 1);
                    } 
                    //If no previous move exists, fill it with this placeholder 
                    else {
                        Integer newInt[] = {0, 0, -100}; 
                        previousMove = newInt;
                    }
                    int previousPotency = previousMove[1];
                    int previousReward = previousMove[2];

                    //if one move is obviously better than the other, just take it
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
                    Integer previousMove[];
                    if (!currentMoves.isEmpty()) {
                        previousMove = currentMoves.get(currentMoves.size() - 1);
                    } else {
                        Integer newInt[] = {0, 0, -100}; 
                        previousMove = newInt;
                    }
                    int previousPotency = previousMove[1];
                    int previousReward = previousMove[2];

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
                Integer previousMove[];
                if (!currentMoves.isEmpty()) {
                    previousMove = currentMoves.get(currentMoves.size() - 1);
                } else {
                    Integer newInt[] = {0, 0, -100}; 
                    previousMove = newInt;
                }
                int previousPotency = previousMove[1];
                int previousReward = previousMove[2];

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

    //Returns a random number from 1 to 6
    public int randomMove() {
        Random numGen = new Random();
        return numGen.nextInt(5) + 1;
    }
}