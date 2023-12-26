package com.example.familytree.services;

import com.example.familytree.entities.OtpEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.entities.VerificationCodeEntity;
import com.example.familytree.enums.RegisterEnum;
import com.example.familytree.enums.VerificationEnum;
import com.example.familytree.models.dto.ResetPasswordDto;
import com.example.familytree.models.dto.UpdateUserAccountDto;
import com.example.familytree.models.dto.UserAccountDto;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
@Transactional
public interface UserAccountService {
    void save(UserAccountEntity userAccountEntity);

    void edit (UpdateUserAccountDto updateUserAccountDto);

    boolean isTimeOutRequired(OtpEntity otpEntity, long ms);
    boolean isTimeOutRequired(VerificationCodeEntity verificationCodeEntity, long ms);

    RegisterEnum register(UserAccountDto userAccountDto) throws MessagingException, NoSuchAlgorithmException;
    VerificationEnum verify(String code);
    void forgetPassword(int userId);
    VerificationEnum checkOtp(ResetPasswordDto resetPasswordDto);
}