package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.requestButton;

public class Application extends Controller {

    public static Result index() {
      return ok("Originator says hello!");
    }

    public static Result postRequestButton() {
        return ok(requestButton.render("Push the button"));
    }

}
