package com.example.familytree.controllers;

import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.entities.LinkSharingEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.repositories.*;
import com.example.familytree.services.LinkSharingService;
import com.example.familytree.services.SendMailService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.BearerTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping(path = "/linkSharing")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LinkSharingController {

    private final UserAccountRepo userAccountRepo;
    private final PersonRepo personRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;
    private final FamilyTreeRepo familyTreeRepo;
    private final LinkSharingService linkSharingService;
    private final LinkSharingRepo linkSharingRepo;
    private final SendMailService sendMailService;

    @PostMapping(path = "/create")
    public ResponseEntity<ApiResult<?>> createLinkShare (@RequestParam int familyTreeId, @RequestParam int personId, HttpServletRequest request) {
        ApiResult<?> result;
        // Cây có tồn tại không
        if (!familyTreeRepo.existsByFamilyTreeId(familyTreeId)) {
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_FAMILY_TREE, familyTreeId), null);
            return ResponseEntity.ok(result);
        }
        // Person có tồn tại k
        if (!personRepo.existsByPersonId(personId)){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
            return ResponseEntity.ok(result);
        }
        // Person có trong cây không
        if (!personRepo.existsByFamilyTreeIdAndPersonId(familyTreeId, personId)){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.PERSON_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, personId), null);
            return ResponseEntity.ok(result);
        }

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByToken = userAccountRepo.findFirstByUserEmail(email);

        // Người tạo có trong cây không
        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(familyTreeId, userByToken.getUserId(), true )) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userByToken.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        result = ApiResult.create(HttpStatus.OK, "Tạo Link thành công!", linkSharingService.createLink(familyTreeId, personId, userByToken.getUserId()));
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "")
    public ResponseEntity<ApiResult<?>> joinLink (@RequestParam String code, HttpServletRequest request) {
        ApiResult<?> result;

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByToken = userAccountRepo.findFirstByUserEmail(email);

        // Ktra code có tồn tại không
        LinkSharingEntity linkByCode = linkSharingRepo.findFirstByLink(code);
        if (linkByCode == null) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Link không tồn tại!", null);
            return ResponseEntity.ok(result);
        }
        if (linkSharingService.isTimeOutRequired(linkByCode, Constants.LINK_SHARING_DURATION)){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Link đã hết hạn!", null);
            return ResponseEntity.ok(result);
        }

        // Ktra xem user đã gửi gửi yêu cầu trong cây chưa và đã là thành viên chưa
        if (familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(linkByCode.getFamilyTreeId(), userByToken.getUserId(), false)){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bạn đã gửi yêu cầu tham gia cây rồi!", null);
            return ResponseEntity.ok(result);
        }
        if (familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(linkByCode.getFamilyTreeId(), userByToken.getUserId(), true)){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bạn đã là thành viên rồi không thể gửi yêu cầu tham gia!", null);
            return ResponseEntity.ok(result);
        }

        // service
        FamilyTreeUserEntity newFamilyTreeUser = FamilyTreeUserEntity.create(
                0,
                linkByCode.getFamilyTreeId(),
                userByToken.getUserId(),
                false,
                1,
                null
        );
        familyTreeUserRepo.save(newFamilyTreeUser);

        sendMailService.requestJoinFamilyTree(userAccountRepo.findFirstByUserId(userByToken.getUserId()), familyTreeRepo.findFirstByFamilyTreeId(linkSharingRepo.findFirstByLink(code).getFamilyTreeId()));

        result = ApiResult.create(HttpStatus.BAD_REQUEST, "Gửi yêu cầu tham gia cây thành công. Hãy chờ được duyệt!", null);
        return ResponseEntity.ok(result);
    }

}
