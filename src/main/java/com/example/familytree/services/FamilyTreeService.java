package com.example.familytree.services;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.response.ListTreeResponse;
import com.example.familytree.models.response.InfoUser;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public interface FamilyTreeService {

    ListTreeResponse getListTreeByUserId(int userId);

    List<InfoUser> getListUserByFamilyTreeId(int familyTreeId, int userId);

    FamilyTreeEntity create(FamilyTreeEntity familyTree);

    @Transactional
    FamilyTreeEntity copy(String name, PersonEntity person, int side, UserAccountEntity user);

    List<PersonEntity> getListSharingPerson(int ft, int pid, int side);

    List<SpouseEntity> getListSharingSpouse(List<PersonEntity> listPerson);

    List<PersonInfoSimplifiedInfoDis> getPersonSimplified(int pid);

    Map<Integer, PersonDataV2> getDataV2(int fid,int pid, int userId);

}
