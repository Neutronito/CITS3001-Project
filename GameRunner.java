public class GameRunner {

    private Game gameInstance;

    /**
     * This creates the game instance, it takes a clone of the Game parameters to feed into the Game class it will create.
     * @param greenAgentCount The number of green nodes to exist in the nework
     * @param probabilityOfConnection The probability of a connection between two green nodes
     * @param greyCount The number of grey nodes
     * @param greyEvilProportion The proportion of grey nodes on the red team, as a percentage
     * @param greenUncertaintyInterval The uncertainty interval as an array, the first index is lower bound and the second is upperbound
     * @param greenVotePercent The proportion of green nodes who wish to vote (at the start of the game) as a percentage
     */
    public GameRunner(int greenAgentCount, double probabilityOfConnection, int greyCount, double greyEvilProportion, double[] greenUncertaintyInterval, double greenVotePercent) {
        //There is no error checking because the Game constructor deals with this.
        gameInstance = new Game(greenAgentCount, probabilityOfConnection, greyCount, greyEvilProportion, greenUncertaintyInterval, greenVotePercent);
    }

    /**
     * Returns the game instance. It returns a shallow copy so TAKE CARE.
     * @return
     */
    public Game getGameInstance() {
        return gameInstance;
    }

    public static void main(String[] args) {
        double[] uncertaintyInterval = {-1.0, 0.4};
        GameRunner curRunner = new GameRunner(15, 0.4, 10, 40.0, uncertaintyInterval, 60.0);
        Game curGame = curRunner.getGameInstance();
        curGame.printGreenAgents();
        curGame.printGreenStatistics();
        for (int i = 0; i < 10; i++) {
            curGame.executeGreenTurn();
        }
        System.out.println();
        curGame.printGreenAgents();
        curGame.printGreenStatistics();
    }
}