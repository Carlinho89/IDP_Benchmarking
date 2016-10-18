package controllers;

//CPLEX Libs


import com.fasterxml.jackson.databind.JsonNode;
import ilog.concert.IloException;
import ilog.cplex.IloCplexModeler;
import models.*;
import play.Routes;
import play.api.libs.ws.ssl.SystemConfiguration;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.util.parsing.json.JSONObject;
import scala.util.parsing.json.JSONObject$;
import views.html.*;
import workpackage.Scenario;
import workpackage.ScenarioMQI;
import play.api.libs.json.*;
import java.util.ArrayList;
import java.util.List;

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
        return ok(get_started.render(Input.getByType("Sporty"), Input.getByType("Social"), Input.getByType("Monetary"), Input.getOutputs(), Input.getByType("Offensive"),Input.getByType("Defensive")));

    }

    public Result showCharts() {

        GarciaSanchez gs = new GarciaSanchez();
        String jsonResult = "";
        try {
           jsonResult = gs.test();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsonResult);

        Scenario a = null;

        return ok(show_charts.render(jsonResult));

    }

    public Result getLeagueTeamsBySeason(int year, int league_id) {
        return ok(Json.toJson(Team.getAllbySeason(year, league_id)));
    }

    public  Result simpleSolver(){
        DynamicForm form = Form.form().bindFromRequest();
        System.out.println("Simple");
        if (form.data().size() == 0) {
            return badRequest("Expecting some data");
        } else {
            String response = form.get("query");
            JsonNode json = Json.parse(response);
            JsonNode solution = null;
            SolverController solverController = null;
            SolverSimpleQuery query = null;

            solverController = new SolverController();
            query = new SolverSimpleQuery(response);

            solution = solverController.solve(query);
            System.out.println("Solution: " + solution);

            //TO-DO: Change with new return type for the solution --> JsonNode
            Scenario solvedScenario = null;
             return ok(show_charts.render(Json.stringify(solution)));

        }
    }

    public  Result complexSolver(){
        DynamicForm form = Form.form().bindFromRequest();
        boolean mockIt = false;
        System.out.println("Complex");

        if (form.data().size() != 0 || mockIt){
            //TO-DO: remove mock string
            final String mockResponse = "{\"superEff\":false,\"numberOfSeasons\":1,\"leagueID\":\"2\",\"leagueName\":\"Premier\",\"season\":2014,\"numberOfTeams\":20,\"teamID\":\"11\",\"teamName\":\"Arsenal\",\"selectedInputs\":[2,8,9,10,16],\"selectedInputsNames\":[\"Games Drawn\",\"Ball Possession\",\"Pass Success\",\"Red Cards\",\"Cross Per Game\"],\"selectedOutputs\":[6,1,4,3,5],\"selectedOutputsNames\":[\"Rank\",\"Games Won\",\"Goals Scored\",\"Games Lost\",\"Goals Against\"],\"solver\":\"complex\",\"selectedMethod\":\"CCR\",\"stage1DEA\":[{\"selectedInputs\":[2],\"selectedOutputs\":[6],\"previousResults\":[],\"stage\":1,\"inputOriented\":false,\"deaID\":0},{\"selectedInputs\":[8],\"selectedOutputs\":[1],\"previousResults\":[],\"stage\":1,\"inputOriented\":true,\"deaID\":1}],\"stage2DEA\":[{\"selectedInputs\":[9],\"selectedOutputs\":[4],\"previousResults\":[0,1],\"stage\":2,\"inputOriented\":false,\"deaID\":0},{\"selectedInputs\":[2],\"selectedOutputs\":[6,1],\"previousResults\":[],\"stage\":2,\"inputOriented\":false,\"deaID\":1}],\"stage3DEA\":[{\"selectedInputs\":[2],\"selectedOutputs\":[6],\"previousResults\":[0],\"stage\":3,\"inputOriented\":false,\"deaID\":0}]}";

            SolverController solverController = null;
            String response = null;
            JsonNode json = null;
            SolverComplexQuery query = null;

            response = (mockIt)? mockResponse: form.get("query");

            solverController = new SolverController();
            json = Json.parse(response);
            query = new SolverComplexQuery(response);

            JsonNode solution = solverController.solve(query);
            System.out.println("Solution: " + solution);


            Scenario solvedScenario = null;
            JsonNode node = Json.toJson(solution);
            //System.out.println("JSON RESULT: ");
            //System.out.println(Json.stringify(node));
            return ok(show_charts_complex.render(Json.stringify(node)));

        }
        else {
            return badRequest("Expecting some data");
        }
    }

    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("appRoutes", //appRoutes will be the JS object available in our view
                routes.javascript.Application.getLeagueTeamsBySeason()
        ));
    }



}
