package controllers;

import com.avaje.ebean.Model;
import models.Input;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

public class Application extends Controller {

    public Result index() {
        List<Input> inputs = new Model.Finder(Input.class).all();

        String inputsString = "Count = " + inputs.size();
        for (Input input: inputs) {
            inputsString += " \n" + input.name;
        }
        return ok(index.render(inputsString));
    }

}
