package controllers;

import ilog.concert.IloException;
import models.SolverComplexQuery;
import models.SolverSimpleQuery;
import models.SolverSimpleQueryMQI;
import workpackage.Scenario;
import workpackage.ScenarioMQI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 16/05/16.
 */
public class SolverController {
    private Scenario scenario;
    private List<ScenarioMQI> solvedScenarioMQI;
    private List<Integer> inputs;
    private List<Integer> outputs;
    private List<Integer> leagues;
    private int league;
    private int scale;
    private boolean orientation;
    private boolean superEfficiency;
    private boolean data;
    private int start;
    private int seasons;

    public SolverController(){
        scenario = null;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        leagues = new ArrayList<>();
        solvedScenarioMQI = null;
        orientation = superEfficiency = data = false;
        start = seasons = league = scale = -1;

    }
/*
* This method realizes the simple solver solution
* */
    public Scenario solve(SolverSimpleQuery query){
        inputs  = query.getSelectedInputs();
        outputs = query.getSelectedOutputs();
        league = query.getLeagueID();
        orientation = false;
        superEfficiency = false;
        start = query.getSeason();
        seasons = 1;

        try {
            this.scenario = new Scenario(league, inputs, outputs, orientation, superEfficiency, start, seasons);

        }catch (Exception e){
            e.printStackTrace();
            scenario = null;
        }finally {
            return this.scenario;
        }

    }

    public List<ScenarioMQI> solve(SolverSimpleQueryMQI query){
        this.solvedScenarioMQI = new ArrayList<ScenarioMQI>();

        inputs = query.getSelectedInputs();
        outputs = query.getSelectedOutputs();
        leagues = query.getLeagueID();
        start = query.getStart();
        data = query.isData();
        scale = query.getScale();
        orientation = query.isOrientation();
        seasons = query.getSeason();



        for(int i = 0; i < leagues.size(); i++)
            try{
                solvedScenarioMQI.add(new ScenarioMQI(leagues.get(i), inputs, outputs, orientation, data, start, seasons, scale));
            }catch(Exception ex){
                solvedScenarioMQI.add(null);
                System.out.println("Failed to compute the case for league " + leagues.get(i));
            }


        return solvedScenarioMQI;
    }

    public Scenario solve(SolverComplexQuery query) throws IloException {
        Scenario solvedScenario = null;
                                      //int league, List<Integer> inputOff, List<Integer> inputDef, List<Integer> outputOff, List<Integer> outputDef, boolean[][]setting, boolean superEff, int start, int seasons
        solvedScenario = new Scenario(query.getLeagueID(),query.getSelectionOffIn(),query.getSelectionDefIn(),query.getSelectionOffOut(),query.getSelectionDefOut(),query.getSelectionAthOut(),query.getSelectionSocOut(),query.getRamifications(),query.isSuperEff(),query.getStart(),query.getSeason());


        return solvedScenario;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    public List<Integer> getLeagues() {
        return leagues;
    }

    public void setLeagues(List<Integer> leagues) {
        this.leagues = leagues;
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
