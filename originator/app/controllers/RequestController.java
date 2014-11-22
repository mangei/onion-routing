package controllers;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.*;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.twirl.api.Html;
import util.EncryptionHelper;
import views.html.main;

import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Lisa on 20.11.2014.
 */
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
            originator_key = EncryptionHelper.getRSAKeyPair();

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
                nodes_pubKey_reverse[j - i] = EncryptionHelper.stringToKey(n.getPublicKey());
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
                    ExitNodeMessage exitMessage = new ExitNodeMessage();
                    exitMessage.setHostname("http://testhost.at");
                    exitMessage.setMethod("testmethod");
                    exitMessage.setPort(9999);
                    exitMessage.setOriginator_public_key(EncryptionHelper.keyToString(originator_key.getPublic()));
                    exitMessage.setData("testdatatestdata");

                    payload = EncryptionHelper.encryptMessage(mapper.writeValueAsString(exitMessage), k);
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

                    TargetType target = new TargetType();
                    target.setIp(n.getIp());
                    target.setPort(n.getPort());

                    InterNodeMessage interMessage = new InterNodeMessage();
                    interMessage.setPayload(payload);
                    interMessage.setTarget(target);

                    try {

                        payload = EncryptionHelper.encryptMessage(mapper.writeValueAsString(interMessage), k);

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
            NodeSendMessage sendMessage = new NodeSendMessage();
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
                decryptServiceResponse = EncryptionHelper.decryptMessage(result, originator_key.getPrivate());
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

        if (!EncryptionHelper.isPublicKey(node.getPublicKey())) {
            Logger.debug("invalid public key");
            return false;
        }


        return true;
    }

    private static ChainNode[] createTestChainNode() {

        String node1String = "";

        try {

            // !-------- set chain node manually - only temp solution
            KeyPair key1 = EncryptionHelper.getRSAKeyPair();
            KeyPair key2 = EncryptionHelper.getRSAKeyPair();
            KeyPair key3 = EncryptionHelper.getRSAKeyPair();

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
            node1.setPublicKey(EncryptionHelper.keyToString(key1.getPublic()));
            node2.setPublicKey(EncryptionHelper.keyToString(key2.getPublic()));
            node3.setPublicKey(EncryptionHelper.keyToString(key3.getPublic()));
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
