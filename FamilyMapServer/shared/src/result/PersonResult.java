package result;

import model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonResult extends Result {
    private List<Person> data;
    public PersonResult(String message, boolean success, ArrayList<Person> data) {
        super(message, success);
        this.data = data;
    }

    public List<Person> getData() {
        return data;
    }

    public void setData(List<Person> data) {
        this.data = data;
    }

}
