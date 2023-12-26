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
public class UpdateUserAccountDto {
    private String userFullName;
    @Past(message = Constants.INVALID_BIRTHDAY)
    private Date userDob;
    private Boolean userGender;
    private String userPhoneNum;
    private String userAddress;
    @Pattern(regexp = Constants.REGEX_URL_IMAGE, message = Constants.INVALID_FILE_IMAGE)
    private String userImage;
}

