package com.example.familytree.models.response;

import com.example.familytree.models.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class LoginResponse {
    private UserInfo userInfo;
    private TokenResponse tokens;
}