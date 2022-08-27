import org.jgrapht.generate.*;
import java.util.function.Supplier;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Game {

    private double[][] greenNetwork;
    private int greenNodeCount = 0;

    /**
     * Creates an instance of the game using the input parameters
     * @param greenNodeCount The number of green nodes to exist in the nework
     * @param probabilityOfConnection The probability of a connection between two green nodes
     * @param greyCount The number of grey nodes
     * @param greyEvilProportion The proportion of grey nodes on the red team, as a percentage
     * @param greenVotePercent The proportion of green nodes who wish to vote (at the start of the game) as a percentage
     */
    public Game(int greenNodeCount, double probabilityOfConnection, int greyCount, double greyEvilProportion, double greenVotePercent) {
        //Fill variables
        this.greenNodeCount = greenNodeCount;

        //Use the jgraph library to create a graph generator that uses Erdos Renyi Model
        GnpRandomGraphGenerator<Integer,DefaultWeightedEdge> greenNetworkGenerator = new GnpRandomGraphGenerator<>(greenNodeCount, probabilityOfConnection);
        
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
        greenNetwork = new double[greenNodeCount][greenNodeCount];

        for (int i = 0; i < greenNodeCount; i++) {
            for (int j = 0; j < greenNodeCount; j++) {
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
    }

    /**
     * Print out the green network as an adjacency matrix with all the edge weights
     */
    public void printGreenNetwork() {

        System.out.print("\t");
        for (int i = 0; i < greenNodeCount; i++) {
            System.out.print(i + "\t");
        }
        System.out.println();


        for (int i = 0; i < greenNodeCount; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < greenNodeCount; j++) {
                System.out.print(greenNetwork[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Game curGame = new Game(10, 0.4, 0, 0.0, 10.0);
        curGame.printGreenNetwork();
    }

}

