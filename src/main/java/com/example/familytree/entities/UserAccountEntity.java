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
@Table(name = "user_account")
public class UserAccountEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "user_fullname", nullable = true, length = 70)
    private String userFullname;
    @Basic
    @Column(name = "user_password", nullable = true, length = 2147483647)
    private String userPassword;
    @Basic
    @Column(name = "user_dob", nullable = true)
    private Date userDob;
    @Basic
    @Column(name = "user_gender", nullable = true)
    private Boolean userGender;
    @Basic
    @Column(name = "user_phone", nullable = true, length = 15)
    private String userPhone;
    @Basic
    @Column(name = "user_address", nullable = true, length = 70)
    private String userAddress;
    @Basic
    @Column(name = "user_email", nullable = true, length = 50)
    private String userEmail;
    @Basic
    @Column(name = "user_image", nullable = true, length = 2147483647)
    private String userImage;
    @Basic
    @Column(name = "user_status", nullable = true)
    private Boolean userStatus;
    @Basic
    @Column(name = "user_created_at", nullable = true)
    private java.util.Date userCreatedAt;
    @Basic
    @Column(name = "user_updated_at", nullable = true)
    private java.util.Date userUpdatedAt;
    @Basic
    @Column(name = "user_deleted_at", nullable = true)
    private java.util.Date userDeletedAt;
    @Basic
    @Column(name = "user_is_deleted", nullable = true)
    private Boolean userIsDeleted;
   }
