import java.util.ArrayList;
import java.util.Random;

public class QLearning {

    private final int REWARD = 100;
    private final int PENALTY = -10;
    private final int STATESCOUNT = 40;

    private boolean isBlue;
    private int[][] R;       // Reward lookup
    private double[][] Q;    // Q learning
    ArrayList<Integer> possibleActions;

    public QLearning(boolean isBlue) {
        this.isBlue = isBlue;
        initialiseQ();
        possibleActions = new ArrayList<>();

        // Choose potent levels 1 to 6
        for (int i = 0; i < 6; i ++) {
            possibleActions.add(i + 1);
        }
        // If AI is blue, choose option let grey into network
        if (isBlue) {
            possibleActions.add(7);
        }
    }

    public void initialiseQ() {
        for (int i = 0; i < STATESCOUNT; i ++) {
            for (int j = 0; i < STATESCOUNT; j++) {
                Q[i][j] = 0;    // 0 means Q value was never updated (nothing learned)
            }
        }
    }

    public void calculateQ() {
        for (int i = 0; i < STATESCOUNT; i++) { // Train cycles

            // Idk..
            // voters > non-voters for blue AI
            // 30 > 10 is better than 21 > 19 for blue AI - greater reward?
        }
    }

    public boolean isFinalState(int[] votingOpinions, boolean triggerGameEnd, int numIterations) {
        // If AI is blue, desired state is voters > non-voters when game has ended
        if (isBlue) {
            return (votingOpinions[1] > votingOpinions[0] && (triggerGameEnd || numIterations == 40));
        } 
        // If AI is red, desired state is non-voters > voters
        else {
            return (votingOpinions[0] > votingOpinions[1] && (triggerGameEnd ||  numIterations == 40));
        }
    }
         
}



// void calculateQ() {
//     Random rand = new Random();

//     for (int i = 0; i < 1000; i++) { // Train cycles
//         // Select random initial state
//         int crtState = rand.nextInt(statesCount);

//         while (!isFinalState(crtState)) {
//             int[] actionsFromCurrentState = possibleActionsFromState(crtState);

//             // Pick a random action from the ones possible
//             int index = rand.nextInt(actionsFromCurrentState.length);
//             int nextState = actionsFromCurrentState[index];

//             // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
//             double q = Q[crtState][nextState];
//             double maxQ = maxQ(nextState);
//             int r = R[crtState][nextState];

//             double value = q + alpha * (r + gamma * maxQ - q);
//             Q[crtState][nextState] = value;

//             crtState = nextState;
//         }
//     }
// }
