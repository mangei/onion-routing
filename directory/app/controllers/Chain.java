package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.ChainNode;
import play.libs.Json;
import play.mvc.Result;
import util.ChainNodeUsedComparator;
import util.NodeStorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Properties;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

/**
 * Created by markus on 15.11.2014.
 */
public class Chain {

    private static int chainLength = 3;
    private static int timeout = 10;

    static {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("./chain.conf"));
            chainLength = Integer.valueOf(p.getProperty("NODE_NUM", "3"));
            timeout = Integer.valueOf(p.getProperty("TIMEOUT", "10"));
        } catch (IOException e) {
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
