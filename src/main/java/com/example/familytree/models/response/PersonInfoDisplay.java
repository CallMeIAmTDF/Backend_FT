package com.example.familytree.models.response;

import com.example.familytree.models.dto.PersonDisplayDto;
import com.example.familytree.models.dto.PersonDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PersonInfoDisplay {
    int id;
    Integer parentId;
    PersonDisplayDto info;
    ArrayList<Integer> spouseIds;
    Integer groupId;
    String side;
    int rank;

    int isFatherSide; //0: tôi, 1: họ nội, 2: họ ngoại
    String vocative; //xưng hô
}
