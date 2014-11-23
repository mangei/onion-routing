package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedNodeRequest {

    @JsonProperty("payload")
    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
