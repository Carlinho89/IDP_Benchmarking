package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 08/07/2016.
 */
public class SolverSimpleQueryMQI {
    private List<Integer> leagueID;
    private List<Integer> selectedInputs;
    private List<Integer> selectedOutputs;
    private boolean orientation;
    private boolean data;
    private int start;
    private int season;
    private int scale;


    /*
    *TO DO: enrico fill this with the data coming from the post form
    * */
    public SolverSimpleQueryMQI(String query){
        leagueID = new ArrayList<>();
        selectedInputs = new ArrayList<>();
        selectedOutputs = new ArrayList<>();
        orientation = data = false;
        scale = start = season = -1;
    }

    public List<Integer> getLeagueID() {
        return leagueID;
    }

    public void setLeagueID(List<Integer> leagueID) {
        this.leagueID = leagueID;
    }

    public List<Integer> getSelectedInputs() {
        return selectedInputs;
    }

    public void setSelectedInputs(List<Integer> selectedInputs) {
        this.selectedInputs = selectedInputs;
    }

    public List<Integer> getSelectedOutputs() {
        return selectedOutputs;
    }

    public void setSelectedOutputs(List<Integer> selectedOutputs) {
        this.selectedOutputs = selectedOutputs;
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

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }


}
