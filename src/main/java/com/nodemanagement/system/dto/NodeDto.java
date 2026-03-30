package com.nodemanagement.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeDto {

    private String name;
    private Set<NodeDto> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<NodeDto> getChildren() {
        return children;
    }

    public void setChildren(Set<NodeDto> children) {
        this.children = children;
    }
}
