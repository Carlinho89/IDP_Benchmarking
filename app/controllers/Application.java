package controllers;

import com.avaje.ebean.Model;
import models.Input;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.get_started;
import views.html.index;

import java.util.List;

public class Application extends Controller {

    public Result index() {

        return ok(index.render("Welcome"));
    }

    public Result getStarted() {

        List<Input> inputs = new Model.Finder(Input.class).all();
        return ok(get_started.render(inputs));
    }


}
