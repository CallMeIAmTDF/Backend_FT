
package com.example.familytree.models.dto;

import com.example.familytree.shareds.Constants;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePersonDto {
    private Integer personId;
    private String personName;
//    @NotNull
//    @BooleanValue
//    private Boolean personGender;
    @Past(message = Constants.INVALID_BIRTHDAY)
    private Date personDob;
    private String personJob;
    private String personReligion;
    private String personEthnic;
    @Past(message = Constants.INVALID_BIRTHDAY)
    private Date personDod;
    private String personAddress;
//    private Integer parentsId;
//    @NotNull
//    private Integer familyTreeId;
    private Boolean personStatus;
//    private Integer personRank;
    private String personDescription;
    private String personStory;
    private Integer fatherId;
    private Integer motherId;
    @Pattern(regexp = Constants.REGEX_URL_IMAGE, message = Constants.INVALID_FILE_IMAGE)
    private String personImage;
    private Double siblingNum;
    private Integer siblingId;
}
