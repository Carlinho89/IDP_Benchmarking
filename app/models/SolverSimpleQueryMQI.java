package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 08/07/2016.
 */
public class SolverSimpleQueryMQI {
    private List<Integer> league;
    private List<Integer> input;
    private List<Integer> output;
    private boolean orientation;
    private boolean data;
    private int start;
    private int seasons;
    private int scale;


    /*
    *TO DO: enrico fill this with the data coming from the post form
    * */
    public SolverSimpleQueryMQI(String query){
        league = new ArrayList<>();
        input = new ArrayList<>();
        output = new ArrayList<>();
        orientation = data = false;
        scale = start = seasons = -1;
    }

    public List<Integer> getLeague() {
        return league;
    }

    public void setLeague(List<Integer> league) {
        this.league = league;
    }

    public List<Integer> getInput() {
        return input;
    }

    public void setInput(List<Integer> input) {
        this.input = input;
    }

    public List<Integer> getOutput() {
        return output;
    }

    public void setOutput(List<Integer> output) {
        this.output = output;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSeasons() {
        return seasons;
    }

    public void setSeasons(int seasons) {
        this.seasons = seasons;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }


}
