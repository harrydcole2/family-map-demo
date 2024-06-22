package result;

/**
 * Class for /clear output
 */
public class ClearResult extends Result{

    /**
     * constructor for ClearResult to serialize into Json
     *
     * @param message describes success or failure
     * @param success whether successful or not
     */
    public ClearResult(String message, boolean success) {
        super(message,success);
    }
}
