public class BlueAgent {

    private double energyLevel;
    final double maxEnergy = 100.0;

    /**
     * Constructor for the blue agent
     */
    public BlueAgent () {
        energyLevel = maxEnergy;
    }

    /**
     * Getter for the blue agent energy level
     * @return the blue agent energy level
     */
    public double getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Setter for the blue agent energy level
     * @param newEnergyLevel the new blue agent energy level
     */
    public void setEnergyLevel(double newEnergyLevel) {
        if (newEnergyLevel >= 0 && newEnergyLevel <= maxEnergy) {
            energyLevel = newEnergyLevel;
        }
    }

    /**
     * Increment the blue agent energy level by an amount
     * @param increasedAmount the amount to increment the energy level by
     */
    public void incrementEnergy(double increasedAmount) {
        if ((energyLevel + increasedAmount) > maxEnergy) {
            energyLevel = maxEnergy;
        } else {
            energyLevel = energyLevel + increasedAmount;
        }
    }

    /**
     * Decrement the blue agent energy level by an amount
     * @param decreasedAmount the amount to decrement the energy level by
     */
    public void decrementEnergy(double decreasedAmount) {
        if ((energyLevel - decreasedAmount) < 0) {
            energyLevel = 0;
        } else {
            energyLevel = energyLevel - decreasedAmount;
        }
    }

    /**
     * Getter for the blue agent energy level status
     * @return true if energy level has not depleted, false if there is no more energy 
     */
    public boolean hasEnergyLevel() {
        return (energyLevel != 0);
    }
}