package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.ChainNode;
import play.libs.Json;
import play.mvc.Result;
import util.NodeStorage;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

/**
 * Created by markus on 15.11.2014.
 */
public class Chain {

    public static Result getChain() {
        ObjectNode result = Json.newObject();
        ArrayNode chain = result.putArray("chain_nodes");
        ObjectNode obj;
        ChainNode cn;
        //TODO check if node is still available
        for(int i=0;i<3;i++){
            cn= NodeStorage.getLeastUsedNode();
            if(cn==null){
                result = Json.newObject();
                result.put("error", "not enough nodes");
                return badRequest(result);
            }
            obj=chain.addObject();
            obj.put("ip",cn.getIp());
            obj.put("port",cn.getPort());
            obj.put("public_key",cn.getPublicKey());
        }
        return ok(result);
    }
}
