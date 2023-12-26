package com.example.familytree.repositories;

import com.example.familytree.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationEntity, Integer>  {
    List<NotificationEntity> findByReceiveId(int receiveId);
}