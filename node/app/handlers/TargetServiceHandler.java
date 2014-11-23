package handlers;

import model.TargetServiceRequest;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class TargetServiceHandler {

    private TargetServiceRequest targetServiceInfo;
    private final static long REQUEST_WAITING_TIME = 10000;

    public TargetServiceHandler(TargetServiceRequest quoteServiceInfo) {
        this.targetServiceInfo = quoteServiceInfo;
    }

    public String callService() {
        String url = targetServiceInfo.getUrl();
        String method = targetServiceInfo.getMethod();

        // TODO currently we only support GET requests
        if (method.equals("GET")) {
            F.Promise<String> promise = WS.url(url)
                    .setContentType("application/json")
                    .get()
                    .map(new F.Function<WSResponse, String>() {
                        @Override
                        public String apply(WSResponse wsResponse) throws Throwable {
                            return wsResponse.getBody();
                        }
                    });
            return promise.get(REQUEST_WAITING_TIME);
        } else {
            return "Hello!";
        }
    }
}
