package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChainNode;
import model.EncryptedNodeRequest;
import model.NodeRequest;
import model.TargetServiceRequest;
import org.apache.commons.validator.routines.InetAddressValidator;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import util.EncryptionUtil;
import util.UrlUtil;
import views.html.main;

import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class RequestController extends Controller {

    public static Result requestMessage() {

        ChainNode[] nodes = createTestChainNode();

        if (nodes == null) {
            return internalServerError("creating test chain node failed");
        }

        // TODO: request chainnode from directory

        // !------ actual originator starts here

        KeyPair originator_key = null;
        String decryptServiceResponse = null;
        try {
            originator_key = EncryptionUtil.getRSAKeyPair();

        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            Logger.error(e1.getMessage());
            return internalServerError("error - creating rsa key pair");
        } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
            Logger.error(e1.getMessage());
            return internalServerError("error - creating rsa key pair");
        }

        // check valid ip, valid port, valid public key of all 3 nodes
        // create public key array of chain nodes in reverse order
        Key[] nodes_pubKey_reverse = new Key[nodes.length];

        boolean invalidNode = false;
        for (int i = 0, j = nodes_pubKey_reverse.length - 1; i < nodes.length; i++) {
            ChainNode n = nodes[i];
            if (!validateNode(n)) {
                Logger.info("validating nodes failed");
                return badRequest();
            } else {
                // save pub key in array - reverse order
                nodes_pubKey_reverse[j - i] = EncryptionUtil.stringToKey(n.getPublicKey());
            }
        }

        // since all chain nodes are valid nodes, encrypt the single messages for all nodes
        // in reverse order - according to public key array
        String payload = "";

        Logger.info("all test nodes are valid and reversed");

        // encrypt messages
        for (int i = 0, j = nodes.length - 1; i < nodes_pubKey_reverse.length; i++) {

            Key k = nodes_pubKey_reverse[i];
            ChainNode n = nodes[j - i];
            ObjectMapper mapper = new ObjectMapper();

            if (i == 0) {
                // encrypt ExitNodeMessage with public key of exit node
                // encryption result is payload in next message
                try {
                    TargetServiceRequest targetServiceRequest = new TargetServiceRequest();
                    targetServiceRequest.setUrl(UrlUtil.createHttpUrl("testhost.at", "9999", "quote"));
                    targetServiceRequest.setMethod("testmethod");
                    targetServiceRequest.setOriginatorPubKey(EncryptionUtil.keyToString(originator_key.getPublic()));
                    targetServiceRequest.setData("testdatatestdata");

                    payload = EncryptionUtil.encryptMessage(mapper.writeValueAsString(targetServiceRequest), k);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    return badRequest();
                } catch (IllegalBlockSizeException e) {
                    Logger.error(e.getMessage());
                    e.printStackTrace();
                    return badRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    return badRequest();
                }

            } else {

                if (payload == null || payload.isEmpty()) {
                    return badRequest();
                } else {

                    TargetServiceRequest targetServiceRequest = new TargetServiceRequest();
                    targetServiceRequest.setUrl(UrlUtil.createHttpUrl("" + n.getIp(), "" + n.getPort(), "quote"));

                    NodeRequest nodeRequest = new NodeRequest();
                    nodeRequest.setPayload(payload);
                    nodeRequest.setTargetServiceRequest(targetServiceRequest);

                    try {

                        payload = EncryptionUtil.encryptMessage(mapper.writeValueAsString(nodeRequest), k);

                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (JsonProcessingException e1) {
                        e1.printStackTrace();
                        Logger.error(e1.getMessage());
                        return badRequest();
                    }

                }

            }
        }

        // all message are encrypted - payload has to be send to node n[0]
        if (payload == null || payload.isEmpty()) {
            return badRequest();

        } else {
            ChainNode entryNode = nodes[0];
            EncryptedNodeRequest sendMessage = new EncryptedNodeRequest();
            sendMessage.setPayload(payload);
            JsonNode sendJson = Json.toJson(sendMessage);

            F.Promise<String> promise = WS.url(getUri(entryNode.getIp(), entryNode.getPort()))
                    .setContentType("application/json")
                    .post(sendJson)
                    .map(new F.Function<WSResponse, String>() {
                        @Override
                        public String apply(WSResponse wsResponse) throws Throwable {
                            return wsResponse.getBody();
                        }
                    });

            // wait 1000l for response of service
            String result = promise.get(1000l);

            // decrypt response with originator_private_key

            try {
                Logger.debug("response from service: " + result);
                decryptServiceResponse = EncryptionUtil.decryptMessage(result, originator_key.getPrivate());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                Logger.error(e.getMessage());
                return badRequest();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
                Logger.error(e.getMessage());
                return badRequest();
            }


        }

        return ok(main.render("request message", Html.apply("<div>" + decryptServiceResponse + " </div>")));
    }

    private static String getUri(String ip, Integer port) {
        return "http://" + ip + ":" + port + "/request";
    }

    private static boolean validateNode(ChainNode node) {

        if (node.getIp() == null || node.getPort() == null || node.getPublicKey() == null) {
            Logger.debug("node variable is null");
            return false;
        }

        if (!InetAddressValidator.getInstance().isValid(node.getIp())) {
            Logger.debug("invalid ip");
            return false;
        }

        if (node.getPort() <= 0 || node.getPort() >= 65535) {
            Logger.debug("invalid port");
            return false;
        }

        if (!EncryptionUtil.isPublicKey(node.getPublicKey())) {
            Logger.debug("invalid public key");
            return false;
        }


        return true;
    }

    private static ChainNode[] createTestChainNode() {

        String node1String = "";

        try {

            // !-------- set chain node manually - only temp solution
            KeyPair key1 = EncryptionUtil.getRSAKeyPair();
            KeyPair key2 = EncryptionUtil.getRSAKeyPair();
            KeyPair key3 = EncryptionUtil.getRSAKeyPair();

            List<ChainNode> list_nodes_in = new ArrayList<ChainNode>();
            ChainNode node1 = new ChainNode();
            ChainNode node2 = new ChainNode();
            ChainNode node3 = new ChainNode();
            node1.setIp("127.0.0.1");
            node2.setIp("127.0.0.1");
            node3.setIp("127.0.0.1");
            node1.setPort(9001);
            node2.setPort(9002);
            node3.setPort(9003);
            node1.setPublicKey(EncryptionUtil.keyToString(key1.getPublic()));
            node2.setPublicKey(EncryptionUtil.keyToString(key2.getPublic()));
            node3.setPublicKey(EncryptionUtil.keyToString(key3.getPublic()));
            list_nodes_in.add(node1);
        //    list_nodes_in.add(node2);
        //    list_nodes_in.add(node3);

            JsonNode jsonNode = Json.toJson(list_nodes_in);

            ObjectMapper mapper = new ObjectMapper();
            String nodeString = mapper.writeValueAsString(jsonNode);
            ChainNode[] nodes = Json.fromJson(jsonNode, ChainNode[].class);

            for (int i = 0; i < list_nodes_in.size(); i++) {
                node1String += mapper.writeValueAsString(nodes[i]);
                node1String += "\n";
            }

            Logger.info("test chain nodes: " + node1String);

            return nodes;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
