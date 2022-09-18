public class BlueAI {

    private final double MINGROUPDIFFERENCE = 0.5; //This is the minimum difference between the two groups

    private int blueOption;
    private int messagePotency;
    
    public BlueAI() {
        blueOption = 1;
        messagePotency = 1;
    }

    public int chooseBlueOption(boolean hasMoreCertainVoters, double blueEnergyLevel) {
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
         *  1. high energy and more certain voters
         *  2. high energy and more certain non-voters
         *  3. low energy and more certain voters
         *  4. low energy and more certain non-voters
         */

        return blueOption;
        //todo - dumbo
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