package controllers;

//CPLEX Libs

import com.fasterxml.jackson.databind.JsonNode;
import models.Input;
import models.SimpleSolverQuery;
import models.Team;

import play.Routes;
import play.api.libs.json.JsPath;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import scala.util.parsing.json.JSONObject;
import scala.util.parsing.json.JSONObject$;
import views.html.*;


import java.util.HashMap;

public class Application extends Controller {


    public Result index() {

        return ok(index.render("Welcome"));
    }

    public Result benchmarking(String chapter) {
        if (chapter.compareTo("Models") == 0) {
            return ok(bench_models.render());
        } else if(chapter.compareTo("Methodology") == 0){
            return ok(dea_method.render());

        } else if(chapter.compareTo("CCR") == 0){
            return ok(dea_ccr.render());

        } else if(chapter.compareTo("BCC") == 0){
            return ok(dea_bcc.render());

        } else if(chapter.compareTo("Slack") == 0){
            return ok(dea_slack.render());

        } else if(chapter.compareTo("Efficiency") == 0){
            return ok(dea_eff.render());

        } else if(chapter.compareTo("Multi") == 0){
            return ok(dea_multi.render());

        } else if(chapter.compareTo("MQI") == 0){
            return ok(dea_mqi.render());

        } else {
            return ok(bench_intro.render());
        }

    }

    public Result getStarted() {
/*
        CplexController cplexController = new CplexController();
        cplexController.model1(); */

        //return ok(get_started.render(Input.getById(1), Input.getByType("Social")));
        return ok(get_started.render(Input.getByType("Sporty"), Input.getByType("Social"), Input.getByType("Monetary"), Input.getOutputs()));

    }

    public Result getLeagueTeamsBySeason(int year, int league_id) {
        return ok(Json.toJson(Team.getAllbySeason(year, league_id)));
    }

    public  Result simpleSolver(){
        DynamicForm form = Form.form().bindFromRequest();



        if (form.data().size() == 0) {
            return badRequest("Expceting some data");
        } else {
            String response = form.get("query");

            JsonNode json = Json.parse(response);
            SimpleSolverQuery query = new SimpleSolverQuery(response);


            return ok(""+query.teamID);
        }
    }







    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("appRoutes", //appRoutes will be the JS object available in our view
                routes.javascript.Application.getLeagueTeamsBySeason()
        ));
    }
}
