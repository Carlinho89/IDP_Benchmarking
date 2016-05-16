package controllers;

import workpackage.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 16/05/16.
 */
public class SolverController {
    public void solve(){
        List<Integer> inputs  = new ArrayList<Integer>();
        inputs.add(14);
        inputs.add(16);
        inputs.add(7);
        //inputs.add(13);
        //inputs.add(11);

        List<Integer> outputs = new ArrayList<Integer>();
        outputs.add(4);
        //outputs.add(5);

        String league = "Bundes Liga";
        boolean orientation = false;
        boolean superEfficiency = false;
        int start = 2010;
        int seasons = 4;

        try {
            Scenario scenario = new Scenario(league, inputs, outputs, orientation, superEfficiency, start, seasons);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
