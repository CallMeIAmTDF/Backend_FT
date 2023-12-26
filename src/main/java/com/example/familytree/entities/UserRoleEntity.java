package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "user_role")
public class UserRoleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "role_id", nullable = false)
    private int roleId;
    @Basic
    @Column(name = "role_name", nullable = true, length = 100)
    private String roleName;
    @Basic
    @Column(name = "role_description", nullable = true, length = 200)
    private String roleDescription;
}
