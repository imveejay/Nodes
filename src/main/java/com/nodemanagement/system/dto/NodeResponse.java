package com.nodemanagement.system.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeResponse {

    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
}
