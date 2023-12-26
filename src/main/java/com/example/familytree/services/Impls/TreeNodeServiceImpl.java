package com.example.familytree.services.Impls;

import com.example.familytree.entities.TreeNodeEntity;
import com.example.familytree.repositories.TreeNodeRepo;
import com.example.familytree.services.TreeNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TreeNodeServiceImpl implements TreeNodeService {

    private final TreeNodeRepo treeNodeRepo;

    @Override
    public void createTreeNode(TreeNodeEntity treeNode) {
        treeNodeRepo.save(treeNode);
    }
}
