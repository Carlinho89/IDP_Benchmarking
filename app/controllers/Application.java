package controllers;

//CPLEX Libs

import models.Input;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.get_started;
import views.html.index;

import java.util.HashMap;

public class Application extends Controller {

    public Result index() {

        return ok(index.render("Welcome"));
    }

    public Result getStarted() {

        CplexController cplexController = new CplexController();
        cplexController.model1();
        Double objVal = cplexController.getObjectiveVal();

        HashMap<String, Double> vars = cplexController.getAllVars();



        //return ok(get_started.render(Input.getById(1), Input.getByType("Social")));
   	    return ok(get_started.render(Input.getAll(), objVal, null));
    }



}
