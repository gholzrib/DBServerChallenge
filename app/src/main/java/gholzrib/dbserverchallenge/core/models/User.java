package gholzrib.dbserverchallenge.core.models;

import java.util.ArrayList;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class User {

    private Integer id;
    private String name;

    private ArrayList<String> votesOfTheDay = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getVotesOfTheDay() {
        return votesOfTheDay;
    }

    public void setVotesOfTheDay(ArrayList<String> votesOfTheDay) {
        this.votesOfTheDay = votesOfTheDay;
    }
}
