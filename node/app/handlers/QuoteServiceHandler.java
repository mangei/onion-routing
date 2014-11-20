package handlers;

import model.QuoteServiceInfo;

/**
 * @author Mihai Lepadat
 *         Date: 11/2/14
 */
public class QuoteServiceHandler {

    private QuoteServiceInfo quoteServiceInfo;

    public QuoteServiceHandler(QuoteServiceInfo quoteServiceInfo) {
        this.quoteServiceInfo = quoteServiceInfo;
    }

    public String callService() {
        //TODO
        return "Hello!";
    }

}
