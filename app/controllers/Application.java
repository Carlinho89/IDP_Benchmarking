package controllers;

//CPLEX Libs

import com.fasterxml.jackson.databind.JsonNode;
import models.Input;
import models.Team;

import play.Routes;
import play.libs.Json;
import play.mvc.BodyParser;
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
/*
        CplexController cplexController = new CplexController();
        cplexController.model1(); */

        //return ok(get_started.render(Input.getById(1), Input.getByType("Social")));
        return ok(get_started.render(Input.getByType("Sporty"),Input.getByType("Social"),Input.getByType("Monetary"), Input.getOutputs()));

    }

    public  Result getLeagueTeamsBySeason(int year, int league_id)
    {
        return ok(Json.toJson(Team.getAllbySeason(year, league_id)));
    }




    @BodyParser.Of(BodyParser.Json.class)
    public  Result sayHello() {
        JsonNode json = request().body().asJson();
        String name = json.findPath("leagueID").textValue();
        if(name == null) {
            return badRequest("Missing parameter [name]"+request().body().asJson());
        } else {
            return ok(json);

        }
    }


    public  Result showJSON() {
        JsonNode json = request().body().asJson();
        String a = json.path("leagueID").asText();
        return ok(index.render(a));
    }


    public  Result jsRoutes()
    {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("appRoutes", //appRoutes will be the JS object available in our view
                routes.javascript.Application.getLeagueTeamsBySeason()
               ));
    }
}
