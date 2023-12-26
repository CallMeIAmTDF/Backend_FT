package com.example.familytree.models.dto;

import com.example.familytree.shareds.Constants;
import com.example.familytree.valid.BooleanValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PersonDisplayDto {
    private String name;
    private String gender;
    private Date personDob;
    private Date personDod;
    private Integer parentsId;
    private Integer familyTreeId;
    private Boolean personStatus;
    private Integer personRank;
    private Integer fatherId;
    private Integer motherId;
    private String personImage;
    private Double siblingNum;
    private Integer groupChildId;
}
