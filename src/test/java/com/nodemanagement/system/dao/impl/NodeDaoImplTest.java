package com.nodemanagement.system.dao.impl;

import com.nodemanagement.system.entity.Node;
import com.nodemanagement.system.repo.NodeRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeDaoImplTest {

    @Mock
    private NodeRepo nodeRepo;

    @InjectMocks
    private NodeDaoImpl nodeDao;

    @Test
    void test_getNodeByName() {
        Node node = new Node("parent", null);
        when(nodeRepo.findNodeByName("parent")).thenReturn(node);

        Node result = nodeDao.getNodeByName("parent");

        assertSame(node, result);
        verify(nodeRepo).findNodeByName("parent");
    }

    @Test
    void test_addNode_shouldThrowWhenNodeAlreadyExists() {
        when(nodeRepo.findNodeByName("parent")).thenReturn(new Node("parent", null));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.addNode("parent")
        );

        assertEquals("Node already exists: parent", ex.getMessage());
        verify(nodeRepo).findNodeByName("parent");
        verify(nodeRepo, never()).saveAndFlush(any());
    }

    @Test
    void test_addNode_shouldCreateAndSaveWhenNodeDoesNotExist() {
        Node saved = new Node("parent", null);
        when(nodeRepo.findNodeByName("parent")).thenReturn(null);
        when(nodeRepo.saveAndFlush(any(Node.class))).thenReturn(saved);

        Node result = nodeDao.addNode("parent");

        assertNotNull(result);
        assertEquals("parent", result.getName());

        ArgumentCaptor<Node> captor = ArgumentCaptor.forClass(Node.class);
        verify(nodeRepo).saveAndFlush(captor.capture());
        assertEquals("parent", captor.getValue().getName());
        assertNull(captor.getValue().getParent());
    }

    @Test
    void test_addChildNode_shouldThrowWhenParentDoesNotExist() {
        when(nodeRepo.findNodeByName("parent")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.addChildNode("parent", "child")
        );

        assertEquals("Parent not found: parent", ex.getMessage());
        verify(nodeRepo).findNodeByName("parent");
        verify(nodeRepo, never()).saveAndFlush(any());
    }

    @Test
    void test_addChildNode_shouldThrowWhenChildAlreadyExists() {
        Node parent = new Node("parent", null);
        when(nodeRepo.findNodeByName("parent")).thenReturn(parent);
        when(nodeRepo.existsByParentIsNotNullAndName("child")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.addChildNode("parent", "child")
        );

        assertEquals("Child already exists: child", ex.getMessage());
        verify(nodeRepo).existsByParentIsNotNullAndName("child");
        verify(nodeRepo, never()).saveAndFlush(any());
    }

    @Test
    void test_addChildNode_shouldAttachChildAndSaveParent() {
        Node parent = new Node("parent", null);
        when(nodeRepo.findNodeByName("parent")).thenReturn(parent);
        when(nodeRepo.existsByParentIsNotNullAndName("child")).thenReturn(false);
        when(nodeRepo.saveAndFlush(parent)).thenReturn(parent);

        Node result = nodeDao.addChildNode("parent", "child");

        assertSame(parent, result);
        assertEquals(1, parent.getChildren().size());

        Node createdChild = (Node) parent.getChildren().iterator().next();
        assertEquals("child", createdChild.getName());
        assertSame(parent, createdChild.getParent());
        verify(nodeRepo).saveAndFlush(parent);
    }

    @Test
    void test_deleteNode_shouldThrowWhenNodeDoesNotExist() {
        when(nodeRepo.findNodeByName("parent")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.deleteNode("parent")
        );

        assertEquals("Node not found: parent", ex.getMessage());
        verify(nodeRepo, never()).delete(any());
    }

    @Test
    void test_deleteNode_shouldDeleteWhenNodeExists() {
        Node node = new Node("parent", null);
        when(nodeRepo.findNodeByName("parent")).thenReturn(node);

        nodeDao.deleteNode("parent");

        verify(nodeRepo).delete(node);
    }

    @Test
    void test_moveChildNode_shouldThrowWhenChildDoesNotExist() {
        when(nodeRepo.findNodeByName("child")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.moveChildNode("newParent", "child")
        );

        assertEquals("Child not found: child", ex.getMessage());
        verify(nodeRepo).findNodeByName("child");
    }

    @Test
    void test_moveChildNode_shouldThrowWhenNewParentDoesNotExist() {
        Node child = new Node("child", null);
        when(nodeRepo.findNodeByName("child")).thenReturn(child);
        when(nodeRepo.findNodeByName("newParent")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.moveChildNode("newParent", "child")
        );

        assertEquals("Destination parent not found: newParent", ex.getMessage());
        verify(nodeRepo, never()).saveAndFlush(any());
    }

    @Test
    void test_moveChildNode_shouldThrowWhenMovingNodeToItself() {
        Node child = new Node("child", null);
        Node sameNodeByName = new Node("child", null);

        when(nodeRepo.findNodeByName("child")).thenReturn(child);
        when(nodeRepo.findNodeByName("child")).thenReturn(child);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.moveChildNode("child", "child")
        );

        assertEquals("Cannot move node to itself", ex.getMessage());
        verify(nodeRepo, never()).saveAndFlush(any());
    }

    @Test
    void test_moveChildNode_shouldThrowWhenNewParentIsInsideOwnDownline() {
        Node root = new Node("parent", null);
        Node child = new Node("child", root);
        Node grandChild = new Node("grandChild", child);

        root.getChildren().add(child);
        child.getChildren().add(grandChild);

        when(nodeRepo.findNodeByName("child")).thenReturn(child);
        when(nodeRepo.findNodeByName("grandChild")).thenReturn(grandChild);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.moveChildNode("grandChild", "child")
        );

        assertEquals("Cannot move node into its own downline", ex.getMessage());
        verify(nodeRepo, never()).saveAndFlush(any());
    }

    @Test
    void test_moveChildNode_shouldThrowWhenChildAlreadyExistsUnderNewParent() {
        Node oldParent = new Node("oldParent", null);
        Node child = new Node("child", oldParent);
        oldParent.getChildren().add(child);

        Node destination = new Node("newParent", null);
        Node duplicate = new Node("child", destination);
        destination.getChildren().add(duplicate);

        when(nodeRepo.findNodeByName("child")).thenReturn(child);
        when(nodeRepo.findNodeByName("newParent")).thenReturn(destination);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> nodeDao.moveChildNode("newParent", "child")
        );

        assertEquals("Child already exists under new parent: child", ex.getMessage());
        verify(nodeRepo, never()).saveAndFlush(any());
    }

    @Test
    void moveChildNode_shouldMoveChildAndSaveNewParent() {
        Node oldParent = new Node("oldParent", null);
        Node child = new Node("child", oldParent);
        oldParent.getChildren().add(child);

        Node destination = new Node("newParent", null);

        when(nodeRepo.findNodeByName("child")).thenReturn(child);
        when(nodeRepo.findNodeByName("newParent")).thenReturn(destination);
        when(nodeRepo.saveAndFlush(destination)).thenReturn(destination);

        Node result = nodeDao.moveChildNode("newParent", "child");

        assertSame(destination, result);
        assertFalse(oldParent.getChildren().contains(child));
        assertTrue(destination.getChildren().contains(child));
        assertSame(destination, child.getParent());
        verify(nodeRepo).saveAndFlush(destination);
    }
}
