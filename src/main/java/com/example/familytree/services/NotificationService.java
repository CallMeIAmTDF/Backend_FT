package com.example.familytree.services;

import com.example.familytree.entities.NotificationEntity;
import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.enums.NotiTypeEnum;
import com.example.familytree.models.dto.PersonDto;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    void createMultiple(NotificationEntity eNoti, ArrayList<Integer> ids);
    CompletableFuture<Void> createMultipleAsync(NotificationEntity eNoti, ArrayList<Integer> ids);
    void HandleInsertNotification(PersonEntity person, UserAccountEntity user, NotiTypeEnum type);
}