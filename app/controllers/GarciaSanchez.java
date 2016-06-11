package controllers;

import ilog.concert.IloException;
import models.League;
import workpackage.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 05/05/16.
 */
public class GarciaSanchez {

    public void test() throws IloException, Exception{
        CplexController cplexConnection = new CplexController();

        //Set parameter selection
        //Input for offensive efficiency: Shots on target Goal (id 14), shots per game (id 7), Crosses (id 16)
        //Output for offensive efficiency: Goals scored(id 4)
        List<Integer> selectionOffIn = new ArrayList<Integer>();
        selectionOffIn.add(14);
        selectionOffIn.add(16);
        selectionOffIn.add(7);

        List<Integer> selectionOffOut = new ArrayList<Integer>();
        selectionOffOut.add(4);

        //Input for defensive efficiency: Intercepts (id 13), 1 / shots conceived (id 11)
        //Output for defensive efficiency: 1 / Goals conceived (id 5)
        List<Integer> selectionDefIn = new ArrayList<Integer>();
        selectionDefIn.add(13);
        selectionDefIn.add(11);

        List<Integer> selectionDefOut = new ArrayList<Integer>();
        selectionDefOut.add(5);

        //Output for Athletic efficiency: Games won (id 1)
        List<Integer> selectionAthOut = new ArrayList<Integer>();
        selectionAthOut.add(1);

        //Output for Social efficiency: Average Age (id 21)
        List<Integer> selectionSocOut = new ArrayList<Integer>();
        selectionSocOut.add(21);

        List<String[]> dmuList = new ArrayList<String[]>();
        List<double[][]>overList = new ArrayList<double[][]>();
        List<String[]>refList = new ArrayList<String[]>();
        boolean[][]ramifications = {{false, false},{false, true},{false, false}};

        int league = 2; //"premier_league";
        int start = 2010;
        int seasons = 4;

        for(int i = 0; i < seasons; i++) { //3 seasons available
            //Get DMUs
            String[] dmu = cplexConnection.createDMUArray(league, (start + i));
            dmuList.add(dmu);

            //Create Parameter-Arrays for Stage One
            double [][]offIn = cplexConnection.createParameterArray(league, (start + i), selectionOffIn);
            double [][]offOut = cplexConnection.createParameterArray(league, (start + i), selectionOffOut);
            double [][]defIn = cplexConnection.createParameterArray(league, (start + i), selectionDefIn);
            double [][]defOut = cplexConnection.createParameterArray(league, (start + i), selectionDefOut);


            //Create array for reference units
            String[] reference = new String[dmu.length];

            //Create DMUs for stage one and solve them

            DEA deaOff = new DEA(dmu, offIn, offOut);
            DEA deaDef = new DEA(dmu, defIn,defOut);


            double[][]solOffCCR = deaOff.solve_Dual_Basic_Output(false);
            double[][]solOffBCC = deaOff.solve_Dual_Basic_Output(true);
            double[][]solOffSBM = deaOff.solve_Dual_SBM_Output();
            reference = Evaluation.createReferenceSetDual(dmu, solOffBCC);

            double[][]solDefCCR = deaDef.solve_Dual_Basic_Output(false);
            double[][]solDefBCC = deaDef.solve_Dual_Basic_Output(true);
            double[][]solDefSBM = deaOff.solve_Dual_SBM_Output();
            reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, solDefBCC));

            //Create Scale Efficiencies and Overviews (one overview per season)
            double[][]overOff = Evaluation.createOverview(Evaluation.getEfficiency(solOffCCR), Evaluation.getEfficiency(solOffBCC), Evaluation.createScaleEfficiency(solOffCCR, solOffBCC), Evaluation.getEfficiency(solOffSBM));
            double[][]overDef = Evaluation.createOverview(Evaluation.getEfficiency(solDefCCR), Evaluation.getEfficiency(solDefBCC), Evaluation.createScaleEfficiency(solDefCCR, solDefBCC), Evaluation.getEfficiency(solDefSBM));

            //Refit Efficiencies as Inputs for Stage 2
            double[][]inputAthCCR = ExcelOutput.mergeArray(Evaluation.fitEfficiency(Evaluation.getEfficiency(solOffCCR)), Evaluation.fitEfficiency(Evaluation.getEfficiency(solDefCCR)));
            double[][]inputAthBCC = ExcelOutput.mergeArray(Evaluation.fitEfficiency(Evaluation.getEfficiency(solOffBCC)), Evaluation.fitEfficiency(Evaluation.getEfficiency(solDefBCC)));
            double[][]inputAthSBM = ExcelOutput.mergeArray(Evaluation.fitEfficiency(Evaluation.getEfficiency(solOffSBM)), Evaluation.fitEfficiency(Evaluation.getEfficiency(solDefSBM)));

            //Get Output-Array for Stages 2 and 3
            double [][]athOut = cplexConnection.createParameterArray(league, (start + i), selectionAthOut);
            double [][]socOut = cplexConnection.createParameterArray(league, (start + i), selectionSocOut);

            //Solve the last 2 stages with a 2-stage-dea
            TwoStageDEA deaCCR = new TwoStageDEA(dmu, inputAthCCR, athOut, socOut, ramifications);
            deaCCR.solveTwoStage("CCR");
            double[]stageOneCCR = Evaluation.getEfficiency(deaCCR.getSolutionList().get(0));
            double[]stageTwoCCR = Evaluation.getEfficiency(deaCCR.getSolutionList().get(1));

            TwoStageDEA deaBCC = new TwoStageDEA(dmu, inputAthBCC, athOut, socOut, ramifications);
            deaBCC.solveTwoStage("BCC");
            double[]stageOneBCC = Evaluation.getEfficiency(deaBCC.getSolutionList().get(0));
            double[]stageTwoBCC = Evaluation.getEfficiency(deaBCC.getSolutionList().get(1));




            TwoStageDEA deaSBM = new TwoStageDEA(dmu, inputAthSBM, athOut, socOut, ramifications);
            deaSBM.solveTwoStage("SBM");
            double[]stageOneSBM = Evaluation.getEfficiency(deaSBM.getSolutionList().get(0));
            double[]stageTwoSBM = Evaluation.getEfficiency(deaSBM.getSolutionList().get(1));
            reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, deaBCC.getSolutionList().get(0)));
            reference = ExcelOutput.combineStringArray(reference, Evaluation.createReferenceSetDual(dmu, deaBCC.getSolutionList().get(1)));

            double[][]overAth = Evaluation.createOverview(stageOneCCR, stageOneBCC, Evaluation.createScaleEfficiency(stageOneCCR, stageOneBCC), stageOneSBM);
            double[][]overSoc = Evaluation.createOverview(stageTwoCCR, stageTwoBCC, Evaluation.createScaleEfficiency(stageTwoCCR, stageTwoBCC), stageTwoSBM);

            double[][]overFin = ExcelOutput.combineOverviewStage(overOff, overDef);
            overFin = ExcelOutput.combineOverviewStage(overFin, overAth);
            overFin = ExcelOutput.combineOverviewStage(overFin, overSoc);
            overList.add(overFin);
            refList.add(reference);

        }



        //Create Excel Output
        ExcelOutput.createEfficiencyOutput(League.getById(league).name, dmuList, overList, refList);

        //Get MQI values
        List<String[]>dmuMalm = new ArrayList<String[]>();
        List<double[][]>malmList = new ArrayList<double[][]>();
        for(int i = 0; i < (seasons - 1); i++)
        {
            String[] dmuOne = cplexConnection.createDMUArray(league,(2011 + i));
            String[] dmuTwo = cplexConnection.createDMUArray(league, (2010 + i + 1));

            //Create Parameters and MQI-Object for Stage 1
            double [][]offInOne = cplexConnection.createParameterArray(league, (2010 + i), selectionOffIn);
            double [][]offOutOne = cplexConnection.createParameterArray(league, (2010 + i), selectionOffOut);
            double [][]offInTwo = cplexConnection.createParameterArray(league, (2010 + i + 1), selectionOffIn);
            double [][]offOutTwo = cplexConnection.createParameterArray(league, (2010 + i + 1), selectionOffOut);

            double [][]defInOne = cplexConnection.createParameterArray(league, (2010 + i), selectionDefIn);
            double [][]defOutOne = cplexConnection.createParameterArray(league, (2010 + i), selectionDefOut);
            double [][]defInTwo = cplexConnection.createParameterArray(league, (2010 + i + 1), selectionDefIn);
            double [][]defOutTwo = cplexConnection.createParameterArray(league, (2010 + i + 1), selectionDefOut);

            Malmquist mqiOff = new Malmquist(2, false, dmuOne, dmuTwo, offInOne, offInTwo, offOutOne, offOutTwo);
            Malmquist mqiDef = new Malmquist(2, false, dmuOne, dmuTwo, defInOne, defInTwo, defOutOne, defOutTwo);
            double[][]solMQIOff = mqiOff.calculateRadialMalmquist();
            double[][]solMQIDef = mqiDef.calculateRadialMalmquist();
            double[][]solMQI = ExcelOutput.combineOverviewStage(solMQIOff, solMQIDef);

            dmuMalm.add(mqiOff.dmu);
            malmList.add(solMQI);
        }
        //Create Excel Output
        ExcelOutput.createMQIOutput(League.getById(league).name, dmuMalm, malmList);


    }
}
