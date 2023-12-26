package com.example.familytree.services.Impls;

import com.example.familytree.entities.KeyTokenEntity;
import com.example.familytree.entities.OtpEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.entities.VerificationCodeEntity;
import com.example.familytree.enums.RegisterEnum;
import com.example.familytree.enums.VerificationEnum;
import com.example.familytree.models.dto.ResetPasswordDto;
import com.example.familytree.models.dto.UpdateUserAccountDto;
import com.example.familytree.models.dto.UserAccountDto;
import com.example.familytree.repositories.KeyRepo;
import com.example.familytree.repositories.OtpRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.repositories.VerificationCodeRepo;
import com.example.familytree.security.JwtService;
import com.example.familytree.services.SendMailService;
import com.example.familytree.services.UserAccountService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.DateUtil;
import com.example.familytree.utils.GenerateKeyUtil;
import com.example.familytree.utils.GenerateOtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepo userRepository;
    private final OtpRepo otpRepo;
    private final VerificationCodeRepo verificationCodeRepo;
    private final KeyRepo keyRepo;

    private final JwtService jwtService;
    private final SendMailService sendMailService;
    private final PasswordEncoder passwordEncoder;

    private UserAccountEntity createUser(UserAccountDto newUser) {
        return UserAccountEntity.create(
                0,
                newUser.getUserFullName(),
                passwordEncoder.encode(newUser.getUserPassword()),
                newUser.getUserDob(),
                null,
                newUser.getUserPhoneNum(),
                newUser.getUserAddress(),
                newUser.getUserEmail(),
                newUser.getUserImage(),
                false,
                Constants.getCurrentDay(),
                null,
                null,
                false
        );
    }

    @Override
    public void save(UserAccountEntity userAccountEntity) {

    }

    @Override
    public void edit(UpdateUserAccountDto updateUserAccountDto) {

    }

    @Override
    public boolean isTimeOutRequired(OtpEntity otpEntity, long ms) {
        if (otpEntity.getOtpCode() == null) {
            return true;
        }

        long currentTimeInMillis = System.currentTimeMillis();
        long otpRequestedTimeInMillis = otpEntity.getOtpExp().getTime();

        return otpRequestedTimeInMillis + ms <= currentTimeInMillis;
    }

    @Override
    public boolean isTimeOutRequired(VerificationCodeEntity verificationCodeEntity, long ms) {
        if (verificationCodeEntity.getVerificationCode() == null) {
            return true;
        }

        long currentTimeInMillis = System.currentTimeMillis();
        long otpRequestedTimeInMillis = verificationCodeEntity.getVerificationCodeExp().getTime();

        return otpRequestedTimeInMillis + ms <= currentTimeInMillis;
    }

    @Override
    public RegisterEnum register(UserAccountDto newUser) throws MessagingException, NoSuchAlgorithmException {
//        /* Kiểm tra xem username có tồn tại trong tbl_user chưa */
//        /* 1. Username đã tồn tại */
//        if (userRepository.existsByUsername(newUser.getUsername())) {
//            UserAccountEntity userByUsername = userRepository.findFirstByUsername(newUser.getUsername());
//            /* Kiểm tra xem user sở hữu username đó đã được kích hoạt chưa */
//            /* 1.1 Username chưa được kích hoạt */
//            if (!userByUsername.getStatus()) {
//                /* Kiểm tra xem email đã tồn tại trong db chưa */
//                /* 1.1.1. Email đã tồn tại trong db */
//                if (userRepository.existsByEmail(newUser.getEmail())) {
//                    UserAccountEntity userByEmail = userRepository.findFirstByEmail(newUser.getEmail());
//                    /*  Kiểm tra xem email đó đã được kích hoạt chưa */
//                    /* 1.1.1.1. Email chưa được kích hoạt */
//                    if (!userByEmail.getStatus()) {
//                        /* Tạo mới user và xóa 2 đối tượng có username và email trùng */
//                        // Tạo user +  gửi mail + save
//                        UserAccountEntity userAccountEntity = createUser()(newUser);
//                        sendMailService.registerCustomer(userAccountEntity);
//                        userRepository.save(userAccountEntity);
//                        // xóa 2 đối tượng cũ
//                        userRepository.deleteById(userByUsername.getId());
//                        userRepository.deleteById(userByEmail.getId());
//
//                        return RegisterEnum.SUCCESS;
//                    }
//                    /* 1.1.1.2. Email đã được kích hoạt */
//                    else {
//                        /* Thông báo cho người dùng không sử dụng email này */
//
//                        return RegisterEnum.DUPLICATE_EMAIL;
//                    }
//                }
//                /* 1.1.2. Email chưa tồn tại trong db */
//                else {
//                    /* Tạo user và xóa user có username trùng */
//                    // Tạo user +  gửi mail + save
//                    UserAccountEntity userAccountEntity = createUser()(newUser);
//                    sendMailService.registerCustomer(userAccountEntity);
//                    userRepository.save(userAccountEntity);
//                    // xóa 1 đối tượng cũ
//                    userRepository.deleteById(userByUsername.getId());
//
//                    return RegisterEnum.SUCCESS;
//                }
//            }
//            /* 1.2 Username đã được kích hoạt*/
//            else {
//                /* Thông báo cho người dùng không sử dụng username này */
//                return RegisterEnum.DUPLICATE_USERNAME;
//            }
//        }
//        /* 2. Username chưa tồn tại */
//        else {
        Date currentDay = DateUtil.getCurrentDay();
        String code = RandomStringUtils.randomAlphanumeric(64);
        String privateKey = GenerateKeyUtil.generate();
        String publicKey = GenerateKeyUtil.generate();


        /* Kiểm tra xem email đã có người dùng chưa */
            /* 2.1 Email đã tồn tại */
            if (userRepository.existsByUserEmail(newUser.getUserEmail())) {
                UserAccountEntity userByEmail = userRepository.findFirstByUserEmail(newUser.getUserEmail());
                /*Kiểm tra xem email đó đã được kích hoạt chưa */
                /* 2.1.1. Email chưa được kích hoạt */
                if (!userByEmail.getUserStatus()) {


                    /* Tạo mới user và xóa đối tượng có email trùng */
                    // Tạo user +  gửi mail + save
                    UserAccountEntity userAccountEntity = createUser(newUser);
                    sendMailService.registerUser(userAccountEntity, code);
                    userRepository.save(userAccountEntity);
                    // xóa 1 đối tượng cũ
                    verificationCodeRepo.deleteById(userByEmail.getUserId());
                    keyRepo.deleteById(userByEmail.getUserId());
                    userRepository.deleteById(userByEmail.getUserId());

                    // Lấy idUser vừa thêm
                    UserAccountEntity currentUser = userRepository.findFirstByUserEmail(newUser.getUserEmail());

                    /* Tạo VerificationCode và kiểm tra xem có email đó chưa*/
                    VerificationCodeEntity codeByEmail = verificationCodeRepo.findFirstByEmail(newUser.getUserEmail());
                    /* Email đã có trong bảng verificationCode thì cập nhật code với hạn mới */
                    if (codeByEmail != null) {
                        verificationCodeRepo.delete(codeByEmail);
                    }

                    /* Tạo mới */
                    VerificationCodeEntity verificationCode = VerificationCodeEntity.create(
                            currentUser.getUserId(),
                            newUser.getUserEmail(),
                            code,
                            currentDay
                    );
                    verificationCodeRepo.save(verificationCode);

                    /* Thêm vào bảng key */
                        // Kiểm tra xem đã tồn tại userID trong bảng Key chưa
                    KeyTokenEntity keyByUserId = keyRepo.findFirstByUserId(currentUser.getUserId());

                    if (keyByUserId != null) {
                        keyRepo.delete(keyByUserId);
                    }

                    KeyTokenEntity newKey = KeyTokenEntity.create(
                            currentUser.getUserId(),
                            privateKey,
                            publicKey,
                            jwtService.generateRefreshToken(userByEmail.getUserEmail(), privateKey)
                    );
                    keyRepo.save(newKey);

                    return RegisterEnum.SUCCESS;
                }
                /* 2.1.2. Email đã được kích hoạt */
                else {
                    /*Thông báo cho người dùng không sử dụng email này */
                    return RegisterEnum.DUPLICATE_EMAIL;
                }
            }
            /* 2.2 Email chưa tồn tại */
            else {

                /* Tạo user mới hoàn toàn */
                // Tạo user +  gửi mail + save
                UserAccountEntity userAccountEntity = createUser(newUser);
                sendMailService.registerUser(userAccountEntity, code);
                userRepository.save(userAccountEntity);

                // Lấy idUser vừa thêm
                UserAccountEntity currentUser = userRepository.findFirstByUserEmail(newUser.getUserEmail());

                /* Tạo VerificationCode và kiểm tra xem có email đó chưa*/
                VerificationCodeEntity codeByEmail = verificationCodeRepo.findFirstByEmail(newUser.getUserEmail());
                /* Email đã có trong bảng verificationCode thì cập nhật code với hạn mới */
                if (codeByEmail != null) {
                    verificationCodeRepo.delete(codeByEmail);
                }

                /* Tạo mới */
                VerificationCodeEntity verificationCode = VerificationCodeEntity.create(
                        currentUser.getUserId(),
                        newUser.getUserEmail(),
                        code,
                        currentDay
                );
                verificationCodeRepo.save(verificationCode);

                /* Tạo mới key */
                KeyTokenEntity newKey = KeyTokenEntity.create(
                        currentUser.getUserId(),
                        privateKey,
                        publicKey,
                        jwtService.generateRefreshToken(userAccountEntity.getUserEmail(), privateKey)
                );
                keyRepo.save(newKey);

                return RegisterEnum.SUCCESS;
            }
        }
//    }

    @Override
    public VerificationEnum verify(String code) {
        VerificationCodeEntity userCode = verificationCodeRepo.findFirstByVerificationCode(code);

        if (userCode == null || !Objects.equals(userCode.getVerificationCode(), code))
            return VerificationEnum.FAILED;
        if (isTimeOutRequired(userCode, Constants.VERIFICATION_CODE_DURATION))
            return VerificationEnum.TIME_OUT;

        UserAccountEntity userByEmail = userRepository.findFirstByUserEmail(userCode.getEmail());
        if (userByEmail.getUserStatus())
            return VerificationEnum.FAILED;
        userByEmail.setUserStatus(true);
        userRepository.save(userByEmail);

        verificationCodeRepo.delete(userCode);
        return VerificationEnum.SUCCESS;
    }

    @Override
    public void forgetPassword(int userId) {

        UserAccountEntity user = userRepository.findFirstByUserId(userId);
        OtpEntity otp = otpRepo.findFirstByUserId(userId);
        String newOtp = GenerateOtpUtil.create(6);

        if (otp == null) {
            OtpEntity newOtpEntity = OtpEntity.create(
                    userId,
                    user.getUserEmail(),
                    newOtp,
                    Constants.getCurrentDay(),
                    0
            );
            otpRepo.save(newOtpEntity);
        } else {
            otp.setOtpCode(newOtp);
            otp.setOtpExp(Constants.getCurrentDay());
            otp.setOtpFailAttempts(0);

            otpRepo.save(otp);
        }
        sendMailService.forgetPasswordUser(user, newOtp);
    }

    @Override
    public VerificationEnum checkOtp(ResetPasswordDto resetPasswordDto) {
        UserAccountEntity userByEmail = userRepository.findFirstByUserEmail(resetPasswordDto.getEmail());
        if (userByEmail == null)
            return VerificationEnum.NOT_FOUND;

        OtpEntity otpByUserId = otpRepo.findFirstByUserId(userByEmail.getUserId());
        if (otpByUserId.getOtpFailAttempts() >=5 )
            return VerificationEnum.FAIL_ATTEMPT;
        if (Objects.equals(otpByUserId.getOtpCode(), resetPasswordDto.getOtp()) && Objects.equals(userByEmail.getUserEmail(), resetPasswordDto.getEmail())){
            if (isTimeOutRequired(otpByUserId, Constants.OTP_VALID_DURATION_5P)) {
                otpByUserId.setOtpFailAttempts(otpByUserId.getOtpFailAttempts() + 1);
                otpRepo.save(otpByUserId);
                return VerificationEnum.TIME_OUT;
            }
            // cập nhật pass
            userByEmail.setUserPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            userByEmail.setUserUpdatedAt(Constants.getCurrentDay());
            userRepository.save(userByEmail);
            // xoá otp
            otpRepo.delete(otpByUserId);
            return VerificationEnum.SUCCESS;
        }
        otpByUserId.setOtpFailAttempts(otpByUserId.getOtpFailAttempts() + 1);
        otpRepo.save(otpByUserId);
        return VerificationEnum.FAILED;
    }
}
