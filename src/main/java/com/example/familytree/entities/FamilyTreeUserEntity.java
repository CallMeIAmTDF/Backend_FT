package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "family_tree_user")
public class FamilyTreeUserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private int id;
    @Basic
    @Column(name = "family_tree_id", nullable = true)
    private Integer familyTreeId;
    @Basic
    @Column(name = "user_id", nullable = true)
    private Integer userId;
    @Basic
    @Column(name = "user_tree_status", nullable = true)
    private Boolean userTreeStatus;
    @Basic
    @Column(name = "role_id", nullable = true)
    private Integer roleId;
    @Basic
    @Column(name = "person_id", nullable = true)
    private Integer personId;

}
