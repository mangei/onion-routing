package handlers;

import model.ServiceInfo;

/**
 * @author Mihai Lepadat
 *         Date: 11/2/14
 */
public class QuoteServiceHandler {

    private ServiceInfo serviceInfo;

    public QuoteServiceHandler(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public String callService() {
        //TODO
        return "Hello!";
    }

}
