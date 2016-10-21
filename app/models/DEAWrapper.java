package models;

import java.util.ArrayList;

/**
 * Created by carlodidomenico on 23/09/2016.
 */
public class DEAWrapper {
    // id of selectedInputs parameters on DB
    private ArrayList<Integer> selectedInputs;
    // id of DEA efficiency to use as parameter
    // -1 Not to be used;
    // %d id of DEA efficiency
    private ArrayList<Integer> previousResults;
    // id of selectedOutputs patameters on DB
    private ArrayList<Integer> selectedOutputs;
    //DEA stage
    private int stage;
    //Input or Output oriented
    private boolean inputOriented;
    //Superefficiency
    private boolean supereff;
    //DEA's Id
    private int deaID;

    public DEAWrapper(ArrayList<Integer> selectedInputs, ArrayList<Integer> previousResults, ArrayList<Integer> selectedOutputs, int stage, boolean inputOriented, int deaID) {
        this.selectedInputs = selectedInputs;
        this.previousResults = previousResults;
        this.selectedOutputs = selectedOutputs;
        this.stage = stage;
        this.inputOriented = inputOriented;
        this.deaID = deaID;
    }

    public ArrayList<Integer> getSelectedInputs() {
        return selectedInputs;
    }

    public void setSelectedInputs(ArrayList<Integer> selectedInputs) {
        this.selectedInputs = selectedInputs;
    }

    public ArrayList<Integer> getSelectedOutputs() {
        return selectedOutputs;
    }

    public void setSelectedOutputs(ArrayList<Integer> selectedOutputs) {
        this.selectedOutputs = selectedOutputs;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public boolean isInputOriented() {
        return inputOriented;
    }

    public void setInputOriented(boolean inputOriented) {
        this.inputOriented = inputOriented;
    }

    public boolean isSupereff() {
        return supereff;
    }

    public void setSupereff(boolean supereff) {
        this.supereff = supereff;
    }

    public int getDeaID() {
        return deaID;
    }

    public void setDeaID(int deaID) {
        this.deaID = deaID;
    }

    public ArrayList<Integer> getPreviousResults() {
        return previousResults;
    }

    public void setPreviousResults(ArrayList<Integer> previousResults) {
        this.previousResults = previousResults;
    }



}
