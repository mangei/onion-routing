package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.net.httpserver.Filter;

/**
 * Created by Lisa on 20.11.2014.
 */
public class ChainNode {

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("public_key")
    private String publicKey;


    public ChainNode(){
    }

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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChainNode chainNode = (ChainNode) o;

        if (ip != null ? !ip.equals(chainNode.ip) : chainNode.ip != null) return false;
        if (port != null ? !port.equals(chainNode.port) : chainNode.port != null) return false;
        if (publicKey != null ? !publicKey.equals(chainNode.publicKey) : chainNode.publicKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        return result;
    }

}
