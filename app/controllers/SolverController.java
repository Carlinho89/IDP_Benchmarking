package controllers;

import models.SolverQuery;
import workpackage.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 16/05/16.
 */
public class SolverController {
    private Scenario scenario;
    private List<Integer> inputs;
    private List<Integer> outputs;
    private int league;
    private boolean orientation;
    private boolean superEfficiency;
    private int start;
    private int seasons;

    public SolverController(){
        scenario = null;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        orientation = superEfficiency = false;
        start = seasons = league = -1;
    }

    public Scenario solve(SolverQuery query){
        inputs  = query.getSelectedInputs();
        outputs = query.getSelectedOutputs();
        league = query.getLeagueID();
        orientation = false;
        superEfficiency = false;
        start = query.getSeason();
        seasons = 1;

        try {
//            List<Integer> selectionOffIn = new ArrayList<Integer>();
//            selectionOffIn.add(14);
//            selectionOffIn.add(16);
//            selectionOffIn.add(7);
//            selectionOffIn.add(13);
//
//
//            List<Integer> selectionOffOut = new ArrayList<Integer>();
//            selectionOffOut.add(4);
//            Scenario garciaSanchez = new Scenario(league, selectionOffIn, selectionOffOut, orientation, superEfficiency, start, seasons);
            this.scenario = new Scenario(league, inputs, outputs, orientation, superEfficiency, start, seasons);



        }catch (Exception e){
            e.printStackTrace();
            scenario = null;
        }finally {
            return this.scenario;
        }

    }
    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario sc) {
        this.scenario = sc;
    }

    public List<Integer> getInputs() {
        return inputs;
    }

    public void setInputs(List<Integer> inputs) {
        this.inputs = inputs;
    }

    public List<Integer> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Integer> outputs) {
        this.outputs = outputs;
    }

    public int getLeague() {
        return league;
    }

    public void setLeague(int league) {
        this.league = league;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public boolean isSuperEfficiency() {
        return superEfficiency;
    }

    public void setSuperEfficiency(boolean superEfficiency) {
        this.superEfficiency = superEfficiency;
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
}
