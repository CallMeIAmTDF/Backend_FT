package com.example.familytree.services;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.dto.PersonDto;
import com.example.familytree.models.dto.UpdatePersonDto;
import com.example.familytree.models.response.InfoAddPersonResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface PersonService {
    PersonEntity createFirstPerson(PersonDto personDto);

    PersonEntity createParents(PersonDto parentDto, int personID);
    PersonEntity createChildren(PersonDto parentDto, int siblingId);

    PersonEntity createSpouse(PersonDto personDto, int personID);


    InfoAddPersonResponse getInfoPerson(int personId, String option);

    List<PersonEntity> getListChild(int fatherId, int motherId);

    @Transactional
    PersonEntity createPersonVirtual(int fid, boolean gender);

    @Transactional
    void createPersonCopyVirtual(int fid, int pid, boolean gender);

    @Transactional
    PersonEntity createPersonCopy(PersonEntity personEntity, int fid, int range, UserAccountEntity userByEmail);

    PersonEntity updatePerson(UpdatePersonDto updatePersonDto);

}
