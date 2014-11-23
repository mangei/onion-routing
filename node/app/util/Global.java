package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mihai Lepadat
 *         Date: 11/18/14
 */
public class Global extends GlobalSettings {

    private final static long REQUEST_WAITING_TIME = 10000;
    private final static String ADDRESS_DIRECTORY_NODE = "127.0.0.1";
    private final static String PORT_DIRECTORY_NODE = "9001";
    private final static int HEARTBEAT_PERIOD = 5000;

    private static KeyHandler keyHandler;

    @Override
    public void onStart(Application app) {
        Logger.info("Application has started");

        keyHandler = new KeyHandler();

        final String secret = registerNode();

        startHeartbeat(secret);
    }

    private String registerNode() {
        String pubKey = keyHandler.getPublicKey();
        RegisterRequest registerRequest = buildRegisterRequest(pubKey);

        JsonNode json = Json.toJson(registerRequest);
        F.Promise<String> promise = WS.url(getUri(ADDRESS_DIRECTORY_NODE, PORT_DIRECTORY_NODE, "register"))
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

        return result;
    }

    private void startHeartbeat(final String secret) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.debug("Sending heartbeat...");
                F.Promise<String> promise = WS.url(getUri(ADDRESS_DIRECTORY_NODE, PORT_DIRECTORY_NODE, "heartbeat"))
                        .setContentType("application/json")
                        .put(JsonUtility.convertToJson(secret))
                        .map(new F.Function<WSResponse, String>() {
                            @Override
                            public String apply(WSResponse wsResponse) throws Throwable {
                                return wsResponse.getBody();
                            }
                        });
                Logger.debug("Heartbeat response: " + promise.get(REQUEST_WAITING_TIME));
            }
        }, HEARTBEAT_PERIOD, HEARTBEAT_PERIOD);
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown");
    }

    private static String getUri(String ip, String port, String path) {
        return "http://" + ip + ":" + port +"/" + path;
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
