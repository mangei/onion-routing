package model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lisa on 21.11.2014.
 */
public class ExitNodeMessage {

    @JsonProperty("hostname")
    private String hostname;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("method")
    private String method;

    @JsonProperty("data")
    private String data;

    @JsonProperty("originator_public_key")
    private String originator_public_key;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOriginator_public_key() {
        return originator_public_key;
    }

    public void setOriginator_public_key(String originator_public_key) {
        this.originator_public_key = originator_public_key;
    }
}

