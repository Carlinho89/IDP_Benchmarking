package controllers;

//CPLEX Libs

import com.fasterxml.jackson.databind.JsonNode;
import models.Input;
import models.SolverSimpleQuery;
import models.SolverSimpleQueryMQI;
import models.Team;
import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import workpackage.Scenario;
import workpackage.ScenarioMQI;

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
        //TODO remove these tests!!!
        this.testSimpleMQI();

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
        return ok(show_charts.render(jsonResult));

    }

    public Result getLeagueTeamsBySeason(int year, int league_id) {
        return ok(Json.toJson(Team.getAllbySeason(year, league_id)));
    }

    public  Result simpleSolver(){
        DynamicForm form = Form.form().bindFromRequest();

        if (form.data().size() == 0) {
            return badRequest("Expecting some data");
        } else {
            String response = form.get("query");
            JsonNode json = Json.parse(response);

            SolverSimpleQuery query = new SolverSimpleQuery(response);

            SolverController solverController = new SolverController();
            solverController.solve(query);
           


            Scenario solvedScenario = solverController.solve(query);

            

             return ok(show_charts.render(Json.stringify(Json.toJson(solvedScenario))));
            
        }
    }

    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("appRoutes", //appRoutes will be the JS object available in our view
                routes.javascript.Application.getLeagueTeamsBySeason()
        ));
    }

//TODO remove these ugly pieces of code!!!
    private void testSimpleMQI(){
        //String[] league = {"bundesliga", "premier_league", "primera_division"};
        List league = new ArrayList<Integer>();
        league.add(1);
        league.add(2);
        league.add(3);

        //Market value
        //String input = "defMV, midMV, offMV";
        List input = new ArrayList<Integer>();
        input.add(23); // Team value
        input.add(24); // Player Value
        //String output = "score";
        List output = new ArrayList<Integer>();
        output.add(6); // Rank
        boolean orientation = true;
        boolean data = true;
        int start = 2012;
        int seasons = 2; //More than 1
        int scale = 2; //MQI variable scale

        SolverSimpleQueryMQI query = new SolverSimpleQueryMQI("");
        query.setInput(input);
        query.setOutput(output);
        query.setLeague(league);
        query.setOrientation(orientation);
        query.setData(data);
        query.setStart(start);
        query.setSeasons(seasons);
        query.setScale(scale);

        List<ScenarioMQI> solvedScenariosMQI;

        SolverController solverController = new SolverController();
        solvedScenariosMQI = solverController.solve(query);

        System.out.println("SimpleMQI done, number of scenarios is: " + solvedScenariosMQI.size());
        for (ScenarioMQI scenarioMQI: solvedScenariosMQI
             ) {
            System.out.println("Scenario is:" + scenarioMQI);

        }


    }

    private void testComplexMQI(){

    }

    private void testComplex(){

    }





}
