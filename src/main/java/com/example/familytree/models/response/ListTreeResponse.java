package com.example.familytree.models.response;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.models.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class ListTreeResponse {
    private UserInfo userInfo;
    private List<FamilyTreeEntity> owner;
    private List<FamilyTreeEntity> joined;
    private List<FamilyTreeEntity> requestJoin;
}