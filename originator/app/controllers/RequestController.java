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
import java.security.PrivateKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestController extends Controller {

    private static long REQUEST_WAITING_TIME = 10000;

    public static F.Promise<Result> requestMessage() {

        // get chain node from directory
        F.Promise<List<ChainNode>> nodesPromise = getChainNodes();

        F.Promise<Result> responsePromise = nodesPromise.flatMap(new F.Function<List<ChainNode>, F.Promise<Result>>() {
            @Override
            public F.Promise<Result> apply(List<ChainNode> chainNodes) throws Throwable {

                final List<ChainNode> nodes = chainNodes;

                if (nodes == null || nodes.size() != 3) {
                    Result res = internalServerError("creating node chain failed");
                    return F.Promise.pure(res);
                }

                // Create our own public key pair
                KeyPair originatorKey = null;
                try {
                    originatorKey = EncryptionUtil.getRSAKeyPair();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    Result res = internalServerError("error - creating rsa key pair for originator");
                    return F.Promise.pure(res);
                }

                // Validate all the nodes received as the chain
                for (ChainNode node : nodes) {
                    if (!validateNode(node)) {
                        Logger.info("Node validation failed");
                        Result res = badRequest();
                        return F.Promise.pure(res);
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
                    Result res = badRequest();
                    return F.Promise.pure(res);
                }

                // all message are encrypted - payload has to be sent to first node
                if (payload == null || payload.isEmpty()) {
                    Result res = badRequest();
                    return F.Promise.pure(res);

                } else {
                    ChainNode entryNode = nodes.get(0);
                    EncryptedNodeRequest encryptedNodeRequest = new EncryptedNodeRequest();
                    encryptedNodeRequest.setPayload(payload);

                    Logger.debug("the entry node is  " + entryNode.getIp() + ":" + entryNode.getPort());

                    // post request to entry node
                    F.Promise<WSResponse> promise = WS.url(getUri(entryNode.getIp(), entryNode.getPort()))
                            .setContentType("application/json")
                            .post(Json.toJson(encryptedNodeRequest));

                    // map service response and produce response async
                    final PrivateKey originatorPrivateKey = originatorKey.getPrivate();

                    return promise.map(new F.Function<WSResponse, Result>() {
                        @Override
                        public Result apply(WSResponse wsResponse) throws Throwable {
                            String response = wsResponse.getBody();

                            try {
                                Logger.debug("received an encrypted response from the service");
                                //decrypt response with originator_private_key
                                String decryptServiceResponse = EncryptionUtil.decryptMessage(response, originatorPrivateKey);
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
                    });

                }
            }
        });

        return responsePromise;
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

    public static F.Promise<List<ChainNode>> getChainNodes() {

        String url = UrlUtil.createHttpUrl(
                Global.getConfig().getDirectoryConfig().getIp(),
                Global.getConfig().getDirectoryConfig().getPort(),
                "chain");

        Logger.debug("Sending chain node request");

        F.Promise<WSResponse> promise = WS.url(url)
                .setContentType("application/json")
                .get();

        return promise.map(new F.Function<WSResponse, List<ChainNode> >() {
            @Override
            public List<ChainNode> apply(WSResponse wsResponse) throws Throwable {
                String response = wsResponse.getBody();
                try {
                    Logger.debug("Successfully received chain");
                    ChainNodesResponse chainNodesResponse = Json.fromJson(Json.parse(response), ChainNodesResponse.class);
                    return chainNodesResponse.getChainNodes();
                } catch (Exception e) {
                    // some stupid runtime exception of jackson
                    return null;
                }

            }
        });

        // wait for response of service
        //String result = promise.get(REQUEST_WAITING_TIME);

     /*   ChainNodesResponse chainNodesResponse;
        try {
            chainNodesResponse = Json.fromJson(Json.parse(result), ChainNodesResponse.class);
        } catch (Exception e) {
            // some stupid runtime exception of jackson
            return null;
        }

        return chainNodesResponse.getChainNodes(); */
    }
}
