package com.example.familytree.services;

import com.example.familytree.entities.HistoryEntity;
import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.enums.HistoryTypeEnum;
import org.springframework.stereotype.Service;

@Service
public interface HistoryService {
    HistoryEntity HandleInsertHistory(UserAccountEntity user, PersonEntity person, HistoryTypeEnum type, PersonEntity oldPerson);
}
