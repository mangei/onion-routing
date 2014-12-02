package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.*;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import util.DirectoryUtil;
import util.NodeStorage;
import util.UnknownNodeException;

import static play.mvc.Controller.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.ok;

public class Register {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result register() {
        JsonNode json = request().body().asJson();
        if (json != null) {
            try {
                RegisterRequest registerRequest = Json.fromJson(json, RegisterRequest.class);

                Logger.info("register request: " + registerRequest);

                if (registerRequest != null && DirectoryUtil.verifyRegisterRequest(registerRequest)) {
                    String secret = DirectoryUtil.nextSessionId();

                    RegisterResponse registerResponse = new RegisterResponse();
                    registerResponse.setSecret(secret);

                    ChainNode node = new ChainNode();
                    node.setIp(registerRequest.getIp());
                    node.setPublicKey(registerRequest.getPublicKey());
                    node.setPort(registerRequest.getPort());
                    node.setSecret(secret);
                    node.setRegisterDate(System.currentTimeMillis());
                    node.setLastHeatbeat(System.currentTimeMillis());

                    NodeStorage.addNode(node);

                    Logger.info("node was added: " + node);

                    // TODO remove - just here for testing
                    // result.put("public_key", EncryptionHelper.keyToString(EncryptionHelper.getRSAKeyPair().getPublic()));
                    // END

                    return ok(Json.toJson(registerResponse));
                } else {
                    return badRequest(Json.toJson(new ErrorResponse("Couldn't validate JSON data")));
                }
            } catch (Exception e) {
                return badRequest(Json.toJson(new ErrorResponse("Couldn't parse JSON data - look at the API specs")));
            }
        } else {
            return badRequest(Json.toJson(new ErrorResponse("Expecting JSON")));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result heartbeat() {
        JsonNode json = request().body().asJson();
        try {
            if (json != null) {
                HeartbeatRequest heartbeatRequest = Json.fromJson(json, HeartbeatRequest.class);
                String secret = heartbeatRequest.getSecret();
                NodeStorage.updateHeartbeatForNode(secret);
                return ok();
            }
        } catch (UnknownNodeException e) {
            Logger.error("Unknown node for heartbeat");
            Logger.error(e.getMessage());
            e.printStackTrace();
            return badRequest(Json.toJson(new ErrorResponse("invalid request")));
        }
        return internalServerError();
    }
}
