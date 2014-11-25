package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
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
import util.Global;
import util.UrlUtil;
import views.html.main;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.util.List;

public class RequestController extends Controller {

    public static Result requestMessage() {

        List<ChainNode> nodes = getChainNodes();

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
        Key[] nodes_pubKey_reverse = new Key[nodes.size()];

        boolean invalidNode = false;
        for (int i = 0, j = nodes_pubKey_reverse.length - 1; i < nodes.size(); i++) {
            ChainNode n = nodes.get(i);
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
        for (int i = 0, j = nodes.size() - 1; i < nodes_pubKey_reverse.length; i++) {

            int currentChainNodeIdx = j - i;
            Logger.debug("currentChainNodeIdx: " + currentChainNodeIdx);

            Key key = nodes_pubKey_reverse[i];
            ChainNode node = nodes.get(currentChainNodeIdx);
            ObjectMapper mapper = new ObjectMapper();

            if (i == 0) {
                // encrypt TargetServiceRequest with public key of exit node
                // encryption result is payload in next message

                try {
                    NodeRequest nodeRequest = new NodeRequest();
                    TargetServiceRequest targetServiceRequest = new TargetServiceRequest();
                    nodeRequest.setTargetServiceRequest(targetServiceRequest);

                    String url = UrlUtil.createHttpUrl(
                            Global.getConfig().getQuoteConfig().getIp(),
                            Global.getConfig().getQuoteConfig().getPort(),
                            "quote");
                    targetServiceRequest.setUrl(url);
                    targetServiceRequest.setMethod("GET");
                    targetServiceRequest.setOriginatorPubKey(EncryptionUtil.keyToString(originator_key.getPublic()));

                    Logger.debug("target nodeRequest: " + nodeRequest);
                    payload = EncryptionUtil.encryptMessage(Json.stringify(Json.toJson(nodeRequest)), key);
                    Logger.debug("target encrypted payload: " + payload);

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
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    return badRequest();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    return badRequest();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    return badRequest();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    return badRequest();
                }

            } else {
                // Other Nodes

                if (payload == null || payload.isEmpty()) {
                    return badRequest();
                } else {

                    NodeRequest nodeRequest = new NodeRequest();
                    nodeRequest.setPayload(payload);
                    nodeRequest.setTarget(new NodeRequest.Target());
                    nodeRequest.getTarget().setIp(node.getIp());
                    nodeRequest.getTarget().setPort("" + node.getPort());

                    try {
                        Logger.debug("nodeRequest: " + nodeRequest);
                        payload = EncryptionUtil.encryptMessage(Json.stringify(Json.toJson(nodeRequest)), key);
                        Logger.debug("encrypted nodeRequest payload: " + payload);

                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Logger.error(e.getMessage());
                        return badRequest();
                    }
                }
            }
        }

        // all message are encrypted - payload has to be send to first node
        if (payload == null || payload.isEmpty()) {
            return badRequest();

        } else {
            ChainNode entryNode = nodes.get(0);
            EncryptedNodeRequest encryptedNodeRequest = new EncryptedNodeRequest();
            encryptedNodeRequest.setPayload(payload);

            Logger.debug("entryNode: " + entryNode);
            Logger.debug("send message to entryNode: " + encryptedNodeRequest);

            F.Promise<String> promise = WS.url(getUri(entryNode.getIp(), entryNode.getPort()))
                    .setContentType("application/json")
                    .post(Json.toJson(encryptedNodeRequest))
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
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                Logger.error(e.getMessage());
                return badRequest();
            } catch (BadPaddingException e) {
                e.printStackTrace();
                Logger.error(e.getMessage());
                return badRequest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Logger.error(e.getMessage());
                return badRequest();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
                Logger.error(e.getMessage());
                return badRequest();
            } catch (IOException e) {
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

    public static List<ChainNode> getChainNodes() {

        String url = UrlUtil.createHttpUrl(
                Global.getConfig().getDirectoryConfig().getIp(),
                Global.getConfig().getDirectoryConfig().getPort(),
                "chain");

        Logger.debug("Send chain request: " + url);

        F.Promise<String> promise = WS.url(url)
                .setContentType("application/json")
                .get()
                .map(new F.Function<WSResponse, String>() {
                    @Override
                    public String apply(WSResponse wsResponse) throws Throwable {
                        return wsResponse.getBody();
                    }
                });

        // wait 1000l for response of service
        String result = promise.get(1000l);

        Logger.debug("Chain result: " + result);

        ChainNodesResponse chainNodesResponse = Json.fromJson(Json.parse(result), ChainNodesResponse.class);
        return chainNodesResponse.getChainNodes();
    }

}
