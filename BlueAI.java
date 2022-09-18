public class BlueAI {

    private final double MINGROUPDIFFERENCE = 0.5; //This is the minimum difference between the two groups

    private int blueOption;
    private int messagePotency;
    
    public BlueAI() {
        blueOption = 1;
        messagePotency = 1;
    }

    public int chooseBlueOption(boolean hasMoreCertainVoters, double blueEnergyLevel, double proportionCertainGreens) {
        /*
         *  Option 1 : Interact with Green team
         *  - good when Blue AI has high energh level
         *  - good when there are more certain green agents who are voting (uncertainty < 0)
         *  to try end the game faster in favour of Blue Agent
         * 
         *  Option 2 : Let a Grey agent in the Green network
         *  - good when Blue AI has low energh level
         *  - good when there are more certain green agents who are NOT voting (uncertainty < 0)
         * 
         *  There are 4 scenarios
         *  1. high energy and more certain voters (winning)    - option 1 is better (end game faster)
         *  2. high energy and more certain non-voters (losing) - option 1 is better (choose more potent message)
         *  3. low energy and more certain voters (winning)     - option 1 is better (end game faster)
         *  4. low energy and more certain non-voters (losing)  - option 2 is better (better to risk grey agent than to lose)
         */

        //Index 0 - probability of choosing option 1
        //Index 1 - probability of choosing option 2
        double[] optionProbability = {0.5, 0.5};
        double changeInProbability;

        //Regardless of energy level, blue is more likely to use option 1 when winning
        if (hasMoreCertainVoters) {
            changeInProbability = (double) proportionCertainGreens / 2.0;
            optionProbability[0] += changeInProbability;    //Increase chance of option 1
            optionProbability[1] -= changeInProbability;    //Decrease chance of option 2
        }

        if (!hasMoreCertainVoters && blueEnergyLevel < 50.0) {
            changeInProbability = (double) (50 - blueEnergyLevel) / 2.0 / 100.0;
            optionProbability[0] -= changeInProbability;    //Decrease chance of option 1
            optionProbability[1] += changeInProbability;    //Increase chance of option 2
        }

        if (!hasMoreCertainVoters && blueEnergyLevel >= 50.0) {
            changeInProbability = (double) (50 - blueEnergyLevel) / 2.0 / 100.0;
            optionProbability[0] -= changeInProbability;    //Decrease chance of option 1
            optionProbability[1] += changeInProbability;    //Increase chance of option 2

        }

        double optionGenerator = Math.random();

        if (optionProbability[0] < optionProbability[1]) {
            if (optionGenerator > optionProbability[0] && optionGenerator < 1.0) {
                blueOption = 2;
            } else {
                blueOption = 1;
            }
        }
        else {
            if (optionGenerator > optionProbability[1] && optionGenerator < 1.0) {
                blueOption = 1;
            } else {
                blueOption = 2;
            }
        }
        return blueOption;
    }

    public int chooseMessagePotency(GreenAgent[] greenList) {
         /*
         *  The general strategy of the Blue AI is as follows:
         * 
         *  The lower half potency is good for when there are many certain blue agents and 
         *  many certain red agents.
         * 
         *  The upper half potency is good for when there are many uncertain blue agents and 
         *  many uncertain red agents.
         */

        int greenCount = greenList.length;

        /*
         * This array is split into four factors
         * 
         * Blue Team
         * Blue certain is index    0
         * Blue uncertain is index  1
         * 
         * Red Team
         * Red certain is index     2
         * Red uncertain is index   3
         */
        double[] proportionFactor = new double[4];

        //Loop through and process all the green agents
        for (GreenAgent curAgent : greenList) {
            int indexToWrite = 1;
            
            if (!curAgent.getVotingOpinion()) {
                indexToWrite = 3;
            }
            
            double curUncertainty = curAgent.getUncertainty();
            if (curUncertainty < 0) {
                indexToWrite--;
                curUncertainty *= -1;
            }
            proportionFactor[indexToWrite] += curUncertainty;
        }

        //Divide by total agents to obtain a weighting from 0 to 1
        for (int i = 0; i < 4; i++) {
            proportionFactor[i] /= greenCount;
        }

        //Since the two extremeties of potency affect to areas together, we can group them
        double groupedHighPotency = proportionFactor[1] + proportionFactor[3];
        double groupedLowPotency = proportionFactor[0] + proportionFactor[2];

        double difference = groupedHighPotency - groupedLowPotency;

        //Low Potency Message Dominates
        if (difference < -MINGROUPDIFFERENCE) {
            messagePotency = 1;
            return messagePotency;
        } 
        
        //High Potency Message Dominates
        else if (difference > MINGROUPDIFFERENCE) {
            messagePotency = 6;
            return messagePotency;
        }

        return messagePotency;
    }
}