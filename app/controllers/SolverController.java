package controllers;

import models.SolverQuery;
import workpackage.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 16/05/16.
 */
public class SolverController {

    public void solve(SolverQuery query){
        List<Integer> inputs  = query.getSelectedInputs();
        List<Integer> outputs = query.getSelectedOutputs();
        int league = query.getLeagueID();

        boolean orientation = false;
        boolean superEfficiency = false;
        int start = query.getSeason();
        int seasons = 4;

        try {
            List<Integer> selectionOffIn = new ArrayList<Integer>();
            selectionOffIn.add(14);
            selectionOffIn.add(16);
            selectionOffIn.add(7);
            selectionOffIn.add(13);


            List<Integer> selectionOffOut = new ArrayList<Integer>();
            selectionOffOut.add(4);


            Scenario garciaSanchez = new Scenario(league, selectionOffIn, selectionOffOut, orientation, superEfficiency, start, seasons);

            //Scenario scenario = new Scenario(league, inputs, outputs, orientation, superEfficiency, start, seasons);
            //GarciaSanchez g = new GarciaSanchez();
            //g.test();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
