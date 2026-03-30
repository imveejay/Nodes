package com.nodemanagement.system.service.impl;

import com.nodemanagement.system.dao.NodeDao;
import com.nodemanagement.system.dto.NodeDto;
import com.nodemanagement.system.dto.NodeRequest;
import com.nodemanagement.system.dto.NodeResponseDto;
import com.nodemanagement.system.entity.Node;
import com.nodemanagement.system.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeDao nodeDao;

    @Override
    public NodeResponseDto getNodeByName(String nodeName) {
        NodeResponseDto response = new NodeResponseDto();
        response.setParent(mapToDto(nodeDao.getNodeByName(nodeName)));

        return response;
    }

    @Override
    public NodeResponseDto addNode(final NodeRequest nodeRequest) {
        Node node = new Node(nodeRequest.getName(), null);

        NodeResponseDto response = new NodeResponseDto();
        response.setParent(mapToDto(nodeDao.addNode(nodeRequest.getName())));

        return response;
    }

    @Override
    public NodeResponseDto addChildNode(final String parentName, final NodeRequest nodeRequest) {
        Node node = nodeDao.addChildNode(parentName, nodeRequest.getName());
        if (node == null) {
            throw new IllegalArgumentException("Unable to add child node to parent: " + parentName);
        }

        NodeResponseDto response = new NodeResponseDto();
        response.setParent(mapToDto(node));

        return response;
    }

    @Override
    public void deleteNode(String nodeName) {
        nodeDao.deleteNode(nodeName);
    }

    @Override
    public NodeResponseDto moveChildNode(String destParentName, String childName) {
        Node parent = nodeDao.moveChildNode(destParentName, childName);

        NodeResponseDto response = new NodeResponseDto();
        response.setParent(mapToDto(parent));

        return response;
    }

    private NodeDto mapToDto(Node node) {
        NodeDto dto = new NodeDto();
        dto.setName(node.getName());

        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            Set<NodeDto> childDtos = node.getChildren().stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toSet());
            dto.setChildren(childDtos);
        }

        return dto;
    }
}
