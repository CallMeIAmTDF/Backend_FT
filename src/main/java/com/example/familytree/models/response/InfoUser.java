package com.example.familytree.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class InfoUser {
    private int id;
    private String email;
    private String name;
    private int roleId;
    private boolean status;
    private int personId;
    private int familyTreeId;
}