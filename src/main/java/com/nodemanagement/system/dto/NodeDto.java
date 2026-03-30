package com.nodemanagement.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "node")
public class NodeDto {

    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlElementWrapper(localName = "children")
    @JacksonXmlProperty(localName = "child")
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
