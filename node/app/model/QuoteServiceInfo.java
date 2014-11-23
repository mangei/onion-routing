package model;

/**
 * @author Mihai Lepadat
 *         Date: 11/2/14
 */
public class QuoteServiceInfo {

    private String hostname;
    private String port;
    private String method;
    private String data;

    public QuoteServiceInfo(String hostname, String port, String method, String data) {
        this.hostname = hostname;
        this.port = port;
        this.method = method;
        this.data = data;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public String getMethod() {
        return method;
    }

    public String getData() {
        return data;
    }

}
