package result;

import model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /clear output
 */
public class EventResult extends Result{

    /**
     * array of Events received to parse into Json
     */
    private List<Event> data;
    public EventResult(String message, boolean success, ArrayList<Event> data) {
        super(message, success);
        this.data = data;
    }

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }
}
