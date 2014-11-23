package util;

import com.fasterxml.jackson.databind.JsonNode;
import handlers.KeyHandler;
import model.RegisterRequest;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

/**
 * @author Mihai Lepadat
 *         Date: 11/18/14
 */
public class Global extends GlobalSettings {

    private static long REQUEST_WAITING_TIME = 10000;

    private static KeyHandler keyHandler;

    @Override
    public void onStart(Application app) {
        Logger.info("Application has started");

        keyHandler = new KeyHandler();

        registerNode();
    }

    private void registerNode() {
        String pubKey = keyHandler.getPublicKey();
        RegisterRequest registerRequest = buildRegisterRequest(pubKey);

        JsonNode json = Json.toJson(registerRequest);
        F.Promise<String> promise = WS.url(getUri("localhost", "9001"))
                .setContentType("application/json")
                .post(json)
                .map(new F.Function<WSResponse, String>() {
                    @Override
                    public String apply(WSResponse wsResponse) throws Throwable {
                        return wsResponse.getBody();
                    }
                });

        String result = promise.get(REQUEST_WAITING_TIME);
        Logger.debug("Registration result: " + result);
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown");
    }

    private static String getUri(String ip, String port) {
        return "http://" + ip + ":" + port +"/register";
    }

    private RegisterRequest buildRegisterRequest(String pubKey) {
        String ip = Play.application().configuration().getString("http.address");
        int port = Play.application().configuration().getInt("http.port");

        Logger.debug("App ip: " + ip);
        Logger.debug("App port: " + port);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPublicKey(pubKey);
        registerRequest.setIp(ip);
        registerRequest.setPort(port);
        return registerRequest;
    }

    public static KeyHandler getKeyHandler() {
        return keyHandler;
    }
}
