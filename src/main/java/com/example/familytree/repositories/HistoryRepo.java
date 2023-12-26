package com.example.familytree.repositories;

import com.example.familytree.entities.HistoryEntity;
import com.example.familytree.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepo extends JpaRepository<HistoryEntity, Integer>  {
    HistoryEntity findFirstByFamilyTreeIdAndPersonId(int familyTreeId, int personId);
    List<HistoryEntity> findAllByUserIdAndFamilyTreeId(int userId, int familyTreeId);
}