import org.jgrapht.generate.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Game {

    private double[][] greenNetwork;
    private GreenAgent[] greenAgentsList;
    private int greenAgentCount;

    private GreyAgent[] greyAgentsList;
    private int greyAgentCount;

    /**
     * Creates an instance of the game using the input parameters
     * @param greenAgentCount The number of green nodes to exist in the nework
     * @param probabilityOfConnection The probability of a connection between two green nodes
     * @param greyCount The number of grey nodes
     * @param greyEvilProportion The proportion of grey nodes on the red team, as a percentage
     * @param greenUncertaintyInterval The uncertainty interval as an array, the first index is lower bound and the second is upperbound
     * @param greenVotePercent The proportion of green nodes who wish to vote (at the start of the game) as a percentage
     */
    public Game(int greenAgentCount, double probabilityOfConnection, int greyCount, double greyEvilProportion, double[] greenUncertaintyInterval, double greenVotePercent) {
        //Fill variables
        this.greenAgentCount = greenAgentCount;
        greyAgentCount = greyCount;

        //Use the jgraph library to create a graph generator that uses Erdos Renyi Model
        GnpRandomGraphGenerator<Integer,DefaultWeightedEdge> greenNetworkGenerator = new GnpRandomGraphGenerator<>(greenAgentCount, probabilityOfConnection);
        
        //Init the graph 
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> greenNetworkjgrapht = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        
        //Create the vertex supplier
        Supplier<Integer> greenSupplier = new Supplier<Integer>() {
            private int id = 0;
            
            public Integer get() {
                return id++;
            }
        };

        greenNetworkjgrapht.setVertexSupplier(greenSupplier);

        //Use the generator to make the graph
        greenNetworkGenerator.generateGraph(greenNetworkjgrapht);

        //Now put the weights into the matrix
        greenNetwork = new double[greenAgentCount][greenAgentCount];

        for (int i = 0; i < greenAgentCount; i++) {
            for (int j = 0; j < greenAgentCount; j++) {
                //Check if it exists
                if (i == j) {
                    greenNetwork[i][j] = 1.0;
                    continue;
                }
                if (greenNetworkjgrapht.getEdge(i, j) != null) {
                    greenNetwork[i][j] = greenNetworkjgrapht.getEdgeWeight(greenNetworkjgrapht.getEdge(i, j));
                } else {
                    greenNetwork[i][j] = 0;
                }
            }
        }

        //Create all the green agents
        greenAgentsList = new GreenAgent[greenAgentCount];
        for (int i = 0; i < greenAgentCount; i++) {
            greenAgentsList[i] = new GreenAgent(greenUncertaintyInterval[0], greenUncertaintyInterval[1], false);
        }

        //Now make sure the right proportion are voting, use random generation for this
        ArrayList<Integer> greenAgentsRandom = new ArrayList<>();

        for (int i = 0; i < greenAgentCount; i++) {
            greenAgentsRandom.add(i);
        }

        int total = greenAgentCount;
        int proportion = (int)greenVotePercent * greenAgentCount;
        Random votingGenerator = new Random();

        while ((greenAgentCount - total) < proportion) {
            
            //Randomly choose a green and make it want to vote
            int generatedIndex = votingGenerator.nextInt(total);
            int greenID = greenAgentsRandom.get(generatedIndex);
            greenAgentsList[greenID].setVotingOpinion(true);

            //Now remove it so we don't consider it again
            greenAgentsRandom.remove(Integer.valueOf(generatedIndex));
            total--;
        }

        //Create all the grey agents
        greyAgentsList = new GreyAgent[greyAgentCount];
        for (int i = 0; i < greyAgentCount; i++) {
            greyAgentsList[i] = new GreyAgent(true);
        }

        //Now make sure the right proportion are voting, use random generation for this
        ArrayList<Integer> greyAgentsRandom = new ArrayList<>();

        for (int i = 0; i < greyAgentCount; i++) {
            greyAgentsRandom.add(i);
        }

        total = greyAgentCount;
        proportion = (int)greyEvilProportion * greyAgentCount;

        while ((greyAgentCount - total) < proportion) {
            
            //Randomly choose a green and make it want to vote
            int generatedIndex = votingGenerator.nextInt(total);
            int greyID = greyAgentsRandom.get(generatedIndex);
            greyAgentsList[greyID].setBlueTeamStatus(false);

            //Now remove it so we don't consider it again
            greyAgentsRandom.remove(Integer.valueOf(generatedIndex));
            total--;
        }
    }

    /**
     * Print out the green network as an adjacency matrix with all the edge weights
     */
    public void printGreenNetwork() {

        System.out.print("\t");
        for (int i = 0; i < greenAgentCount; i++) {
            System.out.print(i + "\t");
        }
        System.out.println();


        for (int i = 0; i < greenAgentCount; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < greenAgentCount; j++) {
                System.out.print(greenNetwork[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        double[] uncertaintyInterval = {-1.0, 1.0};
        Game curGame = new Game(10, 0.4, 0, 0.0, uncertaintyInterval, 10.0);
        curGame.printGreenNetwork();
    }

}

