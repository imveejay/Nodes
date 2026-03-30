package com.nodemanagement.system.entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "nodes")
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_node_parent"))
    private Node parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("name ASC")
    private Set<Node> children = new LinkedHashSet<>();

    public Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Set<Node> getChildren() {
        return children;
    }
}
