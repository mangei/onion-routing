package handlers;

import model.TargetServiceRequest;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class TargetServiceHandler {

    private final static long REQUEST_WAITING_TIME = 10000;

    private TargetServiceRequest targetServiceRequest;

    public TargetServiceHandler(TargetServiceRequest targetServiceRequest) {
        this.targetServiceRequest = targetServiceRequest;
    }

    public F.Promise<String> callService() {
        String url = targetServiceRequest.getUrl();
        String method = targetServiceRequest.getMethod();

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
            return promise;
        } else {
            return F.Promise.pure("Hello");
        }
    }
}
