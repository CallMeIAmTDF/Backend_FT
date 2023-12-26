package com.example.familytree.repositories;

import com.example.familytree.entities.VerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VerificationCodeRepo extends JpaRepository<VerificationCodeEntity, Integer> {
    VerificationCodeEntity findFirstByVerificationCode(String code);
    VerificationCodeEntity findFirstByEmail(String email);
    boolean existsByEmail(String email);

}
