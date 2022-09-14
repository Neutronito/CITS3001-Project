public class RedAI {

    private int messagePotency;
    
    public RedAI() {
        messagePotency = 1;
    }

    public int chooseMessagePotency() {
        return messagePotency;
        /*
         * todo - hi, this currently returns 1 so needa make it choose the
         * potency depending on the opinions and uncertainties of green agents
         */
    }
}