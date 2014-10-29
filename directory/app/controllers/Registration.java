package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.RegisterRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import util.DirectoryHelper;
import util.EncryptionHelper;

import static play.mvc.Controller.request;
import static play.mvc.Results.ok;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 14:54
 */
public class Registration {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result register() {

        JsonNode json = request().body().asJson();
        ObjectNode result = Json.newObject();

        if (json != null) {
            try {
                RegisterRequest registerRequest = Json.fromJson(json, RegisterRequest.class);

                if (registerRequest != null && DirectoryHelper.verifyRegisterRequest(registerRequest)) {
                    result.put("secret", DirectoryHelper.nextSessionId());

                    // TODO remove - just here for testing
                    result.put("debug", registerRequest.toString());
                    result.put("public_key", EncryptionHelper.keyToString(EncryptionHelper.getRSAKeyPair().getPublic()));
                    // END
                    
                } else {
                    result.put("error", "Couldn't validate JSON data");
                }
            } catch (Exception e) {
                Logger.info(ExceptionUtils.getStackTrace(e));
                result.put("error", "Couldn't parse JSON data - look at the API specs");
            }
        } else {
            result.put("error", "Expecting JSON");
        }

        return ok(result);
    }
}
