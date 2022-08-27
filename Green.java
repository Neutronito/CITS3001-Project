import java.util.Random;

public class Green {

    private boolean opinionOnVoting; //true represents going to vote, false represents not going to vote
    private double uncertainty;

    /**
     * Create an instance of a green agent
     * @param uncertaintyLowerBound The lower bound of the agent's uncertainty value
     * @param uncertaintyUpperBound The upper bound of the agent's uncertainty value
     */
    public Green(double uncertaintyLowerBound, double uncertaintyUpperBound) {
        //For random generation a double is not discrete so we will make it an integer
        int uncertaintyLowerBoundInteger = (int)(uncertaintyLowerBound * 1000);
        int uncertaintyUpperBoundInteger = (int)(uncertaintyUpperBound * 1000);

        Random uncertaintyGenerator = new Random();
        uncertainty = uncertaintyGenerator.nextInt(uncertaintyUpperBoundInteger - uncertaintyLowerBoundInteger);

        //Map random number so its in between the interval
        uncertainty = uncertainty / 1000.0 + (uncertaintyLowerBound);

        opinionOnVoting = false;
    }

    /**
     * Getter for the voting opinion of the green agent
     * @return The agent's voting opinion, false is not voting and true is voting
     */
    public boolean getVotingOpinion() {
        return opinionOnVoting;
    }

    /**
     * Getter for the uncertainty of the green agent
     * @return The uncertainty value
     */
    public double uncertainty() {
        return uncertainty;
    }

    /**
     * Setter for the voting opinion of the green agent
     * @param inputVotingOpinion What to set the opinion to, false is not voting and true is voting
     */
    public void setVotingOpinion (boolean inputVotingOpinion) {
        opinionOnVoting = inputVotingOpinion;
    }

    /**
     * Setter for the uncertainty of the green agent
     * @param inputUncertainty The value to set the uncerainty to
     */
    public void setUncertainty(double inputUncertainty) {
        uncertainty = inputUncertainty;
    }
    
}
