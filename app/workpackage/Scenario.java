package workpackage;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.CplexController;
import ilog.concert.IloException;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Scenario {

    public  double[][] offOutTwo;
    public  Malmquist mqiOff;
    public  double[][] defInTwo;
    public  double[][] defInOne;
    public  Malmquist mqiDef;
    public  double[][] defOutOne;
    public  double[][] defOutTwo;
    public  double[][] solMQIOff;
    public  double[][] solMQIDef;
    public  double[][] solMQI;
    public  String result;
    public double[][] offInTwo;
    public double[][] offOutOne;
    public double[][] offInOne;
    public String[] dmuTwo;
    public String[] dmuOne;
    public List<double[][]> malmList;
    public List<String[]> dmuMalm;
    public double[][] overFin;
    public double[][] overSoc;
    public double[][] overAth;
    public double[] stageTwoSBM;
    public double[] stageOneSBM;
    public TwoStageDEA deaSBM;
    public double[] stageTwoBCC;
    public double[] stageOneBCC;
    public TwoStageDEA deaBCC;
    public double[] stageTwoCCR;
    public double[] stageOneCCR;
    public TwoStageDEA deaCCR;
    public double[][] inputAthSBM;
    public double[][] socOut;
    public double[][] inputAthCCR;
    public double[][] inputAthBCC;
    public double[][] athOut;
    public double[][] overDef;
    public double[][] overOff;
    public double[][] solDefSBM;
    public double[][] solDefBCC;
    public double[][] solDefCCR;
    public double[][] solOffSBM;
    public double[][] solOffBCC;
    public double[][] solOffCCR;
    public DEA deaDef;
    public DEA deaOff;
    public String[] reference;
    public int league;          //Which league are we looking at?
    public  CplexController connection;   //Connect to database
    public List<Integer> selectionAthOut;
    public List<Integer> selectionSocOut;
    public List<Integer> selectionDefOut;
    public List<Integer> selectionDefIn;
    public List<Integer> selectionOffOut;
    public List<Integer> selectionOffIn;
    public String[] dmu;           //Array for selected DMUs
    public double[][] input;       //Array for selected inputs
    public double[][] interOne;    //Array of Stage 2 parameters
    public double[][] interTwo;    //Array of Stage 3 parameters
    public double[][] output;      //Array of  output selection (or of  output of Stage 3 in case of a multi-stage approach
    public boolean orientation;   //Boolean to figure out orientation of a single-stage DEA (true: input, false: output)
    public boolean superEfficiency;    //Boolean to figure out whether Super Efficiency is desired or not (true: yes, false: no)
    public boolean [][]settings;   //Settings: orientation[0][], use efficiency of last stage as input [1][], combined input [2][]
    public  int start;              //Starting year of the observations
    public  int seasons;            //Amount of seasons looked at
    public List<String[]>dmuList;  //List of used DMUs
    public List<double[][]>overList;   //List over overview-arrays from results
    public List<String[]>refList;   //List over reference-arrays from results
    public List<double[][]>paraValues; //List containing the data used
    public List<DEA>deaList;  //List of DEAs created while solving the scenario
    public List<TwoStageDEA>twodeaList;  //List of TwoStageDEAs created while solving the scenario
    public List<ThreeStageDEA>threedeaList;  //List of ThreeStageDEAs created while solving the scenario

    public Scenario(int league, List<Integer> inputStr, List<Integer> outputStr, boolean orientation, boolean superEff, int start, int seasons) throws Exception   //Single-Stage constructor
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
            this.dmu = connection.createDMUArray(league, start + i);
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
        //ExcelOutput.createEfficiencyOutput(League.getById(league).name, dmuList, overList, refList);

        //Print out data that was used in the analysis


       // String[] paraNames = ExcelOutput.combineStringArray(CplexController.listToCSString(inputStr).split(", "), CplexController.listToCSString(outputStr).split(", "));
        this.paraValues = new ArrayList<>();  //Unify input and output list
        paraValues.add(paraIn);
        paraValues.add(paraOut);
        //ExcelOutput.createDataOutput(League.getById(league).name, dmuList, paraNames, paraValues);
    }
    public Scenario (int league, List<Integer> inputOff, List<Integer> inputDef, List<Integer> outputOff, List<Integer> outputDef, List<Integer> outputAth, List<Integer> outputSoc, boolean[][]setting, boolean superEff, int start, int seasons) throws IloException {
        this.connection = new CplexController();

        //Set parameter selection
        //Input for offensive efficiency: Shots on target Goal (id 14), shots per game (id 7), Crosses (id 16)
        //Output for offensive efficiency: Goals scored(id 4)
        this.selectionOffIn = inputOff;
        this.selectionOffOut = outputOff;

        //Input for defensive efficiency: Intercepts (id 13), 1 / shots conceived (id 11)
        //Output for defensive efficiency: 1 / Goals conceived (id 5)
        this.selectionDefIn = inputDef;

        this.selectionDefOut = outputDef;

        //Output for Athletic efficiency: Games won (id 1)
        this.selectionAthOut = outputAth;

        //Output for Social efficiency: Average Age (id 21)
        this.selectionSocOut = outputSoc;

        this.dmuList = new ArrayList<String[]>();
        this.overList = new ArrayList<double[][]>();
        this.refList = new ArrayList<String[]>();
        boolean[][]ramifications = setting;

        this.league = league; //"premier_league";
        this.start = start;
        this.seasons = seasons;

        for(int i = 0; i < seasons; i++) { //3 seasons available
            //Get DMUs
            String[] dmu = connection.createDMUArray(league, (start + i));
            dmuList.add(dmu);

            //Create Parameter-Arrays for Stage One
            double [][]offIn = connection.createParameterArray(league, (start + i), this.selectionOffIn);
            double [][]offOut = connection.createParameterArray(league, (start + i), this.selectionOffOut);
            double [][]defIn = connection.createParameterArray(league, (start + i), this.selectionDefIn);
            double [][]defOut = connection.createParameterArray(league, (start + i), this.selectionDefOut);


            //Create array for reference units
            this.reference = new String[dmu.length];

            //Create DMUs for stage one and solve them

            this.deaOff = new DEA(dmu, offIn, offOut);
            this.deaDef = new DEA(dmu, defIn,defOut);


            this.solOffCCR = deaOff.solve_Dual_Basic_Output(false);
            this.solOffBCC = deaOff.solve_Dual_Basic_Output(true);
            this.solOffSBM = deaOff.solve_Dual_SBM_Output();
            this.reference = Evaluation.createReferenceSetDual(dmu, solOffBCC);

            this.solDefCCR = deaDef.solve_Dual_Basic_Output(false);
            this.solDefBCC = deaDef.solve_Dual_Basic_Output(true);
            this.solDefSBM = deaOff.solve_Dual_SBM_Output();
            reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, solDefBCC));

            //Create Scale Efficiencies and Overviews (one overview per season)
            this.overOff = Evaluation.createOverview(Evaluation.getEfficiency(solOffCCR), Evaluation.getEfficiency(solOffBCC), Evaluation.createScaleEfficiency(solOffCCR, solOffBCC), Evaluation.getEfficiency(solOffSBM));
            this.overDef = Evaluation.createOverview(Evaluation.getEfficiency(solDefCCR), Evaluation.getEfficiency(solDefBCC), Evaluation.createScaleEfficiency(solDefCCR, solDefBCC), Evaluation.getEfficiency(solDefSBM));

            //Refit Efficiencies as Inputs for Stage 2
            this.inputAthCCR = ExcelOutput.mergeArray(Evaluation.fitEfficiency(Evaluation.getEfficiency(solOffCCR)), Evaluation.fitEfficiency(Evaluation.getEfficiency(solDefCCR)));
            this.inputAthBCC = ExcelOutput.mergeArray(Evaluation.fitEfficiency(Evaluation.getEfficiency(solOffBCC)), Evaluation.fitEfficiency(Evaluation.getEfficiency(solDefBCC)));
            this.inputAthSBM = ExcelOutput.mergeArray(Evaluation.fitEfficiency(Evaluation.getEfficiency(solOffSBM)), Evaluation.fitEfficiency(Evaluation.getEfficiency(solDefSBM)));

            //Get Output-Array for Stages 2 and 3
            this.athOut = connection.createParameterArray(league, (start + i), selectionAthOut);
            this.socOut = connection.createParameterArray(league, (start + i), selectionSocOut);

            //Solve the last 2 stages with a 2-stage-dea
            this.deaCCR = new TwoStageDEA(dmu, inputAthCCR, athOut, socOut, ramifications);
            deaCCR.solveTwoStage("CCR");
            this.stageOneCCR = Evaluation.getEfficiency(deaCCR.getSolutionList().get(0));
            this.stageTwoCCR = Evaluation.getEfficiency(deaCCR.getSolutionList().get(1));

            this.deaBCC = new TwoStageDEA(dmu, inputAthBCC, athOut, socOut, ramifications);
            deaBCC.solveTwoStage("BCC");

            this.stageOneBCC = Evaluation.getEfficiency(deaBCC.getSolutionList().get(0));
            this.stageTwoBCC = Evaluation.getEfficiency(deaBCC.getSolutionList().get(1));




            this.deaSBM = new TwoStageDEA(dmu, inputAthSBM, athOut, socOut, ramifications);
            deaSBM.solveTwoStage("SBM");
            this.stageOneSBM = Evaluation.getEfficiency(deaSBM.getSolutionList().get(0));
            this.stageTwoSBM = Evaluation.getEfficiency(deaSBM.getSolutionList().get(1));
            reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, deaBCC.getSolutionList().get(0)));
            reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, deaBCC.getSolutionList().get(1)));

            this.overAth = Evaluation.createOverview(stageOneCCR, stageOneBCC, Evaluation.createScaleEfficiency(stageOneCCR, stageOneBCC), stageOneSBM);
            this.overSoc = Evaluation.createOverview(stageTwoCCR, stageTwoBCC, Evaluation.createScaleEfficiency(stageTwoCCR, stageTwoBCC), stageTwoSBM);

            this.overFin = ExcelOutput.combineOverviewStage(overOff, overDef);
            overFin = ExcelOutput.combineOverviewStage(overFin, overAth);
            overFin = ExcelOutput.combineOverviewStage(overFin, overSoc);


            overList.add(overFin);
            refList.add(reference);

        }



        //Create Excel Output
       // ExcelOutput.createEfficiencyOutput(League.getById(league).name, dmuList, overList, refList);
        System.out.println("MALM");
        //Get MQI values
        this.dmuMalm = new ArrayList<String[]>();
        this.malmList = new ArrayList<double[][]>();
        for(int i = 0; i < (this.seasons - 1); i++)
        {
            System.out.println("MALM2");

            this.dmuOne = connection.createDMUArray(league,(2011 + i));
            this.dmuTwo = connection.createDMUArray(league, (2010 + i + 1));

            //Create Parameters and MQI-Object for Stage 1
            this.offInOne = connection.createParameterArray(league, (2010 + i), selectionOffIn);
            this.offOutOne = connection.createParameterArray(league, (2010 + i), selectionOffOut);
            this.offInTwo = connection.createParameterArray(league, (2010 + i + 1), selectionOffIn);
            this.offOutTwo = connection.createParameterArray(league, (2010 + i + 1), selectionOffOut);

            this.defInOne = connection.createParameterArray(league, (2010 + i), selectionDefIn);
            this.defOutOne = connection.createParameterArray(league, (2010 + i), selectionDefOut);
            this.defInTwo = connection.createParameterArray(league, (2010 + i + 1), selectionDefIn);
            this.defOutTwo = connection.createParameterArray(league, (2010 + i + 1), selectionDefOut);

            this.mqiOff = new Malmquist(2, false, dmuOne, dmuTwo, offInOne, offInTwo, offOutOne, offOutTwo);
            this.mqiDef = new Malmquist(2, false, dmuOne, dmuTwo, defInOne, defInTwo, defOutOne, defOutTwo);
            this.solMQIOff = mqiOff.calculateRadialMalmquist();
            this.solMQIDef = mqiDef.calculateRadialMalmquist();
            this.solMQI = ExcelOutput.combineOverviewStage(solMQIOff, solMQIDef);

            dmuMalm.add(mqiOff.dmu);
            malmList.add(solMQI);
        }


        System.out.println("START");
        for(int i = 0; i<malmList.size(); i++){
            for(int j=0; j<malmList.get(i).length ;j++)
                for(int k=0; k<malmList.get(i)[j].length ;k++){
                    System.out.println("i:"+i+" j:"+j+" k:"+k+"----"+malmList.get(i)[j][k]);

                }


        }
        System.out.println(dmuMalm);
        System.out.println(malmList);

        //Create Excel Output
        //ExcelOutput.createMQIOutput(League.getById(league).name, dmuMalm, malmList);

        JsonNode jsnmalm= Json.toJson(malmList);
        JsonNode jsndmu= Json.toJson(dmuMalm);
        this.result= "{\"dmu\":"+Json.stringify(jsndmu)+", \"malm\" : " +Json.stringify(jsnmalm) +"}";

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

    public void solve(ThreeStageDEA threeDea)  {
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

