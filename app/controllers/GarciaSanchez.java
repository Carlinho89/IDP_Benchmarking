package controllers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 05/05/16.
 */
public class GarciaSanchez {

    public void test(){
        CplexController cplexConnection = new CplexController();

        //Set parameter selection
        //Input for offensive efficiency: Shots on Goal, Chances, Crosses
        //Output for offensive efficiency: Goals shot
        String selectionOffIn = "shotsOnGoal, shots, crosses";
        String selectionOffOut = "goalsSh";

        //Input for defensive efficiency: Intercepts, 1 / shots conceived
        //Output for defensive efficiency: 1 / Goals conceived
        String selectionDefIn = "intercept, oneByShCon";
        String selectionDefOut = "oneByGoCon";

        //Output for Athletic efficiency: SCORE
        String selectionAthOut = "score";

        //Output for Social efficiency: Spectators
        String selectionSocOut = "spec";

        List<String[]> dmuList = new ArrayList<String[]>();
        List<double[][]>overList = new ArrayList<double[][]>();
        List<String[]>refList = new ArrayList<String[]>();
        boolean[][]ramifications = {{false, false},{false, true},{false, false}};

        String league = "Premier League"; //"premier_league";
        int start = 2010;
        int seasons = 4;

        for(int i = 0; i < seasons; i++) { //3 seasons available
            //Get DMUs
            String[] dmu = cplexConnection.createDMUArray(league);
            dmuList.add(dmu);

            //Create Parameter-Arrays for Stage One
            double [][]offIn = cplexConnection.createParameterArray(league, (start + i), selectionOffIn);
            double [][]offOut = cplexConnection.createParameterArray(league, (start + i), selectionOffOut);
            double [][]defIn = cplexConnection.createParameterArray(league, (start + i), selectionDefIn);
            double [][]defOut = cplexConnection.createParameterArray(league, (start + i), selectionDefOut);


        }

    }
}
