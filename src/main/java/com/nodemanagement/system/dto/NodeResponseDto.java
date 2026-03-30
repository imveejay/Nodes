package com.nodemanagement.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "nodeResponse")
public class NodeResponseDto {

    @JacksonXmlProperty(localName = "parent")
    private NodeDto parent;

    public NodeDto getParent() {
        return parent;
    }

    public void setParent(NodeDto parent) {
        this.parent = parent;
    }
}
