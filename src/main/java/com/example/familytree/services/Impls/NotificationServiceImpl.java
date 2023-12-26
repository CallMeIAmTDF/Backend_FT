package com.example.familytree.services.Impls;

import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.entities.NotificationEntity;
import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.enums.NotiTypeEnum;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.repositories.FamilyTreeUserRepo;
import com.example.familytree.repositories.NotificationRepo;
import com.example.familytree.services.NotificationService;
import com.example.familytree.shareds.Constants;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final FamilyTreeRepo familyTreeRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;

    @Override
    @Transactional
    public void createMultiple(NotificationEntity eNoti, ArrayList<Integer> ids) {
        List<NotificationEntity> notifications = new ArrayList<>();
        for (int id : ids) {
            notifications.add(NotificationEntity.create(0, eNoti.getType(), eNoti.getSenderId(), id, eNoti.getContent(), eNoti.getCreatedAt()));
        }
        notificationRepo.saveAll(notifications);
    }

    public CompletableFuture<Void> createMultipleAsync(NotificationEntity eNoti, ArrayList<Integer> ids) {
        return CompletableFuture.runAsync(() -> createMultiple(eNoti, ids));
    }

    @Override
    @Transactional
    public void HandleInsertNotification(PersonEntity person, UserAccountEntity user, NotiTypeEnum type) {
        // Notification
        String familyTreeName = familyTreeRepo.findFirstByFamilyTreeId(person.getFamilyTreeId()).getFamilyTreeName();

        Map<String, String> mapContent = new HashMap<>();
        mapContent.put(Constants.personName, person.getPersonName());
        mapContent.put(Constants.personId, Integer.toString(person.getPersonId()));
        mapContent.put(Constants.userName, user.getUserFullname());
        mapContent.put(Constants.userId, Integer.toString(user.getUserId()));
        mapContent.put(Constants.familyTreeId, person.getFamilyTreeId().toString());

        String message = "";
        switch (type) {
            case CREATE_PERSON -> message = Constants.CREATE_PERSON_MESSAGE;
            case UPDATE_PERSON -> message = Constants.UPDATE_PERSON_MESSAGE;
            case DELETE_PERSON -> message = Constants.DELETE_PERSON_MESSAGE;
        }

        mapContent.put(Constants.message, MessageFormat.format(message,  user.getUserFullname(), person.getPersonName(), familyTreeName));

        Gson gson = new Gson();
        String content = gson.toJson(mapContent);

        NotificationEntity noti = NotificationEntity.create(0, type.getDescription(), user.getUserId(), 0, content, Constants.getCurrentDay());

        ArrayList<Integer> ids = new ArrayList<>();
        List<FamilyTreeUserEntity> listUser = familyTreeUserRepo.findByFamilyTreeIdAndUserTreeStatus(person.getFamilyTreeId(), true);
        for (FamilyTreeUserEntity familyTreeUserEntity : listUser) {
            ids.add(familyTreeUserEntity.getUserId());
        }

        this.createMultipleAsync(noti, ids);
    }
}