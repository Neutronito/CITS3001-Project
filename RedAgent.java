public class RedAgent {

    private int maxFollowers;
    private int followerCount;
    private double uncertainty; 

    /**
     * Constructor for the red agent
     */
    public RedAgent (int greenAgentCount, double uncertaintyValue) {
        maxFollowers = greenAgentCount;
        followerCount = greenAgentCount;
        uncertainty = uncertaintyValue;
    }

    /**
     * Getter for the uncertainty of the green agent
     * @return The uncertainty value
     */
    public double getUncertainty() {
        return uncertainty;
    }

    /**
     * Setter for the uncertainty of the green agent, includes an error check and will simply force the uncertainty to be within bounds
     * @param inputUncertainty The value to set the uncerainty to
     */
    public void setUncertainty(double inputUncertainty) {
        if (inputUncertainty >= -1 && inputUncertainty <= 1) {
            uncertainty = inputUncertainty;
        } else if (inputUncertainty < -1) {
            uncertainty = -1;
        } else {
            uncertainty = 1;
        }
    }

    /**
     * Getter for the red agent follower count
     * @return the red agent follower count
     */
    public int getFollowerCount() {
        return followerCount;
    }

    /**
     * Setter for the red agent follower count
     * @param newFollowerCount the new red agent follower count
     */
    public void setFollowerCount(int newFollowerCount) {
        if (newFollowerCount >= 0 && newFollowerCount <= maxFollowers) {
            followerCount = newFollowerCount;
        }
    }

    /**
     * Increment the red agent follower count by an amount
     * @param increasedAmount the amount to increment the follower count by
     */
    public void incrementFollower(int increasedAmount) {
        followerCount = followerCount + increasedAmount;
        if (followerCount > maxFollowers) {
            followerCount = maxFollowers;
        }
    }

    /**
     * Decrement the red agent follower count by an amount
     * @param increasedAmount the amount to decrement the follower count by
     */
    public void decrementFollower(int decreasedAmount) {
        followerCount = followerCount - decreasedAmount;
        if (followerCount < 0) {
            followerCount = 0;
        }
    }
}