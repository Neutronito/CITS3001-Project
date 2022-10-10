public class RedAgent {

    private int maxFollowers;
    private int followerCount;

    /**
     * Constructor for the red agent
     */
    public RedAgent (int greenAgentCount) {
        maxFollowers = greenAgentCount;
        followerCount = greenAgentCount;
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
        if ((followerCount + increasedAmount) > maxFollowers) {
            followerCount = maxFollowers;
        } else {
            followerCount = followerCount - increasedAmount;
        }
    }

    /**
     * Decrement the red agent follower count by an amount
     * @param increasedAmount the amount to decrement the follower count by
     */
    public void decrementFollower(int decreasedAmount) {
        if ((followerCount - decreasedAmount) < 0) {
            followerCount = 0;
        } else {
            followerCount = followerCount - decreasedAmount;
        }

    }
}