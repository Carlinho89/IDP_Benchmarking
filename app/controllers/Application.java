package controllers;

import models.Input;
import models.League;
import models.SeasonalData;
import models.Team;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.get_started;
import views.html.index;

public class Application extends Controller {

    public Result index() {

        return ok(index.render("Welcome"));
    }

    public Result getStarted() {

        //return ok(get_started.render(Input.getById(1), Input.getByType("Social")));
   	return ok(get_started.render(Input.getAll()));
    }


}
