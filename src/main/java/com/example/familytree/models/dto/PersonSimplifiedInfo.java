package com.example.familytree.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PersonSimplifiedInfo {
    private String name;
    private String gender;
    private Date personDob;
    private Date personDod;
    private Boolean personStatus;
}
