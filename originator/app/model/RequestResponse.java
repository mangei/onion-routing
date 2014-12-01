package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 01.12.2014
 * Time: 23:51
 */
public class RequestResponse {

    @JsonProperty("payload")
    private String responsePayload;

    @JsonProperty("usedNodes")
    private Set<String> usedChainNodes;

    public RequestResponse() {
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public Set<String> getUsedChainNodes() {
        return usedChainNodes;
    }

    public void setUsedChainNodes(Set<String> usedChainNodes) {
        this.usedChainNodes = usedChainNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestResponse that = (RequestResponse) o;

        if (responsePayload != null ? !responsePayload.equals(that.responsePayload) : that.responsePayload != null)
            return false;
        if (usedChainNodes != null ? !usedChainNodes.equals(that.usedChainNodes) : that.usedChainNodes != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = responsePayload != null ? responsePayload.hashCode() : 0;
        result = 31 * result + (usedChainNodes != null ? usedChainNodes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RequestResponse{" +
                "responsePayload='" + responsePayload + '\'' +
                ", usedChainNodes=" + usedChainNodes +
                '}';
    }
}
