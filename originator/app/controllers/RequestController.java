package controllers;

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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestController extends Controller {

    public static Result requestMessage() {

        List<ChainNode> nodes = getChainNodes();

        if (nodes == null || nodes.size() != 3) {
            return internalServerError("creating node chain failed");
        }

        // !------ actual originator starts here

        // Create our own public key pair
        KeyPair originatorKey = null;
        try {
            originatorKey = EncryptionUtil.getRSAKeyPair();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
            return internalServerError("error - creating rsa key pair for originator");
        }

        // Validate all the nodes received as the chain
        for (ChainNode node : nodes) {
            if (!validateNode(node)) {
                Logger.info("Node validation failed");
                return badRequest();
            }
        }

        // since all chain nodes are valid nodes, encrypt the single messages for all nodes
        // in reverse order
        String payload = "";

        // encrypt messages
        try {
            for (int i = nodes.size() - 1; i >= 0; i--) {
                Logger.debug("Encrypting for number " + i);

                ChainNode node = nodes.get(i);
                Key nodeKey = EncryptionUtil.stringToKey(nodes.get(i).getPublicKey());
                NodeRequest nodeRequest = new NodeRequest();

                if (i == nodes.size() - 1) {
                    // we are encrypting a request for the last node in the chain - this happens first
                    TargetServiceRequest targetServiceRequest = new TargetServiceRequest();

                    // TODO this needs to be changed to allow arbitrary requests
                    String url = UrlUtil.createHttpUrl(
                            Global.getConfig().getQuoteConfig().getIp(),
                            Global.getConfig().getQuoteConfig().getPort(),
                            "quote");
                    targetServiceRequest.setUrl(url);
                    targetServiceRequest.setMethod("GET");
                    targetServiceRequest.setOriginatorPubKey(EncryptionUtil.keyToString(originatorKey.getPublic()));

                    nodeRequest.setTargetServiceRequest(targetServiceRequest);

                    Logger.debug("Encrypted for the last node in the chain: " + node.getIp() + ":" + node.getPort());

                } else {

                    ChainNode nextNode = nodes.get(i + 1);

                    nodeRequest.setPayload(payload);
                    nodeRequest.setTarget(new NodeRequest.Target());
                    nodeRequest.getTarget().setIp(nextNode.getIp());
                    nodeRequest.getTarget().setPort("" + nextNode.getPort());

                    Logger.debug("Encrypted for node " + node.getIp() + ":" + node.getPort());
                }

                // encrypt the playload
                payload = EncryptionUtil.encryptMessage(Json.stringify(Json.toJson(nodeRequest)), nodeKey);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
            return badRequest();
        }


        // all message are encrypted - payload has to be sent to first node
        if (payload == null || payload.isEmpty()) {
            return badRequest();

        } else {
            ChainNode entryNode = nodes.get(0);
            EncryptedNodeRequest encryptedNodeRequest = new EncryptedNodeRequest();
            encryptedNodeRequest.setPayload(payload);

            Logger.debug("the entry node is  " + entryNode.getIp() + ":" + entryNode.getPort());

            F.Promise<String> promise = WS.url(getUri(entryNode.getIp(), entryNode.getPort()))
                    .setContentType("application/json")
                    .post(Json.toJson(encryptedNodeRequest))
                    .map(new F.Function<WSResponse, String>() {
                        @Override
                        public String apply(WSResponse wsResponse) throws Throwable {
                            return wsResponse.getBody();
                        }
                    });

            // wait 2000L for response of service
            String result = promise.get(2000L);

            // decrypt response with originator_private_key
            try {
                Logger.debug("received an encrypted response from the service");
                String decryptServiceResponse = EncryptionUtil.decryptMessage(result, originatorKey.getPrivate());
                Logger.debug("the response is " + decryptServiceResponse);

                // build the request respones for prettier output
                Set<String> usedNodeURIs = new HashSet<String>();
                for (ChainNode node : nodes) {
                    usedNodeURIs.add(node.getIp() + ":" + node.getPort());
                }

                RequestResponse requestResponse = new RequestResponse();
                requestResponse.setResponsePayload(decryptServiceResponse);
                requestResponse.setUsedChainNodes(usedNodeURIs);

                return ok(Json.toJson(requestResponse));

            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                Logger.error("decryption failed");
                Logger.error(e.getMessage());
                return badRequest();
            }
        }
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

        Logger.debug("Sending chain node request");

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

        Logger.debug("Successfully received chain");

        ChainNodesResponse chainNodesResponse;
        try {
            chainNodesResponse = Json.fromJson(Json.parse(result), ChainNodesResponse.class);
        } catch (Exception e) {
            // some stupid runtime exception of jackson
            return null;
        }

        return chainNodesResponse.getChainNodes();
    }
}
