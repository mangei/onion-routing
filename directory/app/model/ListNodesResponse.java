package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ListNodesResponse {
    @JsonProperty("nodes")
    private List<ChainNode> chainNodes = new ArrayList<>();

    public List<ChainNode> getChainNodes() {
        return chainNodes;
    }

    public void setChainNodes(List<ChainNode> chainNodes) {
        this.chainNodes = chainNodes;
    }

    @Override
    public String toString() {
        return "ListNodesResponse{" +
                "chainNodes=" + chainNodes +
                '}';
    }
}
