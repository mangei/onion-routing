package model;

/**
 * @author Mihai Lepadat
 *         Date: 11/2/14
 */
public class ServiceInfo {

    private String hostname;
    private String port;
    private String method;
    private String data;
    private String originatorPubKey;

    public ServiceInfo(String hostname, String port, String method, String data, String originatorPubKey) {
        this.hostname = hostname;
        this.port = port;
        this.method = method;
        this.data = data;
        this.originatorPubKey = originatorPubKey;
    }

}
