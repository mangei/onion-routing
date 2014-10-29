package model;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 17:47
 */
public class ChainNode {

    private String ip;
    private Integer port;
    private String publicKey;
    private String secret;

    public ChainNode() {
    }

    public ChainNode(String ip, Integer port, String publicKey, String secret) {
        this.ip = ip;
        this.port = port;
        this.publicKey = publicKey;
        this.secret = secret;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChainNode chainNode = (ChainNode) o;

        if (ip != null ? !ip.equals(chainNode.ip) : chainNode.ip != null) return false;
        if (port != null ? !port.equals(chainNode.port) : chainNode.port != null) return false;
        if (publicKey != null ? !publicKey.equals(chainNode.publicKey) : chainNode.publicKey != null) return false;
        if (secret != null ? !secret.equals(chainNode.secret) : chainNode.secret != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }
}
