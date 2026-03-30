package com.nodemanagement.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeResponseDto {

    private NodeDto parent;

    public NodeDto getParent() {
        return parent;
    }

    public void setParent(NodeDto parent) {
        this.parent = parent;
    }
}
