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

    private BlueAgent blueAgent;
    private RedAgent redAgent;

    private final double OPINIONTHRESHOLD = 0.6; //Above this uncertainty inclusively, an agents opinion can change
    private final double FLIPUPPERBOUND = 0.8; //Upperbound of the increase when flipping opinion
    private final double FLIPLOWERBOUND = 0.4; //Lowerbound of the increase when flipping opinion

    private final int OPINIONSCALEFACTOR = 1000; //Used in calculations, the higher the value the greater accuracy
    private final double OPINIONSETAFTERCHANGE = 0.6; //If an agents opinion changes, this is what their uncerainty becomes 
    
    private final double CHANGEPOSITIVETHRESHOLD = 0.05; //Threshold if uncerainty increases
    private final double CHANGENEGATIVETHRESHOLD  = 0.15; //Threshold if uncertainty decreases

    private final double PULLUPSCALEFACTOR = 1.2; //This is a factor mutliplied to the increase value (of the weaker node) when two nodes agree
    
    private final double[] POTENCYRANGE = {0.1, 0.4}; //This is the potency effect range of the map from the red and blue message potency

    private final double ENERGYSCALEFACTOR = 1.5; //This is a factor mutliplied to calculate the amount of energy loss for blue agent
    private final double FOLLOWERSCALEFACTOR = 0.025; //This is a factor mutliplied to calculate the amount of follower loss for red agent

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

        for (int i = 0; i < proportion; i++) {
            //Randomly choose a green and make it want to vote
            int generatedIndex = votingGenerator.nextInt(total);
            int greenID = greenAgentsRandom.get(generatedIndex);
            greenAgentsList[greenID].setVotingOpinion(true);
            
            //Now remove it so we don't consider it again
            greenAgentsRandom.remove(generatedIndex);
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
            greyAgentsRandom.remove(generatedIndex);
            total--;
        }

        //Create the blue agent
        blueAgent = new BlueAgent();

        //Create the red agent
        redAgent = new RedAgent(greenAgentCount);
    }

    /**
     * This executes the green turn, which consists of all green nodes interaction with one another and modifying their opinion and or uncertainty
     */
    public void executeGreenTurn() {

        //Record all the starting uncertainties of nodes
        double[] curGreenUncertainties = new double[greenAgentCount];

        for (int i = 0; i < greenAgentCount; i++) {
            curGreenUncertainties[i] = greenAgentsList[i].getUncertainty();
        }

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

                double firstOutput = firstUncertainty;
                double secondOutput = secondUncertainty;

                //They have different opinion
                if (firstOpinion != secondOpinion) {
                    
                    //This is for first agent
                    double error = (firstUncertainty + 1) - (secondUncertainty + 1);
                  
                    if (error >= 0) {
                        firstOutput = (1 - firstUncertainty) * (error / 2) + firstUncertainty;
                    } else {
                        error *= -1;
                        firstOutput = (1 - firstUncertainty) * (error / 2) / 100 + firstUncertainty;
                    }

                    //This is for the second agent
                    error = (secondUncertainty + 1) - (firstUncertainty + 1);
                  
                    if (error >= 0) {
                        secondOutput = (1 - secondUncertainty) * (error / 2) + secondUncertainty;
                    } else {
                        error *= -1;
                        secondOutput = (1 - secondUncertainty) * (error / 2) / 100 + secondUncertainty;
                    }

                } 
                
                //They have the same opinion.
                else {

                    //Effectively, the more certain agent will pull up the least certain agent
                    double error = (firstUncertainty + 1) - (secondUncertainty + 1);

                    //Second agent is more certain 
                    if (error > 0) {
                        firstOutput = (1 - firstUncertainty) * (error / 2) + firstUncertainty;
                        firstOutput *= PULLUPSCALEFACTOR;
                    }
                    //First agent is more certain
                    else {
                        error *= -1;
                        secondOutput = (1 - secondUncertainty) * (error / 2) + secondUncertainty;
                        secondOutput *= PULLUPSCALEFACTOR;
                    }
                }
                
                //Update uncertainties at the end. This is done to keep the code simple.
                //However, we can liken this to the agents going home and thinking about the conversation, and then changing their mindset so it is realistic.
                firstAgent.setUncertainty(firstOutput);
                secondAgent.setUncertainty(secondOutput);

                //Change opinion if green agent uncertainty is above the threshold
                changeGreenOpinion(firstAgent);
                changeGreenOpinion(secondAgent);
            }
        }

        //Now consider the change of each agents uncertainty
        //If an agents uncertainty didnt change much, they passively become slightly more certain
        for (int i = 0; i < greenAgentCount; i++) {
            double change = greenAgentsList[i].getUncertainty() - curGreenUncertainties[i];

            //Uncertainty decreased
            if (change < 0) {
                change *= -1;
                //Check if its below the threshold
                if (change < CHANGENEGATIVETHRESHOLD) {
                    double increase = CHANGENEGATIVETHRESHOLD - change;
                    greenAgentsList[i].setUncertainty(greenAgentsList[i].getUncertainty() - increase);
                }
            }
            //Uncertainty increased
            else {
                //Check if its below the threshold
                if (change < CHANGEPOSITIVETHRESHOLD) {
                    double increase = CHANGENEGATIVETHRESHOLD - change;
                    greenAgentsList[i].setUncertainty(greenAgentsList[i].getUncertainty() - increase);
                }
            }
        }
    }

    /**
     * Changes a green agent's opinion based on their uncertainty.
     * The probability of an opinion changing is based upon how far into the threshold it is.
     * @param greenAgent The green agent to be considered.
     */
    public void changeGreenOpinion(GreenAgent greenAgent) {
        Random opinionGenerator = new Random();
        //Check if green agent uncertainty is above the threshold
        if (greenAgent.getUncertainty() > OPINIONTHRESHOLD) {
            //Note, we assume the threshold is always positive
            int thresholdRange = (int)((1 - OPINIONTHRESHOLD) * OPINIONSCALEFACTOR);
            int randomNumber = opinionGenerator.nextInt(thresholdRange);
            int greenAgentMapped = (int)((greenAgent.getUncertainty() - OPINIONTHRESHOLD) * OPINIONSCALEFACTOR);
            
            //This means their opinion changes
            if (randomNumber <= greenAgentMapped) {
                greenAgent.setVotingOpinion(!greenAgent.getVotingOpinion());
                
                //Pick a random value to increase by based on the given range.
                int range = (int)((FLIPUPPERBOUND - FLIPLOWERBOUND) * OPINIONSCALEFACTOR);
                double increase = opinionGenerator.nextInt(range);
                increase /= OPINIONSCALEFACTOR;

                greenAgent.setUncertainty(greenAgent.getUncertainty() - increase);
            }
        }
    }

    /**
     * Executes the red turn, based on the given message potency.
     * @param messagePotency The message potency from 1 to 6 inclusive, the higher the number the more potent.
     * @param byGreySpy True if grey spy interacts with green team without losing followers, false otherwise.
     */
    public void executeRedTurn(int messagePotency, boolean byGreySpy) {
        //Get mapped potency
        double mappedPotency = handleMessagePotency(messagePotency);

        //If red turn is not executed by grey spy, decrease follower count
        if (!byGreySpy) {
            if (messagePotency >= 3) {
                int potency = messagePotency - 3;
                int followerLoss = (int) (greenAgentCount * potency * FOLLOWERSCALEFACTOR);
                redAgent.decrementFollower(followerLoss); 
            }
        }

        //Loop through a number of random green agents
        //Number depends on the red agent follower count
        Random indexGenerator = new Random();   
        ArrayList<Integer> interactedGreens = new ArrayList<>(); 
        for (int i = 0; i < redAgent.getFollowerCount(); i++) {
            
            //Get random green agent
            int greenIndex = indexGenerator.nextInt(redAgent.getFollowerCount());
            //Check if the green agent has already interacted or not
            while(interactedGreens.contains(greenIndex)) {
                greenIndex = indexGenerator.nextInt(redAgent.getFollowerCount());
            }

            //Green agent has not interacted yet, so interact now
            interactedGreens.add(greenIndex); 
            GreenAgent curAgent = greenAgentsList[greenIndex];
            double newUncertainty = curAgent.getUncertainty();
            
            //Agent is on the blue team
            if (curAgent.getVotingOpinion()) {
                //The agent is "certain", so their uncertainty decreases
                if (curAgent.getUncertainty() < 0) {
                    newUncertainty -= mappedPotency;
                }
                //The agent is "uncertain", so their uncertainty increases
                else {
                    newUncertainty += mappedPotency;
                }
            }
            //Agent is on the red team
            else {
                //The agent is "certain", so their uncertainty increases
                if (curAgent.getUncertainty() < 0) {
                    newUncertainty += mappedPotency;
                }
                //The agent is "uncertain", so their uncertainty decreases
                else {
                    newUncertainty -= mappedPotency;
                }
            }
            curAgent.setUncertainty(newUncertainty);
        }
    }

    /**
     * Executes the blue turn option 1 to interact with green team, based on the given message potency.
     * @param messagePotency The message potency from 1 to 6 inclusive, the higher the number the more potent.
     * @param byGreyAgent True if grey agent interacts with green team without losing energy, false otherwise.
     */
    public void executeBlueTurn1(int messagePotency, boolean byGreyAgent) {
        //Get mapped potency
        double mappedPotency = handleMessagePotency(messagePotency);

        //Loop through all the green agents
        for (GreenAgent curAgent : greenAgentsList) {
            double newUncertainty = curAgent.getUncertainty();

            // If blue turn is not executed by grey agent and green agent is certain, decrease energy level
            // Green agent is certain if uncertainty is less than 0
            // The more potent a message, the higher the energy loss
            if (!byGreyAgent && curAgent.getUncertainty() < 0) {
                double curUncertainty = -curAgent.getUncertainty(); 
                double energyLoss =  curUncertainty * ENERGYSCALEFACTOR;
                blueAgent.decrementEnergy(energyLoss);
            }

            //Agent is on the blue team
            if (curAgent.getVotingOpinion()) {  
                //The agent is "certain", so their uncertainty increases and blue loses energy
                if (curAgent.getUncertainty() < 0) {
                    newUncertainty += mappedPotency;
                }
                //The agent is "uncertain", so their uncertainty decreases
                else {
                    newUncertainty -= mappedPotency;
                }
            }
            //Agent is on the red team
            else { 
                //The agent is "certain", so their uncertainty decreases and blue loses energy
                if (curAgent.getUncertainty() < 0) {
                }
                //The agent is "uncertain", so their uncertainty increases
                else {
                    newUncertainty += mappedPotency;
                }
            }
            curAgent.setUncertainty(newUncertainty);
        }
    }

    /**
     * Executes the blue turn option 2 to let a grey agent into the green network.
     */
    public void executeBlueTurn2() {
        Random indexGenerator = new Random();
        int greyAgentIndex = indexGenerator.nextInt(greyAgentCount);
        GreyAgent greyAgent = greyAgentsList[greyAgentIndex];
        int messagePotency = greyAgent.chooseMessage();
        System.out.printf("\nGrey Agent %d chosen is from %s team.\n", greyAgentIndex, greyAgent.getBlueTeamStatus() ? "blue" : "red");

        //Grey Agent is on the blue team
        if (greyAgent.getBlueTeamStatus()) {
            executeBlueTurn1(messagePotency, true);
        }
        //Grey Agent is a spy from the read team
        else {
            executeRedTurn(messagePotency, true);
        }
    }

    /**
     * Handles the error checking and calculation of the given message potency. 
     * @param messagePotency The message potency from 1 to 6 inclusive, the higher the number the more potent.
     * @return The mapped potency.
     */
    public double handleMessagePotency(int messagePotency) {
        //Error handling
        if (messagePotency <= 0 || messagePotency > 6) {
            throw new IllegalArgumentException("Error, the message potency must be within the range 0 to 5 inclusively.");
        }

        /*
         * If the potency is 3 or less, it has a negative effect (i.e. the opposite) of what is mentioned below.
         * The smaller the number, the more negative the effect.
         * If the potency is 4 or more, then it has the effect as stated below.
         * The bigger the number, the greater the effect.  
         */
        double mappedPotency = 0;

        //Map the message potency
        if (messagePotency <= 3) {
            //inverse map potency, 1 highest 3 lowest
            messagePotency = 4 - messagePotency;
            mappedPotency = POTENCYRANGE[0] + ((POTENCYRANGE[1] - POTENCYRANGE[0]) / 2) * (messagePotency - 1); 
            mappedPotency *= -1;
        } else {
            messagePotency -= 3;
            mappedPotency = POTENCYRANGE[0] + ((POTENCYRANGE[1] - POTENCYRANGE[0]) / 2) * (messagePotency - 1);
        }
        return mappedPotency;
    }

    /**
     * Triggers game end, returns true if game has ended, false otherwise.
     * @return True if game has ended, false otherwise.
     */
    public boolean triggerGameEnd() {
        return !blueAgent.hasEnergyLevel();
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
     * Prints out several metrics of the green agents, which is the total agents, the number of agents 
     * voting yes, the number of agents voting no and the average uncertainty. 
     */
    public void printGreenStatistics() {
        int greenYes = 0;
        int greenNo = 0;
        double uncertaintyTotal = 0;

        for (int i = 0; i < greenAgentCount; i++) {
            
            //Increment the voting counter
            if (greenAgentsList[i].getVotingOpinion()) {
                greenYes++;
            } else {
                greenNo++;
            }

            uncertaintyTotal += greenAgentsList[i].getUncertainty();
        }

        uncertaintyTotal /= greenAgentCount;

        System.out.printf("There are %d agents, %d voting and %d not voting. The average uncertainty is %.4f\n", greenAgentCount, greenYes, greenNo, uncertaintyTotal);
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

    /**
     * Prints out the blue agent energy level status
     */
    public void printBlueEnergyLevel() {
        System.out.println(String.format("Blue Agent energy level is at %.2f%%.", blueAgent.getEnergyLevel()));
    }

    /**
     * Prints out the red agent follower count
     */
    public void printRedFollowerCount() {
        System.out.println(String.format("Red Agent follower count is %d.", redAgent.getFollowerCount()));
    }

    /**
     * Returns true if blue agent wins when game ends.
     * Blue agent wins when there is a higher number of green agents voting with uncertainty less than 0.
     * Returns false if red agent wins when game ends.
     * Red agent wins when there is a higher number of green agents not voting with uncertainty less than 0.
     * @return True if blue agent wins when game ends, false if red agent wins when game ends.
     */
    public boolean blueWins() {
        int greenYes = 0;   //Number of green agents who ARE voting with uncertainty less than 0
        int greenNo = 0;    //Number of green agents who ARE NOT voting with uncertainty less than 0
        
        for (int i = 0; i < greenAgentCount; i++) {
            //Increment the voting counter
            if (greenAgentsList[i].getVotingOpinion() && greenAgentsList[i].getUncertainty() < 0) {
                greenYes++;
            }
            if (!greenAgentsList[i].getVotingOpinion() && greenAgentsList[i].getUncertainty() < 0) {
                greenNo++;
            }
        }
        return (greenYes > greenNo);
    }
}