package com.nodemanagement.system.service;

import com.nodemanagement.system.dto.NodeRequest;
import com.nodemanagement.system.dto.NodeResponseDto;

public interface NodeService {

    NodeResponseDto getNodeByName(String nodeName);

    NodeResponseDto addNode(NodeRequest nodeRequest);

    NodeResponseDto addChildNode(String parentName, NodeRequest nodeRequest);

    void deleteNode(String nodeName);

    NodeResponseDto moveChildNode(String destParentName, String childName);
}
