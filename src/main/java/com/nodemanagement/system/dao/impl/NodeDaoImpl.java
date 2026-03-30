package com.nodemanagement.system.dao.impl;

import com.nodemanagement.system.dao.NodeDao;
import com.nodemanagement.system.entity.Node;
import com.nodemanagement.system.repo.NodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeDaoImpl implements NodeDao {

    @Autowired
    private NodeRepo nodeRepo;

    @Override
    public Node getNodeByName(String nodeName) {
        return nodeRepo.findNodeByName(nodeName);
    }

    @Override
    public Node addNode(String nodeName) {
        Node node = nodeRepo.findNodeByName(nodeName);
        if (node != null) {
            throw new IllegalArgumentException("Node already exists: " + nodeName);
        }

        node = new Node(nodeName, null);
        return nodeRepo.saveAndFlush(node);
    }

    @Override
    public Node addChildNode(String parentName, String childName) {
        Node parent = nodeRepo.findNodeByName(parentName);
        if (parent == null) {
            throw new IllegalArgumentException("Parent not found: " + parentName);
        }

        if (nodeRepo.existsByParentIsNotNullAndName(childName)) {
            throw new IllegalArgumentException("Child already exists: " + childName);
        }

        Node child = new Node(childName, parent);
        parent.getChildren().add(child);

        return nodeRepo.saveAndFlush(parent);
    }

    @Override
    public void deleteNode(String nodeName) {
        Node node = nodeRepo.findNodeByName(nodeName);
        if (node == null) {
            throw new IllegalArgumentException("Node not found: " + nodeName);
        }

        nodeRepo.delete(node);
    }

    @Override
    public Node moveChildNode(String destParentName, String childName) {
        Node child = nodeRepo.findNodeByName(childName);
        Node newParent = nodeRepo.findNodeByName(destParentName);

        if (child == null) {
            throw new IllegalArgumentException("Child not found: " + childName);
        }
        if (newParent == null) {
            throw new IllegalArgumentException("Destination parent not found: " + destParentName);
        }

        // prevent moving to itself
        if (child.getName().equalsIgnoreCase(destParentName)) {
            throw new IllegalArgumentException("Cannot move node to itself");
        }

        if (isNodeDownline(newParent, child)) {
            throw new IllegalArgumentException("Cannot move node into its own downline");
        }

        Node oldParent = child.getParent();

        // remove from old parent
        if (oldParent != null) {
            oldParent.getChildren().remove(child);
        }

        // check duplicate under new parent
        boolean exists = newParent.getChildren().stream()
                .anyMatch(n -> childName.equalsIgnoreCase(n.getName()));
        if (exists) {
            throw new IllegalArgumentException("Child already exists under new parent: " + childName);
        }

        // set new relationship
        child.setParent(newParent);
        newParent.getChildren().add(child);

        return nodeRepo.saveAndFlush(newParent);
    }

    private boolean isNodeDownline(Node currentNode, Node targetNode) {
        if (currentNode == null) {
            return false;
        }

        if (currentNode.equals(targetNode)) {
            return true;
        }

        return isNodeDownline(currentNode.getParent(), targetNode);
    }
}
