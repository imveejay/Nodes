package com.nodemanagement.system.dao;

import com.nodemanagement.system.entity.Node;

public interface NodeDao {
    Node getNodeByName(String nodeName);

    Node addNode(String nodeName);

    Node addChildNode(String parentName, String childName);

    void deleteNode(String nodeName);

    Node moveChildNode(String destParentName, String childName);
}
