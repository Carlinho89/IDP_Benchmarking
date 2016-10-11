package controllers;

//CPLEX Libs


import com.fasterxml.jackson.databind.JsonNode;
import ilog.concert.IloException;
import ilog.cplex.IloCplexModeler;
import models.*;
import play.Routes;
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

        return ok(show_charts.render(jsonResult,a));

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

            SolverSimpleQuery query = new SolverSimpleQuery(response);

            SolverController solverController = new SolverController();



            Scenario solvedScenario = solverController.solve(query);

            

             return ok(show_charts.render(Json.stringify(Json.toJson(solvedScenario)), solvedScenario));

        }
    }

    public  Result complexSolver(){
        DynamicForm form = Form.form().bindFromRequest();
        boolean mockIt = true;
        System.out.println("Complex");

        if (form.data().size() != 0 || mockIt){
            final String mockResponse = "{\"superEff\":true,\"solver\":\"complex\",\"selectedMethod\":\"BCC\",\"leagueID\":\"2\",\"leagueName\":\"Premier\",\"season\":2013,\"numberOfTeams\":20,\"teamID\":\"180\",\"teamName\":\"Southampton\",\"selectedInputs\":[2,6,8,9],\"selectedInputsNames\":[\"Games Drawn\",\"Rank\",\"Ball Possession\",\"Pass Success\"],\"selectedOutputs\":[1,4,3,5],\"selectedOutputsNames\":[\"Games Won\",\"Goals Scored\",\"Games Lost\",\"Goals Against\"],\"stage1DEA\":[{\"selectedInputs\":[2,9],\"selectedOutputs\":[1,5],\"previousResults\":[],\"stage\":1,\"inputOriented\":false,\"deaID\":0},{\"selectedInputs\":[6],\"selectedOutputs\":[4],\"previousResults\":[],\"stage\":1,\"inputOriented\":false,\"deaID\":1}],\"stage2DEA\":[{\"selectedInputs\":[8],\"selectedOutputs\":[3],\"previousResults\":[1],\"stage\":2,\"inputOriented\":false,\"deaID\":0}],\"stage3DEA\":[{\"selectedInputs\":[9],\"selectedOutputs\":[5],\"previousResults\":[0],\"stage\":3,\"inputOriented\":false,\"deaID\":0}],\"numberOfSeasons\":1}";
            String response = (mockIt)? mockResponse: form.get("query");
            SolverController solverController = new SolverController();


            JsonNode json = Json.parse(response);
            SolverComplexQuery query = new SolverComplexQuery(response);



            try {
                Scenario solvedScenario = solverController.solve(query);
                JsonNode node = Json.toJson(solvedScenario);
                //System.out.println("JSON RESULT: ");
                //System.out.println(Json.stringify(node));
                return ok(show_charts_complex.render(Json.stringify(node), solvedScenario));
            } catch(Exception e){
                e.printStackTrace();
            }

        return null;


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
