public class BlueAIOLD {

    private final double MINGROUPDIFFERENCE = 0.5; //This is the minimum difference between the two groups

    private int blueOption;
    private int messagePotency;
    
    public BlueAIOLD() {
        blueOption = 1;
        messagePotency = 1;
    }

    public int chooseBlueOption(boolean hasMoreCertainVoters, double blueEnergyLevel, double[] proportionCertain) {
        /*
         *  Option 1 : Interact with Green team
         *  - good when Blue AI has high energh level
         *  - good when there are more certain green agents who are voting (uncertainty < 0)
         *  to try end the game faster in favour of Blue Agent
         * 
         *  Option 2 : Let a Grey agent in the Green network
         *  - good when Blue AI has low energh level
         *  - good when there are more certain green agents who are NOT voting (uncertainty < 0)
         */

        //Index 0 - probability of choosing option 1
        //Index 1 - probability of choosing option 2
        double[] optionProbability = {0.5, 0.5};
        double changeInProbability;
        double proportionCertainGreens  = proportionCertain[0];
        double proportionCertainBlues   = proportionCertain[1];
        double proportionUncertainGreens = 1.0 - proportionCertainGreens;
        
        //Higher the proportion of certain greens means lower chance of choosing option 1 (save energy)
        if (proportionCertainGreens > 0.5) {
            changeInProbability = (proportionCertainGreens - 0.5) / 2.0;    //Based on proportion of certain greens
            optionProbability[0] -= changeInProbability;                    //Decrease chance of option 1
            optionProbability[1] += changeInProbability;                    //Increase chance of option 2
        } 
        //Higher the proportion of uncertain greens means higher chance of choosing option 1 (save energy)
        else {
            changeInProbability = (proportionUncertainGreens - 0.5) / 2.0;  //Based on proportion of certain greens
            optionProbability[0] += changeInProbability;                    //Increase chance of option 1
            optionProbability[1] -= changeInProbability;                    //Decrease chance of option 2
        }
        
        //Energy is low
        if (blueEnergyLevel < 50.0) {
            //Blue is winning and low energy, therefore higher chance of choosing option 1
            if (hasMoreCertainVoters) {
                changeInProbability = (proportionCertainBlues) / 2.0; //Based on proportion of certain blues
                optionProbability[0] += changeInProbability;          //Increase chance of option 1
                optionProbability[1] -= changeInProbability;          //Decrease chance of option 2
            } 
             //Blue is losing and low energy, therefore higher chance of choosing option 2
            else {
                changeInProbability = (proportionCertainBlues) / 2.0; //Based on proportion of certain blues
                optionProbability[0] -= changeInProbability;          //Decrease chance of option 1
                optionProbability[1] += changeInProbability;          //Increase chance of option 2
            }
        }
        //Energy is high
        else {
            //Blue is winning and high energy, therefore higher chance of choosing option 2
            if (hasMoreCertainVoters) {
                changeInProbability = (proportionCertainBlues) / 2.0; //Based on proportion of certain blues
                optionProbability[0] -= changeInProbability;          //Increase chance of option 1
                optionProbability[1] += changeInProbability;          //Decrease chance of option 2
            } 
             //Blue is losing and high energy, therefore higher chance of choosing option 1
            else {
                changeInProbability = (proportionCertainBlues) / 2.0; //Based on proportion of certain blues
                optionProbability[0] += changeInProbability;          //Decrease chance of option 1
                optionProbability[1] -= changeInProbability;          //Increase chance of option 2
            }
        }

        //Now choose blue option based on probability of being chosen
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
        int greenCount = greenList.length;
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

        //The potency is very close, so we should act in between extremities
        else if (difference < 0) {
            if (difference < -(MINGROUPDIFFERENCE / 2)) {
                messagePotency = 2;
            } else {
                messagePotency = 3;
            }
        } 
        //The difference must be between 0 and +Minimum group difference
        else {
            if (difference > (MINGROUPDIFFERENCE / 2)) {
                messagePotency = 5;
            } else {
                messagePotency = 4;
            }
        }
        return messagePotency;
    }
}