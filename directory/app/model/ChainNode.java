package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 17:47
 */
public class ChainNode {

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("register_date")
    private Long registerDate;

    // note: due to the way the nodestorage works, you need to update/set this value MANUALLY
    @JsonProperty("last_heartbeat")
    private Long lastHeatbeat;

    @JsonIgnore
    private String secret;

    public ChainNode() {
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Long registerDate) {
        this.registerDate = registerDate;
    }

    public Long getLastHeatbeat() {
        return lastHeatbeat;
    }

    public void setLastHeatbeat(Long lastHeatbeat) {
        this.lastHeatbeat = lastHeatbeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChainNode chainNode = (ChainNode) o;

        if (ip != null ? !ip.equals(chainNode.ip) : chainNode.ip != null) return false;
        if (lastHeatbeat != null ? !lastHeatbeat.equals(chainNode.lastHeatbeat) : chainNode.lastHeatbeat != null)
            return false;
        if (port != null ? !port.equals(chainNode.port) : chainNode.port != null) return false;
        if (publicKey != null ? !publicKey.equals(chainNode.publicKey) : chainNode.publicKey != null) return false;
        if (registerDate != null ? !registerDate.equals(chainNode.registerDate) : chainNode.registerDate != null)
            return false;
        if (secret != null ? !secret.equals(chainNode.secret) : chainNode.secret != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        result = 31 * result + (registerDate != null ? registerDate.hashCode() : 0);
        result = 31 * result + (lastHeatbeat != null ? lastHeatbeat.hashCode() : 0);
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }
}
