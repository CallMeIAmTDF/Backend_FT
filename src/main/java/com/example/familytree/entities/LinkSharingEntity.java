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
@Table(name = "link_sharing")
public class LinkSharingEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "link", nullable = false, length = 2147483647)
    private String link;
    @Basic
    @Column(name = "person_id", nullable = false)
    private int personId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "family_tree_id", nullable = false)
    private int familyTreeId;
    @Basic
    @Column(name = "exp", nullable = false)
    private Date exp;
    @Basic
    @Column(name = "role", nullable = true, length = 2147483647)
    private String role;


}
