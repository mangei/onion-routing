package model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lisa on 21.11.2014.
 */
public class NodeSendMessage {

    @JsonProperty("payload")
    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
