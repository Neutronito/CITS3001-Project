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

        //Some error handling
        if (greenAgentCount < 1) {
            throw new IllegalArgumentException("Error, number of green agents must be greater than 0.");
        }

        if (probabilityOfConnection < 0 || probabilityOfConnection > 1) {
            throw new IllegalArgumentException("Error, probability of connection must be between 0 and 1 inclusively.");
        }

        if (greenVotePercent < 0 || greenVotePercent > 100) {
            throw new IllegalArgumentException("Error, percentage of green agents that wish to vote must be between 0 and 100 inlcusively.");
        }

        if (greenUncertaintyInterval[0] > greenUncertaintyInterval[1] || greenUncertaintyInterval[0] < -1 || greenUncertaintyInterval[1] > 1) {
            throw new IllegalArgumentException("Error, the first index of the uncertainty interval must be the lowerbound and the second must be the upperbound. The ucnertainty interval also has to be between -1 and 1 inclusively.");
        }

        if (greyCount < 0) {
            throw new IllegalArgumentException("Error, the number of grey agents must be greater than 0.");
        }

        if (greyEvilProportion < 0 || greyEvilProportion > 100) {
            throw new IllegalArgumentException("Error, the percentage of grey agents that are on the red team must be between 0 and 100 inclusively.");
        }

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
        int proportion = (int)(greenVotePercent * greenAgentCount / 100);
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
        proportion = (int)(greyEvilProportion * greyAgentCount / 100);

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
     * This executes the green turn, which consists of all green nodes interaction with one another and modifying their opinion and or uncertainty
     */
     public void executeGreenTurn() {
        //Interactions are bi-directional, so each pair only needs considering once
        int loopCondition = greenAgentCount - 1;
        for (int i = 0; i < loopCondition; i++) {
            for (int j = i + 1; j < greenAgentCount; j++) {
                //Check the pair interacts
                if (greenNetwork[i][j] == 0) {
                    continue;
                }

                GreenAgent firstAgent = greenAgentsList[i];
                GreenAgent secondAgent = greenAgentsList[j];

                boolean firstOpinion = firstAgent.getVotingOpinion();
                boolean secondOpinion = secondAgent.getVotingOpinion();

                double firstUncertainty = firstAgent.getUncertainty();
                double secondUncertainty = secondAgent.getUncertainty();

                double firstOutput;
                double secondOutput;

                //They have different opinion
                if (firstOpinion != secondOpinion) {
                    
                    //This is for first agent

                    double error = (firstUncertainty + 1) - (secondUncertainty + 1);
                  
                    if (error >= 0) {
                        firstOutput = (1 - firstUncertainty) * (error / 2);
                    } else {
                        error *= -1;
                        firstOutput = (1 - firstUncertainty) * (error / 2) / 100;
                    }

                    //This is for the second agent

                    error = (secondUncertainty + 1) - (firstUncertainty + 1);
                  
                    if (error >= 0) {
                        secondOutput = (1 - secondUncertainty) * (error / 2);
                    } else {
                        error *= -1;
                        secondOutput = (1 - secondUncertainty) * (error / 2) / 100;
                    }

                }

                //Update uncertainties at the end. This is done to keep the code simple.
                //However, we can liken this to the agents going home and thinking about the conversation, and then changing their mindset so it is realistic.
                firstAgent.setUncertainty(firstOutput);
                secondAgent.setUncertainty(secondOutput);
            }
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

    /**
     * Prints out the information of all green agents in the network
     */
    public void printGreenAgents() {
        System.out.println("Agent" + "\t" + "Uncertainty" + "\t" + "Opinion");
        
        for (int i = 0; i < greenAgentCount; i++) {
            System.out.printf("%d \t %.4f \t", i, greenAgentsList[i].getUncertainty());
            System.out.println(greenAgentsList[i].getVotingOpinion()); 
        }
    }

    /**
     * Prints out the information of all the grey agents in the game
     */
    public void printGreyAgents() {
        System.out.println("Agent" + "\t" + "Team");
        for (int i = 0; i < greyAgentCount; i++) {
            System.out.print(i + "\t");
            
            if (greyAgentsList[i].getBlueTeamStatus()) {
                System.out.println("Blue");
            } else {
                System.out.println("Red");
            }
        }
    }
}

