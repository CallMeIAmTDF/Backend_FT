package com.example.familytree.models.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PersonDataV2 {
    PersonInfoDisplay data;
    Integer fatherId;
    Integer motherId;
    //ArrayList<Integer> siblingIds;
    ArrayList<Integer> spousePersonIds;
    ArrayList<Integer> childrenIds;
    //Map<PersonId, PersonDataV2>
}