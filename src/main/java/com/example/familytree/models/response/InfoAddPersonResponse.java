package com.example.familytree.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class InfoAddPersonResponse {
    private int personId;
    private String personName;
    private String personGender;
    private Date personDob;
    private String personJob;
    private String personReligion;
    private String personEthnic;
    private Date personDod;
    private String personAddress;
    private Integer parentsId;
    private Integer familyTreeId;
    private Boolean personStatus;
    private Integer personRank;
    private String personDescription;
    private String personStory;
    private Integer fatherId;
    private Integer motherId;
    private Double siblingNum;
    private String personImage;
    private Integer groupChildId;
    private List<Integer> wife;
    private List<Integer> husband;
    private List<Integer> sibling;
    private List<Integer> children;
}