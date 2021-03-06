package util;

import controllers.Chain;
import model.ChainNode;
import play.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NodeStorage {

    private static Set<ChainNode> nodeSet;

    private static Map<ChainNode, Long> heartbeatMap;

    private static List<ChainNode> chainNodesList;

    static {
        nodeSet = Collections.newSetFromMap(new ConcurrentHashMap<ChainNode, Boolean>());
        heartbeatMap = new ConcurrentHashMap<ChainNode, Long>();
        chainNodesList = Collections.synchronizedList(new ArrayList<ChainNode>());
    }

    public static void addNode(ChainNode node) {
        nodeSet.add(node);
        heartbeatMap.put(node, System.currentTimeMillis());
        chainNodesList.add(0,node);
    }

    public static void updateHeartbeatForNode(ChainNode node) {
        heartbeatMap.put(node, System.currentTimeMillis());
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

    public static List<ChainNode> getChainNodeList() {
        return chainNodesList;
    }

    public static void updateHeartbeatForNode(String secret) throws UnknownNodeException {
        ChainNode secretNode = null;

        for (ChainNode node : nodeSet) {
            if (secret.equals(node.getSecret())) {
                secretNode = node;
            }
        }

        if (secretNode != null) {
            heartbeatMap.put(secretNode, System.currentTimeMillis());
        } else {
            Logger.debug("node for heartbeat not found");
            throw new UnknownNodeException();
        }

    }

    public static List<ChainNode> getActiveNodes() {
        Set<ChainNode> nodes = getNodes();
        ArrayList<ChainNode> activeNodes = new ArrayList<ChainNode>();

        for (ChainNode node : nodes) {
            if (Chain.isNodeAlive(node)) {
                activeNodes.add(node);
            }
        }

        return activeNodes;
    }
}
