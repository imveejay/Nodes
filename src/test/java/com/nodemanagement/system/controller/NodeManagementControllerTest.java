package com.nodemanagement.system.controller;

import com.nodemanagement.system.dto.NodeDto;
import com.nodemanagement.system.dto.NodeRequest;
import com.nodemanagement.system.dto.NodeResponseDto;
import com.nodemanagement.system.service.NodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeManagementControllerTest {

    @Mock
    private NodeService nodeService;

    @InjectMocks
    private NodeManagementController controller;

    @Test
    void test_getNodes() {
        NodeResponseDto response = responseWithParentName("parent");
        when(nodeService.getNodeByName("parent")).thenReturn(response);

        NodeResponseDto result = controller.getNodes("parent");

        assertNotNull(result);
        assertEquals("parent", result.getParent().getName());
        verify(nodeService).getNodeByName("parent");
    }

    @Test
    void test_addNode() {
        NodeRequest request = new NodeRequest();
        request.setName("parent");

        NodeResponseDto response = responseWithParentName("parent");
        when(nodeService.addNode(request)).thenReturn(response);

        NodeResponseDto result = controller.addNode(request);

        assertNotNull(result);
        assertEquals("parent", result.getParent().getName());
        verify(nodeService).addNode(request);
    }

    @Test
    void test_addChildNode() {
        NodeRequest request = new NodeRequest();
        request.setName("child");

        NodeResponseDto response = responseWithParentName("parent");
        when(nodeService.addChildNode("parent", request)).thenReturn(response);

        NodeResponseDto result = controller.addChildNode("parent", request);

        assertNotNull(result);
        assertEquals("parent", result.getParent().getName());
        verify(nodeService).addChildNode("parent", request);
    }

    @Test
    void test_moveChildNode() {
        NodeRequest request = new NodeRequest();
        request.setName("child");

        NodeResponseDto response = responseWithParentName("newParent");
        when(nodeService.moveChildNode("newParent", "child")).thenReturn(response);

        NodeResponseDto result = controller.moveChildNode("newParent", request);

        assertNotNull(result);
        assertEquals("newParent", result.getParent().getName());
        verify(nodeService).moveChildNode("newParent", "child");
    }

    @Test
    void test_deleteNode() {
        controller.deleteNode("to-delete");

        verify(nodeService).deleteNode("to-delete");
    }

    private NodeResponseDto responseWithParentName(String name) {
        NodeDto parent = new NodeDto();
        parent.setName(name);

        NodeResponseDto response = new NodeResponseDto();
        response.setParent(parent);
        return response;
    }
}
