package com.example.familytree.models.dto;

import com.example.familytree.shareds.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDto {
    @NotNull
    private String userFullName;
    @NotNull
    @Size(min = 8, max = 20, message = Constants.INVALID_PASSWORD_MIN_LENGTH)
    private String userPassword;
    @Past(message = Constants.INVALID_BIRTHDAY)
    private Date userDob;
    private Boolean userGender;
    private String userPhoneNum;
    private String userAddress;
    @NotNull
    @Email(message = Constants.INVALID_EMAIL)
    private String userEmail;
    @Pattern(regexp = Constants.REGEX_URL_IMAGE, message = Constants.INVALID_FILE_IMAGE)
    private String userImage;
}
