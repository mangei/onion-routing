package model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lisa on 21.11.2014.
 */
public class InterNodeMessage {

    @JsonProperty("payload")
    private String payload;

    @JsonProperty("target")
    private TargetType target;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public TargetType getTarget() {
        return target;
    }

    public void setTarget(TargetType target) {
        this.target = target;
    }
}
