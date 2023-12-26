package com.example.familytree.repositories;

import com.example.familytree.entities.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepo extends JpaRepository<UserAccountEntity, Integer> {
    boolean existsByUserEmail(String email);
    UserAccountEntity findFirstByUserId(int id);
    UserAccountEntity findFirstByUserEmail(String email);

    Optional<UserAccountEntity> findByUserEmail(String email);

}
