package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.requestButton;

public class Application extends Controller {

    public static Result index() {
      return ok(index.render("Orginator says hello!"));
    }

    public static Result postRequestButton() {
        return ok(requestButton.render("Push the button"));
    }

}
