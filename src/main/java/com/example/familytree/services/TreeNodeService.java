package com.example.familytree.services;

import com.example.familytree.entities.TreeNodeEntity;
import org.springframework.stereotype.Service;

@Service
public interface TreeNodeService {
    void createTreeNode(TreeNodeEntity treeNode);
}
