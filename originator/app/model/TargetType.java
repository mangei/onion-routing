package model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lisa on 21.11.2014.
 */
public class TargetType {

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("port")
    private Integer port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
