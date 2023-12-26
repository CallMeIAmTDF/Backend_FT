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
@Table(name = "notification")
public class NotificationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "type", nullable = false, length = 2147483647)
    private String type;
    @Basic
    @Column(name = "sender_id", nullable = false)
    private int senderId;
    @Basic
    @Column(name = "receive_id", nullable = false)
    private int receiveId;
    @Basic
    @Column(name = "content", nullable = true, length = 2147483647)
    private String content;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
