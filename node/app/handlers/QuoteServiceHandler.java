package handlers;

import model.QuoteServiceInfo;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.UriHelper;

/**
 * @author Mihai Lepadat
 *         Date: 11/2/14
 */
public class QuoteServiceHandler {

    private QuoteServiceInfo quoteServiceInfo;
    private final static long REQUEST_WAITING_TIME = 10000;

    public QuoteServiceHandler(QuoteServiceInfo quoteServiceInfo) {
        this.quoteServiceInfo = quoteServiceInfo;
    }

    public String callService() {

        String address = quoteServiceInfo.getHostname();
        String port = quoteServiceInfo.getPort();
        String method = quoteServiceInfo.getMethod();

        if (method.equals("GET")) {
            F.Promise<String> promise = WS.url(UriHelper.getUri(address, port, "quote"))
                    .setContentType("application/json")
                    .get()
                    .map(new F.Function<WSResponse, String>() {
                        @Override
                        public String apply(WSResponse wsResponse) throws Throwable {
                            return wsResponse.getBody();
                        }
                    });
            return promise.get(REQUEST_WAITING_TIME);
        }

        return "Hello!";
    }
}
