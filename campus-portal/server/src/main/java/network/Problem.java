package network;

/** A refused request. The message is shown to the user as it is. */
public class Problem extends RuntimeException {
    public Problem(String message) {
        super(message);
    }
}
