package com.example.familytree.models.response;

import com.example.familytree.models.dto.PersonDisplayDto;
import com.example.familytree.models.dto.PersonSimplifiedInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PersonInfoSimplifiedInfoDis {
    int id;
    Integer parentId;
    PersonSimplifiedInfo info;
    ArrayList<Integer> spouseIds;
    Integer groupId;
    String side;
    int rank;
    int isFatherSide; //0: tôi, 1: họ nội, 2: họ ngoại
    String vocative; //xưng hô
}
