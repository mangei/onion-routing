package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.ChainNode;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import comparator.ChainNodeUsedComparator;
import util.NodeStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class Chain {

    private static int chainLength;
    private static int timeout;

    static {
        // set chain length
        try {
            String CHAIN_LENGTH = System.getenv("CHAIN_LENGTH");
            if(CHAIN_LENGTH == null){
                chainLength = 3;
                Logger.info("CHAIN_NODE_TIMEOUT environment variable is not set");
            }else{
                chainLength = Integer.valueOf(CHAIN_LENGTH);
            }
        } catch (NumberFormatException e) {
            chainLength = 3;
            Logger.info("invalid format of CHAIN_LENGTH environment variable");
        }
        // set node timeout
        try {
            String CHAIN_NODE_TIMEOUT = System.getenv("CHAIN_NODE_TIMEOUT");
            if(CHAIN_NODE_TIMEOUT == null){
                timeout = 15;
                Logger.info("CHAIN_NODE_TIMEOUT environment variable is not set");
            }else{
                timeout = Integer.valueOf(CHAIN_NODE_TIMEOUT);
            }
        } catch (NumberFormatException e) {
            timeout = 15;
            Logger.info("invalid format of CHAIN_NODE_TIMEOUT environment variable");
        }
    }

    public static synchronized Result getChain() {
        ObjectNode result = Json.newObject();
        ArrayNode chain = result.putArray("chain_nodes");
        int nodeCount = 0;
        ArrayList<ChainNode> nodeArrayList = new ArrayList();
        nodeArrayList.addAll(NodeStorage.getNodes());
        ObjectNode obj;
        ChainNode cn;
        long diff;
        Collections.sort(nodeArrayList, new ChainNodeUsedComparator());
        while (nodeArrayList.size() > 0 && chainLength > nodeCount) {
            cn = nodeArrayList.get(0);
            if (cn != null) {
                diff = Calendar.getInstance().getTimeInMillis() - NodeStorage.getLastHeartbeatForNode(cn);
                if (diff / 1000 < timeout) {
                    nodeCount++;
                    obj = chain.addObject();
                    obj.put("ip", cn.getIp());
                    obj.put("port", cn.getPort());
                    obj.put("public_key", cn.getPublicKey());
                    cn.setLasttimeused(Calendar.getInstance().getTimeInMillis());
                }
            }
            nodeArrayList.remove(0);
        }
        if (chainLength == nodeCount)
            return ok(result);
        result = Json.newObject();
        result.put("error", "not enough nodes");
        return badRequest(result);
    }
}
