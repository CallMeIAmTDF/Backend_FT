package com.example.familytree.repositories;

import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.entities.TreeNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeNodeRepo extends JpaRepository<TreeNodeEntity, Integer> {

}
