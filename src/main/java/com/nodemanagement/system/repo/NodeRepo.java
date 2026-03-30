package com.nodemanagement.system.repo;

import com.nodemanagement.system.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepo extends JpaRepository<Node, Long> {


    Node findNodeByName(String name);

    List<Node> findByParentIsNotNull();

    boolean existsByParentIsNotNullAndName(String name);
}
