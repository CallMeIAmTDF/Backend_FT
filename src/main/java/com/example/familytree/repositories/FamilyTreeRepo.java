package com.example.familytree.repositories;

import com.example.familytree.entities.FamilyTreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FamilyTreeRepo extends JpaRepository<FamilyTreeEntity, Integer> {
    FamilyTreeEntity findFirstByFamilyTreeId (int id);
    boolean existsByFamilyTreeId(int id);
    List<FamilyTreeEntity> findByUserId(int userId);
}
