package controllers;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.HashMap;

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

}
