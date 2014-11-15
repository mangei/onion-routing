package util;

import model.ChainNode;
import play.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 17:46
 */
public class NodeStorage {

    private static Set<ChainNode> nodeSet;

    private static Map<ChainNode, Long> heartbeatMap;

    static {
        nodeSet = new HashSet<ChainNode>();
        heartbeatMap = new HashMap<ChainNode, Long>();
    }

    public static void addNode(ChainNode node) {
        nodeSet.add(node);
        heartbeatMap.put(node, System.currentTimeMillis());
    }

    public static void updateHeartbeatForNode(ChainNode node) {
        heartbeatMap.put(node, System.currentTimeMillis());
    }

    public static ChainNode getLeastUsedNode(){
        if(nodeSet.size()<=0) return null;
        ChainNode cn = Collections.min(nodeSet,new ChainNodeUsedComparator());
        cn.setLasttimeused(Calendar.getInstance().getTimeInMillis());
        return cn;
    }

    public static Set<ChainNode> getNodes(String ip, Integer port) {
        Set<ChainNode> retSet = new HashSet<ChainNode>();

        for (ChainNode node : nodeSet) {
            if (ip.equals(node.getIp()) && port.equals(node.getPort())) {
                retSet.add(node);
            }
        }

        return retSet;
    }

    public static ChainNode getMostRecentlyUpdatedNode(String ip, Integer port) {
        Set<ChainNode> nodeSet = NodeStorage.getNodes(ip, port);
        ChainNode mostRecentNode = null;

        for (ChainNode node : nodeSet) {

            if (mostRecentNode == null) {
                mostRecentNode = node;

            } else if (getLastHeartbeatForNode(node) != null &&
                    getLastHeartbeatForNode(mostRecentNode) != null) {

                long existingHeartbeat = getLastHeartbeatForNode(mostRecentNode);
                long newHeartbeat = getLastHeartbeatForNode(node);

                if (newHeartbeat > existingHeartbeat) {
                    mostRecentNode = node;
                }
            }
        }

        return mostRecentNode;
    }

    public static Long getLastHeartbeatForNode(ChainNode node) {
        return heartbeatMap.get(node);
    }

    public static Set<ChainNode> getNodes() {
        return nodeSet;
    }

    public static Map<ChainNode, Long> getHeartbeats() {
        return heartbeatMap;
    }

    public static void updateHeartbeatForNode(String secret) {
        ChainNode secretNode = null;

        for (ChainNode node : nodeSet) {
            if (secret.equals(node.getSecret())) {
                secretNode = node;
            }
        }

        if (secretNode != null) {
            heartbeatMap.put(secretNode, System.currentTimeMillis());
        } else {
            Logger.info("node for heartbeat not found");
        }

    }
}
