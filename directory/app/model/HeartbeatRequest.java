package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HeartbeatRequest {

    @JsonProperty("secret")
    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "HeartbeatRequest{" +
                "secret='" + secret + '\'' +
                '}';
    }
}
