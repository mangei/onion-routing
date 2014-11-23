package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
      return ok("hello");
      //  return ok(index.render("Your new application is ready."));
    }

    public static Result postRequestButton() {
        return ok(requestButton.render("Push the button"));
    }

}
