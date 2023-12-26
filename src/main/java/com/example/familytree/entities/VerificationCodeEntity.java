package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "verification_code")
public class VerificationCodeEntity {
    @Basic
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    @Basic
    @Column(name = "verification_code", nullable = false, length = 2147483647)
    private String verificationCode;
    @Basic
    @Column(name = "verification_code_exp", nullable = false, length = 50)
    private Date verificationCodeExp;


}
