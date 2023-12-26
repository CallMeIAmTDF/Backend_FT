package com.example.familytree.repositories;

import com.example.familytree.entities.FamilyTreeUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FamilyTreeUserRepo extends JpaRepository<FamilyTreeUserEntity, Integer> {
    FamilyTreeUserEntity findFirstByFamilyTreeIdAndUserId(int familyTreeId, int userid);
    List<FamilyTreeUserEntity> findByUserIdAndUserTreeStatus(int userId, boolean status);
    List<FamilyTreeUserEntity> findByFamilyTreeIdAndUserTreeStatus(int fid, boolean status);

    List<FamilyTreeUserEntity> findByFamilyTreeId(int id);

    boolean existsByFamilyTreeIdAndUserIdAndUserTreeStatus (int familyTreeId, int userid, boolean status);
}
