public class GreyAgent {
    
    private boolean onBlueTeam; //true if the agent is on the blue team, false if on red team
    
    /**
     * Constructor for the grey agent
     * @param inputOnBlueTeam true if a member of the blue team, false if a member of the red team
     */
    public GreyAgent (boolean inputOnBlueTeam) {
        onBlueTeam = inputOnBlueTeam;
    }

    /**
     * Getter for the grey agent blue team status
     * @return true if on the blue team, false if on the red team
     */
    public boolean getBlueTeamStatus() {
        return onBlueTeam;
    }
    
    /**
     * Setter for the grey agent blue team status
     * @param inputBlueTeamStatus true if on the blue team, false if on the red team
     */
    public void setBlueTeamStatus(boolean inputBlueTeamStatus) {
        onBlueTeam = inputBlueTeamStatus;
    }
}
