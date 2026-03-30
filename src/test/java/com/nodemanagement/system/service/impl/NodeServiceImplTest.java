package com.nodemanagement.system.service.impl;

import com.nodemanagement.system.dao.NodeDao;
import com.nodemanagement.system.dto.NodeRequest;
import com.nodemanagement.system.dto.NodeResponseDto;
import com.nodemanagement.system.entity.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeServiceImplTest {

    @Mock
    private NodeDao nodeDao;

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Test
    void test_getNodeByName() {
        Node parent = new Node("parent", null);
        Node child1 = new Node("child-1", parent);
        Node child2 = new Node("child-2", parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);

        when(nodeDao.getNodeByName("parent")).thenReturn(parent);

        NodeResponseDto response = nodeService.getNodeByName("parent");

        assertNotNull(response);
        assertNotNull(response.getParent());
        assertEquals("parent", response.getParent().getName());
        assertNotNull(response.getParent().getChildren());
        assertEquals(2, response.getParent().getChildren().size());
        verify(nodeDao).getNodeByName("parent");
    }

    @Test
    void test_addNode() {
        Node savedNode = new Node("parent", null);
        NodeRequest request = new NodeRequest();
        request.setName("parent");

        when(nodeDao.addNode("parent")).thenReturn(savedNode);

        NodeResponseDto response = nodeService.addNode(request);

        assertNotNull(response);
        assertNotNull(response.getParent());
        assertEquals("parent", response.getParent().getName());
        verify(nodeDao).addNode("parent");
    }

    @Test
    void test_addChildNode_shouldReturnMappedParentWhenSuccess() {
        Node parent = new Node("parent", null);
        Node child = new Node("child", parent);
        parent.getChildren().add(child);

        NodeRequest request = new NodeRequest();
        request.setName("child");

        when(nodeDao.addChildNode("parent", "child")).thenReturn(parent);

        NodeResponseDto response = nodeService.addChildNode("parent", request);

        assertNotNull(response);
        assertEquals("parent", response.getParent().getName());
        assertNotNull(response.getParent().getChildren());
        assertEquals(1, response.getParent().getChildren().size());
        verify(nodeDao).addChildNode("parent", "child");
    }

    @Test
    void test_addChildNode_shouldThrowWhenNotSuccess() {
        NodeRequest request = new NodeRequest();
        request.setName("child");

        when(nodeDao.addChildNode("parent", "child")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeService.addChildNode("parent", request)
        );

        assertEquals("Unable to add child node to parent: parent", ex.getMessage());
        verify(nodeDao).addChildNode("parent", "child");
    }

    @Test
    void test_deleteNode() {
        nodeService.deleteNode("to-delete");

        verify(nodeDao).deleteNode("to-delete");
    }

    @Test
    void test_moveChildNode() {
        Node destinationParent = new Node("newParent", null);
        Node movedChild = new Node("child", destinationParent);
        destinationParent.getChildren().add(movedChild);

        // NodeDaoImpl currently returns the saved destination parent, so the service maps that object.
        when(nodeDao.moveChildNode("newParent", "child")).thenReturn(destinationParent);

        NodeResponseDto response = nodeService.moveChildNode("newParent", "child");

        assertNotNull(response);
        assertNotNull(response.getParent());
        assertEquals("newParent", response.getParent().getName());
        assertNotNull(response.getParent().getChildren());
        assertEquals(1, response.getParent().getChildren().size());
        verify(nodeDao).moveChildNode("newParent", "child");
    }
}
