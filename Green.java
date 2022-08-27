import java.util.Random;

public class Green {

    private boolean opinionOnVoting; //true represents going to vote, false represents not going to vote
    private double confidenceOfOpinion;

    public Green(double uncertaintyLowerBound, double uncertaintyUpperBound) {
        //For random generation a double is not discrete so we will make it an integer
        int uncertaintyLowerBoundInteger = (int)(uncertaintyLowerBound * 1000);
        int uncertaintyUpperBoundInteger = (int)(uncertaintyUpperBound * 1000);

        Random uncertaintyGenerator = new Random();
        confidenceOfOpinion = uncertaintyGenerator.nextInt(uncertaintyUpperBoundInteger - uncertaintyLowerBoundInteger);

        //Map random number so its in between the interval
        confidenceOfOpinion = confidenceOfOpinion / 1000.0 + (uncertaintyLowerBound);

        opinionOnVoting = false;
    }

    
    
}
