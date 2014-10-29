package controllers;

import model.ChainNode;
import play.libs.Json;
import play.mvc.Result;
import util.NodeStorage;
import views.html.monitor;

import java.util.Set;

import static play.mvc.Results.ok;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 20:30
 */
public class Monitor {

    public static Result index() {
        return ok(monitor.render(NodeStorage.getNodes(), NodeStorage.getHeartbeats()));
    }

    public static Result listNodes() {

        Set<ChainNode> nodes = NodeStorage.getNodes();
        for (ChainNode node : nodes) {
            node.setLastHeatbeat(NodeStorage.getLastHeartbeatForNode(node));
        }

        return ok(Json.toJson(nodes));
    }
}
