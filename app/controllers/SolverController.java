package controllers;

import ilog.concert.IloException;
import models.DEAWrapper;
import models.SolverComplexQuery;
import models.SolverSimpleQuery;
import models.SolverSimpleQueryMQI;
import workpackage.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    // Solving vars
    ArrayList<String[]> st1dmuList = null;      //List of used DMUs
    ArrayList<String[]> st2dmuList = null;      //List of used DMUs
    ArrayList<String[]> st3dmuList = null;      //List of used DMUs

    ArrayList<double[][]> overList = null;   //List over overview-arrays from results
    ArrayList<String[]> refList = null;      //List over reference-arrays from results
    ArrayList<double[][]> paraValues = null; //List containing the data used
    ArrayList<DEA> deaList = null;
    private ArrayList<double[][]> st1solList;   //List to save solutions of different stages
    private ArrayList<double[][]> st2solList;
    private ArrayList<double[][]> st3solList;

    public SolverController(){
        scenario = null;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        leagues = new ArrayList<>();
        solvedScenarioMQI = null;
        orientation = superEfficiency = data = false;
        start = seasons = league = scale = -1;
        st1solList = st2solList = st3solList = null;

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
        start = query.getStart();
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
        //Complex Query variables
        ArrayList<DEAWrapper> stage1DEA = query.getStage1DEA();
        ArrayList<DEAWrapper> stage2DEA = query.getStage2DEA();
        ArrayList<DEAWrapper> stage3DEA = query.getStage3DEA();

        st1solList = new ArrayList<>();
        st2solList = new ArrayList<>();
        st3solList = new ArrayList<>();

        //Cplex variables
        CplexController connection = new CplexController();

        String dmu[] = null;

        double[][] input;
        double[][] output;
        double[][] paraIn;    //Arrays for saving parameter values
        double[][] paraOut;

        String method = query.getSelectedMethod();

        dmu = connection.createDMUArray(query.getLeagueID(), query.getSeason());

        for (DEAWrapper dea:stage1DEA){

            paraIn = null;    //Arrays for saving parameter values
            paraOut = null;
            st1dmuList = new ArrayList<>();

            //Create parameter-arrays to work from
            input = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedInputs());
            output = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedOutputs());

            st1dmuList.add(dmu);   //Add DMU to list for Exceloutput later on
            paraIn = input;
            paraOut = output;

            DEA _dea =  new DEA(dmu, input, output); //Create DEA-object to be solved

            //Solve DEA
            //solve(_dea, dmu, query); //Solves the DEA and updates overview and reference lists
            //deaList.add(_dea);
            double [][] solutionOne = solveDEA(_dea,query.getSelectedMethod(),query.isInputOriented());

            this.st1solList.add(dea.getDeaID(), solutionOne);
            paraValues = new ArrayList<>();  //Unify input and output list
            paraValues.add(paraIn);
            paraValues.add(paraOut);
        }

        for (DEAWrapper dea:stage2DEA){
            //clearing
            st2dmuList = new ArrayList<>();
            paraIn = null;
            paraOut = null;

            //Stage 2 DEA that will be solved
            DEA _dea = null;

            //Create parameter-arrays to work from
            input = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedInputs());
            output = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedOutputs());

            st2dmuList.add(dmu);   //Add DMU to list for Exceloutput later on
            paraIn = input;
            paraOut = output;


            ArrayList<Integer> prevDEAsIDs = dea.getPreviousResults();
            HashMap<Integer, double[][]> prevDEAsSols = new HashMap<>();

            for (int deaID:prevDEAsIDs) {
                double [][] solution = st1solList.get(deaID);
                prevDEAsSols.put(deaID, solution);
            }

            double[][] solutionOne = (prevDEAsSols.size()>0)? prevDEAsSols.get(1):null;

            _dea = fitInputsForStage(dea,dmu,input,output,prevDEAsSols);

            double [][] solutionTwo = solveDEA(_dea, query.getSelectedMethod(), query.isInputOriented());
            double[][]solutionComplete = createSolutionTwo(solutionOne, solutionTwo);  //Create a single solution array containing weights and efficiencies.

            st2solList.add(solutionComplete);
        }

        for (DEAWrapper dea:stage3DEA){
            //clearing
            st3dmuList = new ArrayList<>();

            //Stage 3 DEA that will be solved
            DEA _dea = null;

            //Create parameter-arrays to work from
            input = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedInputs());
            output = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedOutputs());

            st3dmuList.add(dmu);   //Add DMU to list for Exceloutput later on
            paraIn = input;
            paraOut = output;

            double [][]effOneFitted = null;
            ArrayList<Integer> prevDEAsIDs = dea.getPreviousResults();
            HashMap<Integer, double[][]> prevDEAsSols = new HashMap<>();

            for (int deaID:prevDEAsIDs) {
                double [][] solution = st2solList.get(deaID);
                prevDEAsSols.put(deaID, solution);
            }
            _dea = fitInputsForStage(dea,dmu,input,output,prevDEAsSols);

            double[][] solutionTwo = (prevDEAsSols.size()>0)? prevDEAsSols.get(0):null;

            double [][] solutionThree = solveDEA(_dea, query.getSelectedMethod(), query.isInputOriented());
            double[][]solutionComplete = createSolutionThree(solutionTwo, solutionThree);  //Create a single solution array containing weights and efficiencies.

            st3solList.add(solutionComplete);
        }
        return solvedScenario;
    }

    private DEA fitInputsForStage(DEAWrapper dea, String[] dmu, double[][] input ,double[][] output, HashMap<Integer, double[][]> prevDEAsSols){
        DEA _dea = null;
        double[][] solutionOne = null;

        if(dea.getSelectedInputs().size() > 0 && dea.getPreviousResults().size() == 0){ // new inputs, no 1stage Dea eff
            _dea = new DEA(dmu, input, output);
        }
        else if(dea.getSelectedInputs().size() == 0 && dea.getPreviousResults().size() > 0){ // no new inputs, only 1stage Dea eff
            solutionOne = prevDEAsSols.get(dea.getPreviousResults().get(0));
            double[]effOne = Evaluation.getEfficiency(solutionOne); //Fit efficiency to fullfill requirement of double[][]
            double [][]effOneFitted = fitEfficiency(effOne);
            _dea = new DEA(dmu, effOneFitted, output);
        }
        else if(dea.getSelectedInputs().size() > 0 && dea.getPreviousResults().size() > 0){ // new inputs + prev stage Dea eff
            solutionOne = prevDEAsSols.get(dea.getPreviousResults().get(0));

            double[][] combInter = combineEffInter(Evaluation.getEfficiency(solutionOne), dmu, input);
            _dea = new DEA(dmu, combInter, output);
        }
        else {
            System.out.println("SOMETHING WENT HORRIBLY WRONG WHILE COMPUTING STAGE 2!");
        }

        return _dea;
    }
    // Solving Algorithms
    private double [][] solveDEA(DEA _dea, String method, boolean isInputOriented){
        double [][] solutionOne = null;
        try {
            if(method.equals("CCR") == true && isInputOriented)  //Input-Orientation
                solutionOne = _dea.solve_Dual_Basic_Input(false);    //Solve stage one
            else if(method.equals("CCR") == true && !isInputOriented)  //Output-Orientation
                solutionOne = _dea.solve_Dual_Basic_Output(false);    //Solve stage one
            else if(method.equals("BCC") == true && isInputOriented)  //Input-Orientation
                solutionOne = _dea.solve_Dual_Basic_Input(true);    //Solve stage one
            else if(method.equals("BCC") == true && !isInputOriented)  //Output-Orientation
                solutionOne = _dea.solve_Dual_Basic_Output(true);    //Solve stage one
            else if(method.equals("SBM") == true && !isInputOriented) //Input-Orientation
                solutionOne = _dea.solve_Dual_SBM_Input();    //Solve stage one
            else if(method.equals("SBM") == true && !isInputOriented) //Output-Orientation
                solutionOne = _dea.solve_Dual_SBM_Output();    //Solve stage one
        }
        catch (IloException ex) {
            Logger.getLogger(TwoStageDEA.class.getName()).log(Level.SEVERE, null, ex);
            solutionOne = null;
        }
        finally {
            return  solutionOne;
        }

    }

    private double[][] fitEfficiency(double[]eff)    //Method to make a 2-dimenstional array out of the efficiency in order to use it as input for stage 2
    {
        double[][]fitted = new double[1][eff.length];   //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU
        System.arraycopy(eff, 0, fitted[0], 0, eff.length);
        return fitted;
    }

    private double[][] combineEffInter(double[]eff, String[] dmu, double[][] inter)  //Method to create a single, 2-dimensional array out of efficiency and intermediary products
    {
        double[][]combined = new double[inter.length + 1][dmu.length];  //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU

        for(int i = 0; i < combined.length; i++)    //Iteration over parameters
            for(int j = 0; j < combined[i].length; j++)  //Iteration over DMU-values
                if(i < (combined.length -1))    //Within borders of inter-array
                    combined[i][j] = inter[i][j];
                else                                //Outside borders of inter-array, i.e. put in efficiency
                    combined[i][j] = eff[j];
        return combined;
    }


    private void solve(DEA dea, String dmu[], SolverComplexQuery query)    {
        //Method that solves a DEA according to CCR and BCC, creates scale efficiency and puts things in an overview-array
        double[][] overview = null;     //Array to save efficiency results
        String[] reference = null;      //Array for reference units. Reference units are pulled from the BCC results
        try {
            if(query.isInputOriented()) {
                double[][]solCCR = dea.solve_Dual_Basic_Input(false);
                double[][]solBCC = dea.solve_Dual_Basic_Input(true);
                double[][]solSBM = dea.solve_Dual_SBM_Input();

                double[]effCCR = Evaluation.getEfficiency(solCCR);
                double[]effBCC = Evaluation.getEfficiency(solBCC);
                double[]effSBM = Evaluation.getEfficiency(solSBM);

                if(superEfficiency == true)
                {
                    solCCR = dea.solve_Radial_SuperEff_Input(effCCR, false);
                    double[][]solSuperBCC = dea.solve_Radial_SuperEff_Input(effBCC, true); //Separate Array to avoid confusing reference units
                    solSBM = dea.solve_SBM_SuperEff_Input(effSBM);

                    for(int i = 0; i < effCCR.length; i++)  //Write Superefficiency into normal efficiency arrays
                    {
                        if(effCCR[i] == 1)
                            effCCR[i] = solCCR[i][dmu.length];
                        if(effBCC[i] == 1)
                            effBCC[i] = solSuperBCC[i][dmu.length];
                        if(effSBM[i] == 1)
                            effSBM[i] = solSBM[i][dmu.length];
                    }
                }
                double[]scale = Evaluation.createScaleEfficiency(effCCR, effBCC);

                overview = Evaluation.createOverview(effCCR, effBCC, scale, effSBM);
                reference = Evaluation.createReferenceSetDual(dmu, solBCC);
            }
            else
            {
                double[][]solCCR = dea.solve_Dual_Basic_Output(false);
                double[][]solBCC = dea.solve_Dual_Basic_Output(true);
                double[][]solSBM = dea.solve_Dual_SBM_Output();

                double[]effCCR = Evaluation.getEfficiency(solCCR);
                double[]effBCC = Evaluation.getEfficiency(solBCC);
                double[]effSBM = Evaluation.getEfficiency(solSBM);

                if(superEfficiency == true)
                {
                    solCCR = dea.solve_Radial_SuperEff_Output(effCCR, false);
                    double[][]solSuperBCC = dea.solve_Radial_SuperEff_Output(effBCC, true); //Separate Array to avoid confusing reference units
                    solSBM = dea.solve_SBM_SuperEff_Output(effSBM);

                    for(int i = 0; i < effCCR.length; i++)  //Write Superefficiency into normal efficiency arrays
                    {
                        if(effCCR[i] == 1)
                            effCCR[i] = solCCR[i][dmu.length];
                        if(effBCC[i] == 1)
                            effBCC[i] = solSuperBCC[i][dmu.length];
                        if(effSBM[i] == 1)
                            effSBM[i] = solSBM[i][dmu.length];
                    }
                }
                double[]scale = Evaluation.createScaleEfficiency(effCCR, effBCC);

                overview = Evaluation.createOverview(effCCR, effBCC, scale, effSBM);
                reference = Evaluation.createReferenceSetDual(dmu, solBCC);
            }
        } catch (IloException ex) {
            System.out.println("Error solving for CCR, BCC or SBM efficiency");
            Logger.getLogger(Scenario.class.getName()).log(Level.SEVERE, null, ex);
        }
        overList.add(overview); //Add to list of result-arrays
        refList.add(reference); //Add to list of result-arrays
    }

    private double[][] createSolutionTwo(double[][]solutionOne, double[][]solutionTwo)
    {
        double[][] solution = new double[solutionOne.length + solutionTwo.length][solutionOne[0].length];

        for(int i = 0; i < solutionOne.length; i++)
            for(int j = 0; j < solutionOne[i].length; j++)
            {
                solution[i][j] = solutionOne[i][j];
                solution[i + solutionOne.length][j] = solutionTwo[i][j];
            }
        return solution;
    }
    private double[][]createSolutionThree(double[][]solutionOne, double[][]solutionTwo)
    {
        double[][] solution = new double[solutionOne.length + solutionTwo.length][solutionOne[0].length];

        for(int i = 0; i < solutionOne.length; i++)
            System.arraycopy(solutionOne[i], 0, solution[i], 0, solutionOne[i].length);
        for(int i = solutionOne.length; i < solution.length; i++)
            System.arraycopy(solutionTwo[i - solutionOne.length], 0, solution[i], 0, solutionTwo[i - solutionOne.length].length);
        return solution;
    }

    // Getters and Setters
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
