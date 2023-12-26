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
@Table(name = "otp")
public class OtpEntity {
    @Basic
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    @Basic
    @Column(name = "otp_code", nullable = false, length = 2147483647)
    private String otpCode;
    @Basic
    @Column(name = "otp_exp", nullable = false)
    private Date otpExp;
    @Basic
    @Column(name = "otp_fail_attempts", nullable = true)
    private Integer otpFailAttempts;


}
