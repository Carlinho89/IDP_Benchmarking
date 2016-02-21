package controllers;

import models.Input;
import models.League;
import models.SeasonalData;
import models.Team;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.get_started;
import views.html.index;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;

import play.mvc.BodyParser;

public class Application extends Controller {

    public Result index() {

        return ok(index.render("Welcome"));
    }

    public Result getStarted() {

        //return ok(get_started.render(Input.getById(1), Input.getByType("Social")));
   	return ok(get_started.render(Input.getByType("Sporty"),Input.getByType("Social"),Input.getByType("Monetary")));
    
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

}
