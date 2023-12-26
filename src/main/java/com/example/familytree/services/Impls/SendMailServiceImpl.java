package com.example.familytree.services.Impls;


import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.dto.DataMailDto;
import com.example.familytree.repositories.FamilyTreeUserRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.services.MailService;
import com.example.familytree.services.SendMailService;
import com.example.familytree.shareds.Constants;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service
@AllArgsConstructor
public class SendMailServiceImpl implements SendMailService {
    private MailService mailService;
    private FamilyTreeUserRepo familyTreeUserRepo;
    private UserAccountRepo userAccountRepo;

    private void putPropsDataMail(DataMailDto dataMail, String URL, String fullName, String email) throws MessagingException {
        Map<String, Object> props = new HashMap<>();
        props.put("fullname", fullName);
        props.put("email", email);
        props.put("URL", URL);
        dataMail.setProps(props);

        mailService.sendHtmlMail(dataMail, Constants.TEMPLATE_FILE_NAME.VERIFY_USER);
    }

    @Override
    @Async
    public void forgetPasswordUser(UserAccountEntity user, String otp) {
        try {
            DataMailDto dataMail = new DataMailDto();

            dataMail.setTo(user.getUserEmail());
            dataMail.setSubject(Constants.SEND_MAIL_SUBJECT.USER_FORGET_PASSWORD);

            Map<String, Object> props = new HashMap<>();
            props.put("fullname", user.getUserFullname());
            props.put("email", user.getUserEmail());
            props.put("OTP", otp);
            dataMail.setProps(props);

            mailService.sendHtmlMail(dataMail, Constants.TEMPLATE_FILE_NAME.USER_FORGET_PASSWORD);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async
    public void registerUser(UserAccountEntity user, String verificationCode) {
        try {
            DataMailDto dataMail = new DataMailDto();

            dataMail.setTo(user.getUserEmail());
            dataMail.setSubject(Constants.SEND_MAIL_SUBJECT.USER_REGISTER);

            String URL = Constants.URL_VERIFICATION_CUSTOMER + verificationCode;

            putPropsDataMail(dataMail, URL, user.getUserFullname(), user.getUserEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async
    public void requestJoinFamilyTree(UserAccountEntity user, FamilyTreeEntity familyTree) {
        try {
            List<FamilyTreeUserEntity> familyTreeUserEntityList = familyTreeUserRepo.findByFamilyTreeId(familyTree.getFamilyTreeId()).stream().filter(p -> p.getRoleId() > 1).toList();
            for (FamilyTreeUserEntity item: familyTreeUserEntityList) {
                UserAccountEntity userAccount = userAccountRepo.findFirstByUserId(item.getUserId());
                DataMailDto dataMail = new DataMailDto();

                dataMail.setTo(userAccount.getUserEmail());
                dataMail.setSubject(MessageFormat.format(Constants.SEND_MAIL_SUBJECT.REQUEST_JOIN_FAMILY_TREE, familyTree.getFamilyTreeName()));

                Map<String, Object> props = new HashMap<>();
                props.put("message", MessageFormat.format(Constants.REQUEST_JOIN_FAMILY_TREE_MESSAGE,user.getUserFullname(), familyTree.getFamilyTreeName()));
                dataMail.setProps(props);

                mailService.sendHtmlMail(dataMail, Constants.TEMPLATE_FILE_NAME.REQUEST_JOIN_FAMILY_TREE);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
