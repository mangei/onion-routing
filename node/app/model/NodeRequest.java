package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeRequest {

    @JsonProperty("payload")
    private String payload;
    @JsonProperty("target")
    private Target target;
    @JsonProperty("service")
    private TargetServiceInfo targetServiceInfo;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public TargetServiceInfo getTargetServiceInfo() {
        return targetServiceInfo;
    }

    public void setTargetServiceInfo(TargetServiceInfo targetServiceInfo) {
        this.targetServiceInfo = targetServiceInfo;
    }

    public static class Target {
        @JsonProperty("ip")
        private String ip;
        @JsonProperty("port")
        private String port;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }

    @Override
    public String toString() {
        return "NodeRequest{" +
                "payload='" + payload + '\'' +
                ", target=" + target +
                ", targetServiceInfo=" + targetServiceInfo +
                '}';
    }
}
