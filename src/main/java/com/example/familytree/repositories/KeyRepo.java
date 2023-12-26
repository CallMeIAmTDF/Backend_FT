package com.example.familytree.repositories;

import com.example.familytree.entities.KeyTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface KeyRepo extends JpaRepository<KeyTokenEntity, Integer> {
    KeyTokenEntity findFirstByUserId(int userId);
}
