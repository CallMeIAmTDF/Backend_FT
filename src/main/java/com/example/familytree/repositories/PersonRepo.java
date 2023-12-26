package com.example.familytree.repositories;

import com.example.familytree.entities.PersonEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepo extends JpaRepository<PersonEntity, Integer> {
    PersonEntity findFirstByPersonId(int personId);
    List<PersonEntity> findByFamilyTreeId(int treeId);
    List<PersonEntity> findByParentsId(int parentsId);
    List<PersonEntity> findByFatherId(int fatherId);
    List<PersonEntity> findByMotherId(int motherId);

    List<PersonEntity> findByFatherIdAndMotherId (int fatherId, int motherId);
    List<PersonEntity> findByGroupChildId(int groupChildId);



    boolean existsByFamilyTreeId(int treeId);
    boolean existsByPersonId(int personId);
    boolean existsByPersonIdAndPersonGender(int personId, boolean gender);
    boolean existsByFamilyTreeIdAndPersonId(int familyTreeID, int personId);

}
