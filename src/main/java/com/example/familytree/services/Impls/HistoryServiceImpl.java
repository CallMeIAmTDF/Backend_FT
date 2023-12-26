package com.example.familytree.services.Impls;

import com.example.familytree.entities.HistoryEntity;
import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.enums.HistoryTypeEnum;
import com.example.familytree.repositories.HistoryRepo;
import com.example.familytree.services.HistoryService;
import com.example.familytree.shareds.Constants;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final HistoryRepo historyRepo;
    @Override
    public HistoryEntity HandleInsertHistory(UserAccountEntity user, PersonEntity person, HistoryTypeEnum type, PersonEntity oldPerson) {
        Gson gson = new Gson();
        String newDataJson = gson.toJson(person);
        int familyTreeId = person.getFamilyTreeId();
        String objectEmptyJson = "{}";

        Date createdTime;
        Date updatedTime;
        Date deletedTime;

        HistoryEntity historyEntity;
        HistoryEntity oldHistory;

        switch (type) {
            case CREATED:
                createdTime = Constants.getCurrentDay();
                historyEntity = HistoryEntity.create(0, user.getUserId(), type.getDescription(), familyTreeId, person.getPersonId(), createdTime, null, createdTime, newDataJson, objectEmptyJson);
                break;
            case UPDATED:
                updatedTime = Constants.getCurrentDay();
                oldHistory = historyRepo.findFirstByFamilyTreeIdAndPersonId(familyTreeId, person.getPersonId());
                if (oldPerson == null || oldHistory == null) {
                    throw new RuntimeException("Not exists history have familyTreeId: " + familyTreeId + " and personId is " + person.getPersonId());
                }
                historyEntity = HistoryEntity.create(0, user.getUserId(), type.getDescription(), familyTreeId, person.getPersonId(), oldHistory.getHistoryCreatedAt(), null, updatedTime, newDataJson, oldHistory.getCurrentData());
                break;
            case DELETED:
                deletedTime = Constants.getCurrentDay();
                oldHistory = historyRepo.findFirstByFamilyTreeIdAndPersonId(familyTreeId, person.getPersonId());
                if (oldHistory == null) {
                    throw new RuntimeException("Not exists history have familyTreeId: " + familyTreeId + " and personId is " + person.getPersonId());
                }
                historyEntity = HistoryEntity.create(0, user.getUserId(), type.getDescription(), familyTreeId, person.getPersonId(), oldHistory.getHistoryCreatedAt(), oldHistory.getHistoryUpdatedAt(), deletedTime, objectEmptyJson, oldHistory.getCurrentData());
                break;
            default:
                throw new RuntimeException("Not exists type of History: " + type.getDescription());
        }

        historyRepo.save(historyEntity);

        return historyEntity;
    }
}
