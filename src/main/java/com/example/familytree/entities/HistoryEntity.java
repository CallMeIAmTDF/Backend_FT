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
@Table(name = "History")
public class HistoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "history_id", nullable = false)
    private int historyId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "history_action", nullable = false, length = 50)
    private String historyAction;
    @Basic
    @Column(name = "family_tree_id", nullable = false)
    private int familyTreeId;
    @Basic
    @Column(name = "person_id", nullable = false)
    private int personId;
    @Basic
    @Column(name = "history_created_at", nullable = false)
    private Date historyCreatedAt;
    @Basic
    @Column(name = "history_deleted_at", nullable = true)
    private Date historyDeletedAt;
    @Basic
    @Column(name = "history_updated_at", nullable = true)
    private Date historyUpdatedAt;
    @Basic
    @Column(name = "current_data", nullable = false, length = -1)
    private String currentData;
    @Basic
    @Column(name = "old_data", nullable = true, length = -1)
    private String oldData;
}
