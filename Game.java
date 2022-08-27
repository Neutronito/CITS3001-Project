import org.jgrapht.generate.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Game {

    private SimpleWeightedGraph<Integer, DefaultWeightedEdge> greenNetwork;

    /**
     * Creates an instance of the game using the input parameters
     * @param greenNodeCount The number of green nodes to exist in the nework
     * @param probabilityOfConnection The probability of a connection between two green nodes
     * @param greyCount The number of grey nodes
     * @param greyEvilProportion The proportion of grey nodes on the red team, as a percentage
     * @param greenVotePercent The proportion of green nodes who wish to vote (at the start of the game) as a percentage
     */
    public Game(int greenNodeCount, double probabilityOfConnection, int greyCount, double greyEvilProportion, double greenVotePercent) {

        //Use the jgraph library to create a graph generator that uses Erdos Renyi Model
        GnpRandomGraphGenerator<Integer,DefaultWeightedEdge> greenNetworkGraph = new GnpRandomGraphGenerator<>(greenNodeCount, probabilityOfConnection);

        CompleteGraphGenerator<Integer, DefaultWeightedEdge> completeGenerator = new CompleteGraphGenerator<>(greenNodeCount);
        
        //Init the graph
        greenNetwork = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        

        //Use the generator to make the graph
        //greenNetworkGraph.generateGraph(greenNetwork);
        completeGenerator.generateGraph(greenNetwork);

        //Debugging lemme have a look through this graph
        for (int i = 0; i < greenNodeCount; i++) {
            for (int j = 0; j < greenNodeCount; j++) {
                System.out.println("We are currently looking at path (" + i + ", " + j + ") and the weight is " + greenNetwork.getEdgeWeight(greenNetwork.getEdge(i, j)));
            }
        }
    }

    public static void main(String[] args) {
        Game curGame = new Game(10, 0.4, 0, 0.0, 10.0);
    }

}

