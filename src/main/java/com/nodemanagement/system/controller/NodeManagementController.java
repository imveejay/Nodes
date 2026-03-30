package com.nodemanagement.system.controller;

import com.nodemanagement.system.dto.NodeRequest;
import com.nodemanagement.system.dto.NodeResponseDto;
import com.nodemanagement.system.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/nodes",
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
)
public class NodeManagementController {

    @Autowired
    private NodeService nodeService;

    @GetMapping("/getNode/{parentName}")
    public NodeResponseDto getNodes(@PathVariable String parentName) {
        return nodeService.getNodeByName(parentName);
    }

    @PostMapping(value = "/addNode", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public NodeResponseDto addNode(@RequestBody NodeRequest nodeRequest) {
        return nodeService.addNode(nodeRequest);
    }

    @PostMapping(value = "{parentName}/addChildNode", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public NodeResponseDto addChildNode(@PathVariable String parentName,
                                        @RequestBody NodeRequest nodeRequest) {
        return nodeService.addChildNode(parentName, nodeRequest);
    }

    @PutMapping(value = "{parentName}/moveChildNode", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public NodeResponseDto moveChildNode(@PathVariable String parentName,
                                        @RequestBody NodeRequest nodeRequest) {
        return nodeService.moveChildNode(parentName, nodeRequest.getName());
    }

    @DeleteMapping("/deleteNode/{name}")
    public void deleteNode(@PathVariable String name) {
        nodeService.deleteNode(name);
    }
}
