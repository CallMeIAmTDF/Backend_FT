package com.example.familytree.repositories;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpouseRepo extends JpaRepository<SpouseEntity, Integer> {
    List<SpouseEntity> findByWifeId(int wifeId);
    List<SpouseEntity> findByWifeIdAndSpouseStatus(int wifeId, int status);

    List<SpouseEntity> findByHusbandId(int husbandId);
    List<SpouseEntity> findByHusbandIdAndSpouseStatus(int husbandId, int status);


    SpouseEntity findFirstBySpouseId(int spouseId);
    SpouseEntity findFirstByHusbandIdAndWifeId(Integer husbandId, Integer wifeId);
    boolean existsByHusbandIdAndWifeId(int husbandId, int wifeId);

}
