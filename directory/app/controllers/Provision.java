package controllers;

import play.mvc.Result;
import util.NodeStorage;
import util.Provisioner;
import views.html.monitor;
import views.html.provision;
import views.html.provisionlog;

import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 22.12.2014
 * Time: 22:22
 */
public class Provision {

    public static Result index() {
        return ok(provision.render(Provisioner.getLogFiles()));
    }

    public static Result show(Integer id) {
        if (id >= 0) {
            return ok(provisionlog.render(Provisioner.getContentOfNthLogFile(id), id));
        } else {
            return notFound();
        }
    }

}
