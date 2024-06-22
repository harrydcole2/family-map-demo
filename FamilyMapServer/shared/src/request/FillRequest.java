package request;

/**
 * Class for /fill/[username]/{generations} input
 */
public class FillRequest extends Request {
    /**
     * username parsed from URL
     */
    private String username;
    /**
     * generations optionally parsed from URL (possible null)
     */
    private int generations;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }


}
