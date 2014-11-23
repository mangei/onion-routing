package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.ChainNode;
import model.RegisterRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import util.DirectoryUtil;
import util.NodeStorage;

import static play.mvc.Controller.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class Register {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result register() {

        JsonNode json = request().body().asJson();
        ObjectNode result = Json.newObject();

        if (json != null) {
            try {
                RegisterRequest registerRequest = Json.fromJson(json, RegisterRequest.class);

                if (registerRequest != null && DirectoryUtil.verifyRegisterRequest(registerRequest)) {
                    String secret = DirectoryUtil.nextSessionId();
                    result.put("secret", secret);

                    ChainNode node = new ChainNode();
                    node.setIp(registerRequest.getIp());
                    node.setPublicKey(registerRequest.getPublicKey());
                    node.setPort(registerRequest.getPort());
                    node.setSecret(secret);
                    node.setRegisterDate(System.currentTimeMillis());
                    node.setLastHeatbeat(System.currentTimeMillis());

                    NodeStorage.addNode(node);

                    // TODO remove - just here for testing
                    // result.put("public_key", EncryptionHelper.keyToString(EncryptionHelper.getRSAKeyPair().getPublic()));
                    // END

                    return ok(result);

                } else {
                    result.put("error", "Couldn't validate JSON data");
                    return badRequest(result);
                }
            } catch (Exception e) {
                Logger.info(ExceptionUtils.getStackTrace(e));
                result.put("error", "Couldn't parse JSON data - look at the API specs");
                return badRequest(result);

            }
        } else {
            result.put("error", "Expecting JSON");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result heartbeat() {
        JsonNode json = request().body().asJson();
        ObjectNode result = Json.newObject();

        if (json != null && json.get("secret") != null) {
            String secret = json.get("secret").textValue();

            NodeStorage.updateHeartbeatForNode(secret);

            return ok(result);

        } else {
            result.put("error", "invalid request json");
            return badRequest(result);
        }
    }
}
