package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import ilog.concert.IloException;
import models.DEAWrapper;
import models.SolverComplexQuery;
import models.SolverSimpleQuery;
import play.libs.Json;
import workpackage.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by carlodidomenico on 16/05/16.
 */
public class SolverController {
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

        st1solList = st2solList = st3solList = null;

    }
/*
* This method realizes the simple solver solution
* */
    public JsonNode solve(SolverSimpleQuery query){
        SimpleSolutionModel model;

        String[] dmu = null;
        CplexController connection = new CplexController();  //Establish connection to database

        double[][]input = null;
        double[][]output = null;
        double[][]paraIn = null;    //Arrays for saving parameter values
        double[][]paraOut = null;

        overList = new ArrayList<>();
        refList = new ArrayList<>();
        deaList = new ArrayList<>();

        dmu = connection.createDMUArray(query.getLeagueID(), query.getSeason());

        input = connection.createParameterArray(query.getLeagueID(), query.getSeason(), query.getSelectedInputs());
        output = connection.createParameterArray(query.getLeagueID(), query.getSeason(), query.getSelectedOutputs());

        paraIn = input;
        paraOut = output;
        paraOut = ExcelOutput.combineOverviewStage(paraOut, output);
        DEA dea =  new DEA(dmu, input, output);

        deaList.add(dea);
        model = solveSingleStageDEA(dea,dmu,query.isInputOriented(),query.isSuperEff(),query.getSelectedInputsNames(),query.getSelectedOutputsNames()); //Solves the DEA and updates overview and reference lists
        JsonNode node = Json.toJson(model);

        return node;

    }


    public JsonNode solve(SolverComplexQuery query) {
        ComplexSolutionModel model;
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
            String[] inputNames = null;
            String[] outputNames = null;

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
            double [][] solutionOne = solveDEA(_dea,query.getSelectedMethod(),query.isInputOriented());

            this.st1solList.add(dea.getDeaID(), solutionOne);

        }

        for (DEAWrapper dea:stage2DEA){
            //clearing
            st2dmuList = new ArrayList<>();
            paraIn = null;
            paraOut = null;
            double[][]solutionStageTwo = null;

            //Stage 2 DEA that will be solved
            DEA _dea = null;
            //Add DMU to list for Exceloutput later on
            st2dmuList.add(dmu);
            //Create parameter-arrays to work from

            input = (dea.getSelectedInputs().size() == 0)? null:connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedInputs());
            output = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedOutputs());

            paraIn = input;
            paraOut = output;
            solutionStageTwo = solveMultiStage(input,output,dmu,dea,query.getSelectedMethod());

            this.st2solList.add(dea.getDeaID(), solutionStageTwo);

        }

        for (DEAWrapper dea:stage3DEA){
            //clearing
            st3dmuList = new ArrayList<>();
            paraIn = null;
            paraOut = null;
            double[][]solutionStageThree = null;

            //Stage 2 DEA that will be solved
            DEA _dea = null;
            //Add DMU to list for Exceloutput later on
            st3dmuList.add(dmu);
            //Create parameter-arrays to work from
            input = (dea.getSelectedInputs().size() == 0)? null:connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedInputs());
            output = connection.createParameterArray(query.getLeagueID(), query.getSeason(), dea.getSelectedOutputs());

            paraIn = input;
            paraOut = output;
            solutionStageThree = solveMultiStage(input,output,dmu,dea,query.getSelectedMethod());

            this.st3solList.add(dea.getDeaID(), solutionStageThree);
        }
        ArrayList<double[]> st1eff = new ArrayList<>();
        ArrayList<double[]> st2eff = new ArrayList<>();
        ArrayList<double[]> st3eff = new ArrayList<>();

        for (double[][]sol:st1solList)
            st1eff.add(st1solList.indexOf(sol), Evaluation.getEfficiency(sol));
        for (double[][]sol:st2solList)
            st2eff.add(st2solList.indexOf(sol), Evaluation.getEfficiency(sol));
        for (double[][]sol:st3solList)
            st3eff.add(st3solList.indexOf(sol), Evaluation.getEfficiency(sol));

        model = new ComplexSolutionModel(st1solList,st1eff,st2solList,st2eff,st3solList,st3eff,query.getSelectedMethod(),dmu,query.getSelectedInputsNames(),query.getSelectedOutputsNames());

        JsonNode node = Json.toJson(model);

        return node;
    }

    /**
     * This method solves a single stage DEA with CCR, BCC, SBM solution methods
     * @param dea
     * @param dmu
     * @param orientation
     * @param superEfficiency
     * @param selectedInputsNames
     *@param selectedOutputsNames @return
     */
    private SimpleSolutionModel solveSingleStageDEA(DEA dea, String[] dmu, boolean orientation, boolean superEfficiency, ArrayList<String> selectedInputsNames, ArrayList<String> selectedOutputsNames)    {
        //Method that solves a DEA according to CCR and BCC, creates scale efficiency and puts things in an overview-array
        double[][] overview = null;     //Array to save efficiency results
        String[] reference = null;      //Array for reference units. Reference units are pulled from the BCC results

        HashMap<String,double[][]> solutions = new HashMap<>();
        HashMap<String,double[]> efficencies = new HashMap<>();


        double[][]solCCR = null;
        double[][]solBCC = null;
        double[][]solSBM = null;

        double[]effCCR = null;
        double[]effBCC = null;
        double[]effSBM = null;

        SimpleSolutionModel model = null;
        try {
            if(orientation == true)
            {

                solCCR = dea.solve_Dual_Basic_Input(false);
                solBCC = dea.solve_Dual_Basic_Input(true);
                solSBM = dea.solve_Dual_SBM_Input();

                effCCR = Evaluation.getEfficiency(solCCR);
                effBCC = Evaluation.getEfficiency(solBCC);
                effSBM = Evaluation.getEfficiency(solSBM);

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
                solCCR = dea.solve_Dual_Basic_Output(false);
                solBCC = dea.solve_Dual_Basic_Output(true);
                solSBM = dea.solve_Dual_SBM_Output();

                effCCR = Evaluation.getEfficiency(solCCR);
                effBCC = Evaluation.getEfficiency(solBCC);
                effSBM = Evaluation.getEfficiency(solSBM);

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
        solutions.put("BCC", solBCC);
        solutions.put("CCR", solCCR);
        solutions.put("SBM", solSBM);

        efficencies.put("BCC",effBCC);
        efficencies.put("CCR",effCCR);
        efficencies.put("SBM",effSBM);

        model = new SimpleSolutionModel(solutions,efficencies,selectedInputsNames,selectedOutputsNames,overview,reference,dmu);

        return model;
    }

    /**
     * Solves Multistage DEA
     *
     * @param input  DEA's inputs
     * @param output DEA's outputs
     * @param dmu    DEA's dmu
     * @param dea    DEA's parameters chosen by the user
     * @param method DEA's solution method
     * @return  computed solution
     */
    private double[][] solveMultiStage(double[][] input, double[][] output, String[] dmu, DEAWrapper dea, String method) {
        DEA _dea = null;
        double [][]solutionComplete = null;

        // No DEA eff as input
        if (!(dea.getPreviousResults().size() > 0)){
            // Only New Input
            if(dea.getSelectedInputs().size() > 0) {
                _dea = new DEA(dmu,input,output);
                solutionComplete = solveDEA(_dea, method, dea.isInputOriented());
            }
        }
        // DEA eff as input
        else{ //if (dea.getPreviousResults().size() > 0){
            HashMap<Integer, double[][]> prevDEAsSols = getPrevDEAEff(dea.getPreviousResults());
            double[][]combEff = null;

            if(dea.getSelectedInputs().size() > 0){
                combEff = fitPrevDEAsEff(prevDEAsSols,dmu.length,input);
                _dea = new DEA(dmu,combEff,output);
                solutionComplete = solveDEA(_dea,method,dea.isInputOriented());
            }
            // No New input
            else{ //if(dea.getSelectedInputs().size() <= 0){
                double[][]firstSol = prevDEAsSols.get(0);
                double[]eff = Evaluation.getEfficiency(firstSol);
                double[][]fitEff = fitEfficiency(eff);

                prevDEAsSols.remove(0);

                if (prevDEAsSols.size()>0)
                    combEff = fitPrevDEAsEff(prevDEAsSols,dmu.length,fitEff);
                else
                    combEff = fitEff;
                _dea = new DEA(dmu, combEff, output);
                solutionComplete = solveDEA(_dea,method,dea.isInputOriented());
            }
        }

        return solutionComplete;
    }

    /**
     * Function that gets the previous stage's solutions
     * @param previousResults Prev stage's IDs
     * @return prev stage solutions
     */
    private HashMap<Integer, double[][]> getPrevDEAEff(ArrayList<Integer> previousResults) {
        ArrayList<Integer> prevDEAsIDs = previousResults;
        HashMap<Integer, double[][]> prevDEAsSols = new HashMap<>();
        // Getting values from prec solved DEAs
        for (int deaID:prevDEAsIDs) {
            double [][] solution = st1solList.get(deaID);
            prevDEAsSols.put(deaID, solution);
        }
        return  prevDEAsSols;
    }

    /**
     * Fitting Prev DEAs' Efficiencies with the input
     * @param prevDEAsSols
     * @param dmuLenght
     * @param input
     * @return
     */
    private double[][] fitPrevDEAsEff(HashMap<Integer,double[][]> prevDEAsSols, int dmuLenght, double[][]input) {

        double[] prevDEAEff = null;
        double[][] prevFittedEff = input;
        double[][] combDEAEff = null;
        boolean firstTime = true;

        for (double[][] sol : prevDEAsSols.values()){
            combDEAEff = combineEffInter(Evaluation.getEfficiency(sol), dmuLenght, prevFittedEff);
            prevFittedEff = combDEAEff;
        }

        return combDEAEff;
    }

    /**
     * Calculating DEA's solution
     * @param _dea      DEA to solve
     * @param method    Solution Method - CCR
     *                                  - BCC
     *                                  - SBM
     *
     * @param isInputOriented
     * @return  computed solution
     */
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

    /**
     * Method to make a 2-dimenstional array out of the efficiency in order to use it as input for stage 2
     * @param eff
     * @return
     */
    private double[][] fitEfficiency(double[]eff)
    {
        double[][]fitted = new double[1][eff.length];   //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU
        System.arraycopy(eff, 0, fitted[0], 0, eff.length);
        return fitted;
    }

    /**
     * //Method to create a single, 2-dimensional array out of efficiency and intermediary products
     * @param eff
     * @param dmuLength
     * @param inter
     * @return
     */
    private double[][] combineEffInter(double[]eff, int dmuLength, double[][] inter)
    {
        double[][]combined = new double[inter.length + 1][dmuLength];  //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU

        for(int i = 0; i < combined.length; i++)    //Iteration over parameters
            for(int j = 0; j < combined[i].length; j++)  //Iteration over DMU-values
                if(i < (combined.length -1))    //Within borders of inter-array
                    combined[i][j] = inter[i][j];
                else                                //Outside borders of inter-array, i.e. put in efficiency
                    combined[i][j] = eff[j];
        return combined;
    }

    private double[][] copy2DArray(double[][]matrix){
        double [][] myInt = new double[matrix.length][];
        for(int i = 0; i < matrix.length; i++)
            myInt[i] = matrix[i].clone();
        return myInt;
    }

    // JSON Models
    private class ComplexSolutionModel {
        public final ArrayList<double[][]> stage1Solutions;
        public final ArrayList<double[]> stage1Eff;

        public final ArrayList<double[][]> stage2Solutions;
        public final ArrayList<double[]> stage2Eff;

        public final ArrayList<double[][]> stage3Solutions;
        public final ArrayList<double[]> stage3Eff;

        public final String solutionMethod;
        public final String[] dmu;
        public final ArrayList<String> inputs;
        public final ArrayList<String> outputs;

        public ComplexSolutionModel(ArrayList<double[][]> stage1Solutions, ArrayList<double[]> stage1Eff, ArrayList<double[][]> stage2Solutions, ArrayList<double[]> stage2Eff, ArrayList<double[][]> stage3Solutions, ArrayList<double[]> stage3Eff, String solutionMethod, String[] dmu, ArrayList<String> inputs, ArrayList<String> outputs) {
            this.stage1Solutions = stage1Solutions;
            this.stage1Eff = stage1Eff;
            this.stage2Solutions = stage2Solutions;
            this.stage2Eff = stage2Eff;
            this.stage3Solutions = stage3Solutions;
            this.stage3Eff = stage3Eff;
            this.solutionMethod = solutionMethod;
            this.dmu = dmu;
            this.inputs = inputs;
            this.outputs = outputs;
        }
    }
    private class SimpleSolutionModel {
        public final HashMap<String, double[][]> solutions;
        public final HashMap<String, double[]> efficiencies;

        public final ArrayList<String> inputs;
        public final ArrayList<String> outputs;

        public final double[][] overview;     //Array to save efficiency results
        public final String[] reference;
        public final String[] dmu;

        public SimpleSolutionModel(HashMap<String, double[][]> solutions, HashMap<String, double[]> efficencies, ArrayList<String> inputs, ArrayList<String> outputs, double[][] overview, String[] reference, String[] dmu) {
            this.solutions = solutions;
            this.efficiencies = efficencies;
            this.inputs = inputs;
            this.outputs = outputs;
            this.overview = overview;
            this.reference = reference;
            this.dmu = dmu;
        }
    }
}
