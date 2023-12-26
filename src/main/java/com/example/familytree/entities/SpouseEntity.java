package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "Spouse")
public class SpouseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "spouse_id", nullable = false)
    private int spouseId;
    @Basic
    @Column(name = "husband_id", nullable = true)
    private Integer husbandId;
    @Basic
    @Column(name = "wife_id", nullable = true)
    private Integer wifeId;
    @Basic
    @Column(name = "spouse_status", nullable = true)
    private Integer spouseStatus;


}
