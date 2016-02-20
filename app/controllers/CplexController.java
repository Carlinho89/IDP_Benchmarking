package controllers;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * Created by carlodidomenico on 20/02/16.
 */
public class CplexController {
    public void model1(){
        try {
            IloCplex cplex = new IloCplex();

            //variables

            //x,y >= 0 constraint implicit
            IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x"),
                    y = cplex.numVar(0, Double.MAX_VALUE, "y");

            //expressions
            IloLinearNumExpr objective = cplex.linearNumExpr();

            objective.addTerm(0.12, x);
            //.addTerm(1, x); if you want x multiplied by 1
            objective.addTerm(0.15, y);

            //define objective

            cplex.addMinimize(objective);

            //define constraints
            //60x + 60y >= 300
            cplex.addGe(cplex.sum(cplex.prod(60, x),cplex.prod(60,y)), 300);
            //12x + 6y  >= 36
            cplex.addGe(cplex.sum(cplex.prod(12, x),cplex.prod(6,y)), 36);
            //10x + 30y >= 90
            cplex.addGe(cplex.sum(cplex.prod(10, x),cplex.prod(30,y)), 90);


            //solve
            if(cplex.solve()){
                System.out.println("obj = " + cplex.getObjValue());
                System.out.println("x = " + cplex.getValue(x));
                System.out.println("y = " + cplex.getValue(y));


            }else{
                System.out.println("Model Not solved");
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public String getValueForObject(IloCplex cplex, String key){
        Object valObj;

        try{

        }catch (IloException e) {
            e.printStackTrace();
        }

    }
}
