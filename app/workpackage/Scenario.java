package workpackage;
import controllers.CplexController;
import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Scenario {

    private final String league;          //Which league are we looking at?
    private final CplexController connection;   //Connect to database
    private String[] dmu;           //Array for selected DMUs
    private double[][] input;       //Array for selected inputs
    private double[][] interOne;    //Array of Stage 2 parameters
    private double[][] interTwo;    //Array of Stage 3 parameters
    private double[][] output;      //Array of  output selection (or of final output of Stage 3 in case of a multi-stage approach
    private boolean orientation;   //Boolean to figure out orientation of a single-stage DEA (true: input, false: output)
    private boolean superEfficiency;    //Boolean to figure out whether Super Efficiency is desired or not (true: yes, false: no)
    private boolean [][]settings;   //Settings: orientation[0][], use efficiency of last stage as input [1][], combined input [2][]
    private final int start;              //Starting year of the observations
    private final int seasons;            //Amount of seasons looked at
    private List<String[]>dmuList;  //List of used DMUs
    private List<double[][]>overList;   //List over overview-arrays from results
    private List<String[]>refList;   //List over reference-arrays from results
    private List<double[][]>paraValues; //List containing the data used
    private List<DEA>deaList;  //List of DEAs created while solving the scenario
    private List<TwoStageDEA>twodeaList;  //List of TwoStageDEAs created while solving the scenario
    private List<ThreeStageDEA>threedeaList;  //List of ThreeStageDEAs created while solving the scenario

    public Scenario(String league, List<Integer> inputStr, List<Integer> outputStr, boolean orientation, boolean superEff, int start, int seasons) throws Exception   //Single-Stage constructor
    {
        this.connection = new CplexController();  //Establish connection to database

        //Initialize parameters
        this.league = league;
        this.orientation = orientation;
        this.superEfficiency = superEff;
        this.start = start;
        this.seasons = seasons;
        this.dmuList = new ArrayList<>();
        this.overList = new ArrayList<>();
        this.refList = new ArrayList<>();
        this.deaList = new ArrayList<>();

        double[][]paraIn = null;    //Arrays for saving parameter values
        double[][]paraOut = null;

        for(int i = 0; i < seasons; i++)
        {
            //Create parameter-arrays to work from
            this.dmu = connection.createDMUArray(league);
            this.input = connection.createParameterArray(league, start + i, inputStr);
            this.output = connection.createParameterArray(league, start + i, outputStr);

            if(dmu.length < (3 * (input.length + output.length))) //CHECK: Correct formula?
                throw new Exception("Cooper et al.'s (2007a) law is not fulfilled. Please reduce either the amount of inputs or the amount of outputs.");

            dmuList.add(dmu);   //Add DMU to list for Exceloutput later on
            if(paraIn == null)  //Building matrices showing all inputs and outputs
                paraIn = input;
            else
                paraIn = ExcelOutput.combineOverviewStage(paraIn, input);
            if(paraOut == null)
                paraOut = output;
            else
                paraOut = ExcelOutput.combineOverviewStage(paraOut, output);

            DEA dea =  new DEA(dmu, input, output); //Create DEA-object to be solved

            //Solve DEA
            solve(dea); //Solves the DEA and updates overview and reference lists
            deaList.add(dea);
        }
        //Create Excel Output
        ExcelOutput.createEfficiencyOutput(league, dmuList, overList, refList);

        //Print out data that was used in the analysis


        String[] paraNames = ExcelOutput.combineStringArray(CplexController.ListToCSString(inputStr).split(", "), CplexController.ListToCSString(outputStr).split(", "));
        this.paraValues = new ArrayList<>();  //Unify input and output list
        paraValues.add(paraIn);
        paraValues.add(paraOut);
        ExcelOutput.createDataOutput(league, dmuList, paraNames, paraValues);    }

    public Scenario(String league, List<Integer> inputStr, List<Integer> interOneStr, List<Integer> interTwoStr, List<Integer> outputStr, boolean[][]setting, boolean superEff, int start, int seasons)
    {
        this.connection = new CplexController();  //Establish connection to database

        //Initialize parameters
        this.league = league;
        this.settings = setting;
        this.superEfficiency = superEff;
        this.start = start;
        this.seasons = seasons;
        this.dmuList = new ArrayList<>();
        this.overList = new ArrayList<>();
        this.refList = new ArrayList<>();
        if(interTwoStr == null)
            this.twodeaList = new ArrayList<>();
        else
            this.threedeaList = new ArrayList<>();

        double[][]paraIn = null;    //Arrays for saving parameter values
        double[][]paraIntOne = null;
        double[][]paraIntTwo = null;
        double[][]paraOut = null;

        for(int i = 0; i < seasons; i++)
        {
            //Create parameter-arrays to work from
            this.dmu = connection.createDMUArray(league);
            this.input = connection.createParameterArray(league, start + i, inputStr);
            this.output = connection.createParameterArray(league, start + i, outputStr);
            this.interOne = connection.createParameterArray(league, start + i, interOneStr);
            if(interTwoStr != null)
                this.interTwo = connection.createParameterArray(league, start + i, interTwoStr);

            if(paraIn == null)  //Building matrices showing all inputs and outputs
                paraIn = input;
            else
                paraIn = ExcelOutput.combineOverviewStage(paraIn, input);
            if(paraIntOne == null)
                paraIntOne = interOne;
            else
                paraIntOne = ExcelOutput.combineOverviewStage(paraIntOne, interOne);
            if(interTwoStr != null)    //active only in case of a third stage
            {
                if(paraIntTwo == null)
                    paraIntTwo = interTwo;
                else
                    paraIntTwo = ExcelOutput.combineOverviewStage(paraIntTwo, interTwo);
            }
            if(paraOut == null)
                paraOut = output;
            else
                paraOut = ExcelOutput.combineOverviewStage(paraIn, output);

            dmuList.add(dmu);   //Add DMU to list for Exceloutput later on

            //Establish workflow
            TwoStageDEA twoDea = null;
            ThreeStageDEA threeDea = null;
            if(interTwoStr == null)
            {
                twoDea = new TwoStageDEA(dmu, input, interOne, output, settings);
                solve(twoDea);  //Solve DEA
                twodeaList.add(twoDea);
            }
            else
            {
                threeDea = new ThreeStageDEA(dmu, input, interOne, interTwo, output, settings);
                solve(threeDea);    //Solve DEA
                threedeaList.add(threeDea);
            }
        }
        //Create Excel Output
        ExcelOutput.createEfficiencyOutput(league, dmuList, overList, refList);

        //Print out data that was used in the analysis
        String[] paraNames = ExcelOutput.combineStringArray(CplexController.ListToCSString(inputStr).split(", "), CplexController.ListToCSString(interOneStr).split(", "));
        if(interTwoStr != null)
            paraNames = ExcelOutput.combineStringArray(paraNames, CplexController.ListToCSString(interTwoStr).split(", "));
        paraNames = ExcelOutput.combineStringArray(paraNames, CplexController.ListToCSString(outputStr).split(", "));
        this.paraValues = new ArrayList<>();  //Unify input and output list
        paraValues.add(paraIn);
        paraValues.add(paraIntOne);
        if(interTwoStr != null)
            paraValues.add(paraIntTwo);
        paraValues.add(paraOut);
        ExcelOutput.createDataOutput(league, dmuList, paraNames, paraValues);


    }

    private void solve(DEA dea)    {
        //Method that solves a DEA according to CCR and BCC, creates scale efficiency and puts things in an overview-array
        double[][] overview = null;     //Array to save efficiency results
        String[] reference = null;      //Array for reference units. Reference units are pulled from the BCC results
        try {
            if(orientation == true)
            {

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

    private void solve(TwoStageDEA twoDea)  {
        //Method solving a two-stage-DEA
        double[][]solCCR = twoDea.solveTwoStage("CCR"); //The method returns an array with both stages in it (same format as for a one-stage-solution array)
        double[][]solBCC = twoDea.solveTwoStage("BCC");
        double[][]solSBM = twoDea.solveTwoStage("SBM");

        double[]effCCR = Evaluation.getEfficiency(solCCR);
        double[]effBCC = Evaluation.getEfficiency(solBCC);
        double[]effSBM = Evaluation.getEfficiency(solSBM);

        if(superEfficiency == true)
        {
            solCCR = twoDea.solveTwoStageSuper("CCR", effCCR); //The method returns an array with both stages in it (same format as for a one-stage-solution array)
            double[][]solSuperBCC = twoDea.solveTwoStageSuper("BCC", effBCC);
            solSBM = twoDea.solveTwoStageSuper("SBM", effSBM);

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

        double[][] overview = Evaluation.createOverview(effCCR, effBCC, scale, effSBM); //Array to save efficiency results
        String[] reference = Evaluation.createReferenceSetDual(dmu, twoDea.getSolutionList().get(2));    //Array for reference units. Reference units are pulled from the BCC results
        reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, twoDea.getSolutionList().get(3)));
        overList.add(overview); //Add to list of result-arrays
        refList.add(reference); //Add to list of result-arrays
    }

    private void solve(ThreeStageDEA threeDea)  {
        //Method for properly solving a DEA with three stages
        double[][]solCCR = threeDea.solveThreeStage("CCR"); //The method returns an array with both stages in it (same format as for a one-stage-solution array)
        double[][]solBCC = threeDea.solveThreeStage("BCC");
        double[][]solSBM = threeDea.solveThreeStage("SBM");

        double[]effCCR = Evaluation.getEfficiency(solCCR);
        double[]effBCC = Evaluation.getEfficiency(solBCC);
        double[]effSBM = Evaluation.getEfficiency(solSBM);

        if(superEfficiency == true)
        {
            solCCR = threeDea.solveThreeStageSuper("CCR", effCCR); //The method returns an array with both stages in it (same format as for a one-stage-solution array)
            double[][]solSuperBCC = threeDea.solveThreeStageSuper("BCC", effBCC);
            solSBM = threeDea.solveThreeStageSuper("SBM", effSBM);

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

        double[][] overview = Evaluation.createOverview(effCCR, effBCC, scale, effSBM); //Array to save efficiency results
        String[] reference = Evaluation.createReferenceSetDual(dmu, threeDea.getSolutionList().get(3));    //Array for reference units. Reference units are pulled from the BCC results
        reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, threeDea.getSolutionList().get(4)));
        reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, threeDea.getSolutionList().get(5)));
        overList.add(overview); //Add to list of result-arrays
        refList.add(reference); //Add to list of result-arrays
    }

    public void computeMQI(List<Integer> inputStr, List<Integer> interOneStr, List<Integer> interTwoStr, List<Integer> outputStr, int scale) throws Exception {
        //method to create a Scenario-MQI for a Scenario Object. Data will not be printed out, as this was already done in the Scenario onstructor
        ScenarioMQI mqi = null;
        if(interOneStr == null && interTwoStr == null)    //MQI for a one-stage-DEA
            mqi = new ScenarioMQI(this.league, inputStr, outputStr, orientation, false, start, seasons, scale);
        else if (interTwoStr == null && settings[1][1] == false && settings[2][1] == false) //TwoStageDEA and no use of efficiency as input
            mqi = new ScenarioMQI(this.league, inputStr, interOneStr, interTwoStr, outputStr, this.settings, false, start, seasons, scale);
        else if(settings[1][1] == false && settings[2][1] == false && settings[1][2] == false && settings[2][2] == false)   //ThreeStageDEA and no use of efficiency as input
            mqi = new ScenarioMQI(this.league, inputStr, interOneStr, interTwoStr, outputStr, this.settings, false, start, seasons, scale);
    }

    public List<String[]> getDMUList()  {
        return this.dmuList;
    }

    public List<double[][]> getOverviewList()  {
        return this.overList;
    }

    public List<DEA> getDEAList()  {
        return this.deaList;
    }

    public List<TwoStageDEA> getTwoDEAList()  {
        return this.twodeaList;
    }

    public List<ThreeStageDEA> getThreeDEAList()  {
        return this.threedeaList;
    }
}

