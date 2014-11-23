package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import handlers.QuoteServiceHandler;
import model.QuoteServiceInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import util.EncodingUtility;
import util.Global;
import util.JsonUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Mihai Lepadat
 *         Date: 11/1/14
 */
public class RequestController extends Controller {

    private static long REQUEST_WAITING_TIME = 1000;

    // curl --header "Content-type: application/json" --request POST --data-binary @data.json http://localhost:9000/request
    public static Result requestMessage() {
        Logger.info("Process a new request");
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            String payload = EncodingUtility.decodeMessage(JsonUtility.getResource(json, "payload"));
            Logger.debug("decoded payload:" + payload.replaceAll("\r", ""));

            json = JsonUtility.convertToJson(payload);

            String nextPayload = JsonUtility.getResource(json, "payload");
            String ip = JsonUtility.getResource(json, "target/ip");
            String port = JsonUtility.getResource(json, "target/port");

            Logger.debug("next payload (encoded): " + nextPayload);
            Logger.debug("ip: " + ip);
            Logger.debug("port: " + port);

            if (bothNotEmpty(ip, port)) {
                return requestNextNode(nextPayload, ip, port);
            } else {
                return produceResponse(payload);
            }
        }
    }

    private static Result requestNextNode(String payload, String ip, String port) {
        Logger.info("Request next node");
        ObjectNode jsonRequest = Json.newObject();
        jsonRequest.put("payload", payload);

        F.Promise<String> promise = WS.url(getUri(ip, port))
                .setContentType("application/json")
                .post(jsonRequest)
                .map(new F.Function<WSResponse, String>() {
                    @Override
                    public String apply(WSResponse wsResponse) throws Throwable {
                        return wsResponse.getBody();
                    }
                });

        String result = promise.get(REQUEST_WAITING_TIME);
        Logger.info("Got message: " + result);
        return ok(result);
    }

    private static String getUri(String ip, String port) {
        return "http://" + ip + ":" + port +"/request";
    }

    private static Result produceResponse(String payload) {
        JsonNode json = JsonUtility.convertToJson(payload);
        String hostname = JsonUtility.getResource(json, "hostname");
        String port = JsonUtility.getResource(json, "port");
        String method = JsonUtility.getResource(json, "method");
        String data = JsonUtility.getResource(json, "data");
        String originatorPubKey = JsonUtility.getResource(json, "originator_public_key");

        QuoteServiceInfo quoteServiceInfo = new QuoteServiceInfo(hostname, port, method, data, originatorPubKey);
        QuoteServiceHandler quoteServiceHandler = new QuoteServiceHandler(quoteServiceInfo);
        String response = quoteServiceHandler.callService();
        return ok(response);
    }

    private static boolean bothNotEmpty(String ip, String port) {
        return !ip.isEmpty() && !port.isEmpty();
    }
}
