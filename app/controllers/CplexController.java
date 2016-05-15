package controllers;

import com.avaje.ebean.*;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import models.Input;
import models.League;
import models.SeasonalData;

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


    public String[] createDMUArray(String league) {
        return League.getLeaguesNames();
    }
    /*
    * The method is given a String which separates the selected tables by a comma.
    * This string is then turned into an array from which the parameters are gained
    * */
    public double[][] createParameterArray(String league, int season, List<Integer> choice)    {
        Integer choices[] = choice.toArray(new Integer[choice.size()]);
        List<List<SeasonalData>> results = new ArrayList<List<SeasonalData>>();

        for (int i = 0; i < choice.size(); i++){
            RawSql rawSql = RawSqlBuilder.parse("select id, team_id, team_name, year, league_id, input_id, value  " + "from seasonal_data " + "where year =" + season + "AND input_id = " + choices[i])
                    .columnMapping("id","id")
                    .columnMapping("team_id","team_id")
                    .columnMapping("team_name","team_name")
                    .columnMapping("year","year")
                    .columnMapping("league_id","league_id")
                    .columnMapping("input_id","input_id")
                    .columnMapping("value","value")
                    .create();
            Query<SeasonalData> query = Ebean.find(SeasonalData.class);
            query.setRawSql(rawSql);
            List<SeasonalData> result = query.findList();
            results.add(result);
        }

        double parameter [][] = new double[results.size()][results.get(0).size()];
        for (int i=0; i < results.size(); i++){
            for (int j=0; j < results.get(i).size(); j++){
                parameter[i][j] = Double.parseDouble(String.valueOf(results.get(i).get(j).value));
            }
        }
        /*
        int i, j = i = 0;
        for (double[] p:parameter) {
            System.out.println("i: " + i++);
            for (double pp: p) {
                System.out.println("j: " + j++);
                System.out.println("value: " + pp );
                j=0;
            }
        }*/

        return parameter;
    }

    static public String ListToCSString(List<Integer> l){
        String result = null;
        List<Input> inputs = new ArrayList<Input>();
        System.out.println("Debug: ListToCSString");
        for (Integer id: l) {
            Input input = (Input) new Model.Finder(Input.class).byId(id);
            inputs.add(input);
            System.out.println("Debug - ListToCSString : input-name is " + input.name);
        }

        for (int i=0; i < inputs.size(); i++){
            if (i != inputs.size()-1)
                result += inputs.get(i).name + ", ";
            else
                result += inputs.get(i).name;
        }

        return result;
    }
}
