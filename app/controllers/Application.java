package controllers;

//CPLEX Libs

import com.fasterxml.jackson.databind.JsonNode;
import models.Input;
import models.League;
import models.Team;

import play.Routes;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;


import java.util.ArrayList;

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
        GarciaSanchez garciaSanchez = new GarciaSanchez();
        garciaSanchez.test();

        return ok(get_started.render(Input.getByType("Sporty"), Input.getByType("Social"), Input.getByType("Monetary"), Input.getOutputs()));

    }

    public Result getLeagueTeamsBySeason(int year, int league_id) {
        return ok(Json.toJson(Team.getAllbySeason(year, league_id)));
    }


    @BodyParser.Of(BodyParser.Json.class)
    public Result sayHello() {
        JsonNode json = request().body().asJson();
        String name = json.findPath("leagueID").textValue();
        if (name == null) {
            return badRequest("Missing parameter [name]" + request().body().asJson());
        } else {
            return ok(json);

        }
    }


    public Result showJSON() {
        JsonNode json = request().body().asJson();
        String a = json.path("leagueID").asText();
        return ok(index.render(a));
    }


    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("appRoutes", //appRoutes will be the JS object available in our view
                routes.javascript.Application.getLeagueTeamsBySeason()
        ));
    }
}
