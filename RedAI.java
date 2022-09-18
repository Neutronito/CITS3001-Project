public class RedAI {

    private int messagePotency;
    
    public RedAI() {
        messagePotency = 1;
    }

    public int chooseMessagePotency(GreenAgent[] greenList) {
        /*
         *  The general strategy of the Red AI is as follows:
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
        int[] proportionFactor = new int[4];

        //Loop through and process all the green agents
        for (GreenAgent curAgent : greenList) {
            int indexToWrite = 1;
            
            if (!curAgent.getVotingOpinion()) {
                indexToWrite = 3;
            }
            
            double curUncertainty = curAgent.getUncertainty();
            if (curUncertainty < 0) {
                indexToWrite--;
                curUncertainty *= 1;
            }
            proportionFactor[indexToWrite] += curUncertainty;
        }

        //Divide by total agents to obtain a weighting from 0 to 1
        for (int i = 0; i < 4; i++) {
            proportionFactor[i] /= greenCount;
        }

        //Since we are targetting 


        return messagePotency;
        /*
         * todo - hi, this currently returns 1 so needa make it choose the
         * potency depending on the opinions and uncertainties of green agents
         */
    }
}