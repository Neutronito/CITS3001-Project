import java.util.Random;

public class dumbAI {
    Random rand;

    public dumbAI() {
        rand = new Random();
    }

    public int chooseMessagePotency(String currentGameHash) {
        return rand.nextInt(5) + 1;
    }

    public int chooseBlueOption(String currentGameHash) {
        return rand.nextInt(1) + 1;
    }

    public void endGame() {

    }

    public void updateRewards(int reward, String mapHash, int previousPotency, int previousOption) {

    }

    public void updateRewards(int reward, String mapHash, int previousPotency) {
        
    }
}
