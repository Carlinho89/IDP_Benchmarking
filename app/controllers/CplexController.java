package controllers;

import com.avaje.ebean.*;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import models.Input;
import models.SeasonalData;
import models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by carlodidomenico on 20/02/16.
 */
public class CplexController {
    private IloCplex cplex;
    private HashMap<String, IloNumVar> numVars;

    public CplexController(){
        super();


        cplex = null;
        numVars = new HashMap<String, IloNumVar>();
    }
    public void model1(){
        try {
            cplex = new IloCplex();

            //variables

            //x,y >= 0 constraint implicit
            numVars.put("x",cplex.numVar(0, Double.MAX_VALUE, "x"));
            numVars.put("y",cplex.numVar(0, Double.MAX_VALUE, "y"));

            //IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x"),
            //          y = cplex.numVar(0, Double.MAX_VALUE, "y");

            //expressions
            IloLinearNumExpr objective = cplex.linearNumExpr();

            objective.addTerm(0.12, numVars.get("x"));
            //.addTerm(1, x); if you want x multiplied by 1
            objective.addTerm(0.15, numVars.get("y"));

            //define objective

            cplex.addMinimize(objective);

            //define constraints
            //60x + 60y >= 300
            cplex.addGe(cplex.sum(cplex.prod(60, numVars.get("x")),cplex.prod(60,numVars.get("y"))), 300);
            //12x + 6y  >= 36
            cplex.addGe(cplex.sum(cplex.prod(12, numVars.get("x")),cplex.prod(6,numVars.get("y"))), 36);
            //10x + 30y >= 90
            cplex.addGe(cplex.sum(cplex.prod(10, numVars.get("x")),cplex.prod(30,numVars.get("y"))), 90);


            //solve
            if(cplex.solve()){
                System.out.println("obj = " + cplex.getObjValue());
                System.out.println("x = " + cplex.getValue(numVars.get("x")));
                System.out.println("y = " + cplex.getValue(numVars.get("y")));


            }else{
                System.out.println("Model Not solved");
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the objective value of the model implemented in the private var 'cplex'
     * @return objVal or null
     */
    public Double getObjectiveVal(){
        Double objVal = null;
        try {
            if (cplex != null)
                objVal = cplex.getObjValue();
            return objVal;
        } catch (IloException e) {
            e.printStackTrace();
            return objVal;
        }
    }

    public HashMap<String, Double> getAllVars(){
        HashMap<String, Double> allVars = null;

        try {
            if (cplex != null){
                allVars = new HashMap<String, Double>();
                for (String key: allVars.keySet()) {
                    allVars.put(key, cplex.getValue(numVars.get(key)));
                }
            }

            return allVars;
        } catch (IloException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String[] createDMUArray(int league, int season) {
        List<Team> teamsForSeason = Team.getAllbySeason(season, league);
        String[] teamsNamesForSeason = new String[teamsForSeason.size()];
        for (int i = 0; i < teamsForSeason.size(); i++) {
            teamsNamesForSeason[i] = teamsForSeason.get(i).name;
        }
        return teamsNamesForSeason;
    }
    /*
    * The method is given a String which separates the selected tables by a comma.
    * This string is then turned into an array from which the parameters are gained
    * */
    public double[][] createParameterArray(int league, int season, List<Integer> choice)    {
        SeasonalData stringPara[][] = new SeasonalData[choice.size()][];
        System.out.println("Fetching DB, choices number: " + choice.size());
        for (int i = 0; i < choice.size(); i++){
            List<SeasonalData> result = SeasonalData.getBySeasonAndLeague(season, league, choice.get(i));
            stringPara[i] = result.toArray(new SeasonalData[result.size()]);
            System.out.println("Done choice n" + i + " has n: " + result.size() + " elements");
        }

        System.out.println("stringPara.length: " + stringPara.length);
        System.out.println("stringPara[0].length: " + stringPara[0].length);

        for (int i = 0; i < stringPara.length; i++) {
            System.out.println("i: "+i);
            for (int j = 0; j < stringPara[i].length; j++) {
                System.out.println("j: "+j);
                System.out.println("Team " + stringPara[i][j].team_name + " val: " + stringPara[i][j].value);

            }

        }


        double[][] parameter = new double[stringPara.length][stringPara[0].length];   //Create final array for return
        for(int i = 0; i < stringPara.length; i++)
            for (int j = 0; j < stringPara[i].length; j++)
                parameter[i][j] = stringPara[i][j].value;


        return parameter;
    }

    static public String listToCSString(List<Integer> l){
        String result = "";
        List<Input> inputs = new ArrayList<Input>();
        //System.out.println("ListToCCSting size is: " + l.size());
        for (Integer id: l) {
            //System.out.println("Id is: " + id);
            Input input = (Input) new Model.Finder(Input.class).byId(id);
            //System.out.println("Name is: " + input.name);
            inputs.add(input);
        }

        for (int i=0; i < inputs.size(); i++){
            if (i != inputs.size()-1)
                result += inputs.get(i).name + ", ";
            else
                result += inputs.get(i).name;
        }

        //System.out.println("result string is '" + result + "'");
        return result;
    }


}
