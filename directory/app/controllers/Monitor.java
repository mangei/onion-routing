package controllers;

import model.ChainNode;
import model.ListNodesResponse;
import play.libs.Json;
import play.mvc.Result;
import util.NodeStorage;
import views.html.monitor;

import java.util.Set;

import static play.mvc.Results.ok;

public class Monitor {

    public static Result index() {
        return ok(monitor.render(NodeStorage.getNodes(), NodeStorage.getHeartbeats()));
    }

    public static Result listNodes() {
        ListNodesResponse listNodesResponse = new ListNodesResponse();

        Set<ChainNode> nodes = NodeStorage.getNodes();
        for (ChainNode node : nodes) {
            node.setLastHeatbeat(NodeStorage.getLastHeartbeatForNode(node));
            listNodesResponse.getChainNodes().add(node);
        }

        return ok(Json.toJson(listNodesResponse));
    }
}
