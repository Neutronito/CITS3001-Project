public class BlueAgent {

    private int energyLevel;

    /**
     * Constructor for the blue agent
     */
    public BlueAgent () {
        energyLevel = 100;
    }

    /**
     * Getter for the blue agent energy level
     * @return the blue agent energy level
     */
    public int getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Setter for the blue agent energy level
     * @param newEnergyLevel the new blue agent energy level
     */
    public void setEnergyLevel(int newEnergyLevel) {
        energyLevel = newEnergyLevel;
    }

    /**
     * Increment the blue agent energy level by an amount
     * @param increasedAmount the amount to increment the energy level by
     */
    public void incrementEnergy(int increasedAmount) {
        energyLevel = energyLevel + increasedAmount;
    }

    /**
     * Decrement the blue agent energy level by an amount
     * @param increasedAmount the amount to decrement the energy level by
     */
    public void decrementEnergy(int decreasedAmount) {
        energyLevel = energyLevel - decreasedAmount;
    }

    /**
     * Getter for the blue agent energy level status
     * @return true if energy level has not depleted, false if there is no more energy 
     */
    public boolean hasEnergyLevel() {
        if (energyLevel != 0) {
            return true;
        }
        return false;
    }
}