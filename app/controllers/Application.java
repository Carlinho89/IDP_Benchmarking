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
        //TODO remove these tests!!!
        // this.testSimpleMQI(); WORKS!!!
        //this.testComplex();

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
        System.out.println("Complex");
        if (form.data().size() == 0) {
            return badRequest("Expecting some data");
        } else {
            String response = form.get("query");
           // JsonNode json = Json.parse(response);

            SolverComplexQuery query = new SolverComplexQuery(response);

            SolverController solverController = new SolverController();


            try {
                Scenario solvedScenario = solverController.solve(query);
                return ok(show_charts.render(Json.stringify(Json.toJson(solvedScenario)), solvedScenario));
            } catch(Exception e){
                e.printStackTrace();
            }

        return null;


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
        query.setSelectedInputs(input);
        query.setSelectedOutputs(output);
        query.setLeagueID(league);
        query.setOrientation(orientation);
        query.setData(data);
        query.setStart(start);
        query.setSeason(seasons);
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
//TODO remove these ugly pieces of code!!!
    private void testComplex(){
        //Set parameter selection
        //Input for offensive efficiency: Shots on target Goal (id 14), shots per game (id 7), Crosses (id 16)
        //Output for offensive efficiency: Goals scored(id 4)
        List<Integer> selectionOffIn = new ArrayList<Integer>();
        selectionOffIn.add(14);
        selectionOffIn.add(16);
        selectionOffIn.add(7);

        boolean superEff = true;

        List<Integer> selectionOffOut = new ArrayList<Integer>();
        selectionOffOut.add(4);

        //Input for defensive efficiency: Intercepts (id 13), 1 / shots conceived (id 11)
        //Output for defensive efficiency: 1 / Goals conceived (id 5)
        List<Integer> selectionDefIn = new ArrayList<Integer>();
        selectionDefIn.add(13);
        selectionDefIn.add(11);

        List<Integer> selectionDefOut = new ArrayList<Integer>();
        selectionDefOut.add(5);

        //Output for Athletic efficiency: Games won (id 1)
        List<Integer> selectionAthOut = new ArrayList<Integer>();
        selectionAthOut.add(1);

        //Output for Social efficiency: Average Age (id 21)
        List<Integer> selectionSocOut = new ArrayList<Integer>();
        selectionSocOut.add(21);

        List<String[]> dmuList = new ArrayList<String[]>();
        List<double[][]>overList = new ArrayList<double[][]>();
        List<String[]>refList = new ArrayList<String[]>();
        boolean[][]ramifications = {{false, false},{false, true},{false, false}};

        int league = 2; //"premier_league";
        int start = 2010;
        int seasons = 4;

        SolverComplexQuery query = new SolverComplexQuery("");
        query.setLeagueID(league);
        query.setStart(start);
        query.setRamifications(ramifications);
        query.setSelectionAthOut(selectionAthOut);
        query.setSelectionDefOut(selectionDefOut);
        query.setSelectionDefIn(selectionDefIn);
        query.setSelectionSocOut(selectionSocOut);
        query.setSelectionOffIn(selectionOffIn);
        query.setSelectionOffOut(selectionOffOut);
        query.setSuperEff(superEff);
        query.setSeason(seasons);

        SolverController solverController = new SolverController();

        try {
            solverController.solve(query);
            System.out.println("HELLO I'M WORKING");
        } catch (IloException e) {
            e.printStackTrace();
            System.out.println("HELLO I'M NOT WORKING");

        } finally {

        }


    }





}
