package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetServiceInfo {

    @JsonProperty("url")
    private String url;
    @JsonProperty("method")
    private String method;
    @JsonProperty("data")
    private String data;
    @JsonProperty("originator_public_key")
    private String originatorPubKey;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getOriginatorPubKey() {
        return originatorPubKey;
    }

    public void setOriginatorPubKey(String originatorPubKey) {
        this.originatorPubKey = originatorPubKey;
    }
}
