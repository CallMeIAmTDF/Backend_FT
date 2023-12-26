package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "family_tree")
public class FamilyTreeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "family_tree_id", nullable = false)
    private int familyTreeId;
    @Basic
    @Column(name = "user_id", nullable = true)
    private Integer userId;
    @Basic
    @Column(name = "family_tree_name", nullable = true, length = 500)
    private String familyTreeName;
    @Basic
    @Column(name = "person_id", nullable = true)
    private Integer personId;

}
