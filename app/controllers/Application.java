package controllers;

//CPLEX Libs
import ilog.cplex.*;
import ilog.concert.*;

import models.Input;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.get_started;
import views.html.index;


public class Application extends Controller {

    public Result index() {

        return ok(index.render("Welcome"));
    }

    public Result getStarted() {

        model1();
        //return ok(get_started.render(Input.getById(1), Input.getByType("Social")));
   	    return ok(get_started.render(Input.getAll()));
    }

    public static void model1(){
        try {
            IloCplex cplex = new IloCplex();

            //variables

            //x,y >= 0 constraint implicit
            IloNumVar 	x = cplex.numVar(0, Double.MAX_VALUE, "x"),
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

}
