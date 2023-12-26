package com.example.familytree.controllers;

import com.example.familytree.entities.*;
import com.example.familytree.models.ApiResult;
import com.example.familytree.repositories.*;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.LinkSharingService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.BearerTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/familyTree")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FamilyTreeController {

    private final UserAccountRepo userAccountRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;
    private final PersonRepo personRepo;
    private final FamilyTreeRepo familyTreeRepo;
    private final FamilyTreeService familyTreeService;
    private final LinkSharingRepo linkSharingRepo;
    private final LinkSharingService linkSharingService;
    @PostMapping("/create")
    public ResponseEntity<ApiResult<?>> create(HttpServletRequest request, @RequestBody FamilyTreeEntity familyTreeEntity) {
        ApiResult<?> result;
        String email = BearerTokenUtil.getUserName(request);

        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(email);

        if (userByEmail == null || !userByEmail.getUserStatus()) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "UserId trong Token không tồn tại hoặc chưa kích hoạt tài khoản!", null);
            return ResponseEntity.ok(result);
        }
        FamilyTreeEntity familyTree = FamilyTreeEntity.create(
                0,
                userByEmail.getUserId(),
                familyTreeEntity.getFamilyTreeName(),
                null
        );
        FamilyTreeEntity newFamilyTree =  familyTreeService.create(familyTree);
        result = ApiResult.create(HttpStatus.OK, "Tạo thành công Family Tree", newFamilyTree);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/firstPersonId")
    public ResponseEntity<ApiResult<?>> firstPersonId(@RequestParam int familyTreeId, HttpServletRequest request) {
        ApiResult<?> result;

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByToken = userAccountRepo.findFirstByUserEmail(email);
        // Cây có tồn tại
        if (!familyTreeRepo.existsByFamilyTreeId(familyTreeId)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_FOUND_FAMILY_TREE, familyTreeId), null);
            return ResponseEntity.ok(result);
        }

        // userBytoken có tồn tại trong cây không
        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(familyTreeId, userByToken.getUserId(), true)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userByToken.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        FamilyTreeEntity familyTree = familyTreeRepo.findFirstByFamilyTreeId(familyTreeId);

        result = ApiResult.create(HttpStatus.OK, "Lấy thành công firstPersonId trong cây!", familyTree.getPersonId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResult<?>> list(HttpServletRequest request) {
        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(email);

        ApiResult<?> result = ApiResult.create(HttpStatus.OK, "lấy thành công danh sách cây của người dùng!", familyTreeService.getListTreeByUserId(userByEmail.getUserId()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/listUser")
    public ResponseEntity<ApiResult<?>> listUser(@RequestParam int familyTreeId, HttpServletRequest request) {
        ApiResult<?> result;

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByToken = userAccountRepo.findFirstByUserEmail(email);
        // Cây có tồn tại
        if (!familyTreeRepo.existsByFamilyTreeId(familyTreeId)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_FOUND_FAMILY_TREE, familyTreeId), null);
            return ResponseEntity.ok(result);
        }

        // userBytoken có tồn tại trong cây không
        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(familyTreeId, userByToken.getUserId(), true)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userByToken.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        result = ApiResult.create(HttpStatus.OK, "Lấy thành công User trong cây!", familyTreeService.getListUserByFamilyTreeId(familyTreeId, userByToken.getUserId()));
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/actionUser")
    public ResponseEntity<ApiResult<?>> actionUser(@RequestParam int familyTreeId, @RequestParam int userId, @RequestParam String action, HttpServletRequest request){
        ApiResult<?> result ;

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByToken = userAccountRepo.findFirstByUserEmail(email);
        FamilyTreeUserEntity familyTreeUserByToken = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(familyTreeId, userByToken.getUserId());
        // userBytoken có tồn tại trong cây không
        if (familyTreeUserByToken == null) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userByToken.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        if (!familyTreeRepo.existsByFamilyTreeId(familyTreeId)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_FOUND_FAMILY_TREE, familyTreeId), null);
            return ResponseEntity.ok(result);
        }

        // Tiính hợp lệ giữa familyId và user
        FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(familyTreeId, userId);
        if (familyTreeUser == null) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userId), null);
            return ResponseEntity.ok(result);
        }


        switch (action) {
            case "upRole" -> {
                // Chỉ có Role = 3 mới được thêm hoặc xoá quyền
                if (familyTreeUserByToken.getRoleId() != 3) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "User phải có roleId = 3 mới được thêm hoặc huỷ quyền!", null);
                    return ResponseEntity.ok(result);
                }
                if (familyTreeUser.getRoleId() != 1) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Không thể thêm quyền với người có RoleId = 2 và 3!", null);
                    return ResponseEntity.ok(result);
                }
                // service
                familyTreeUser.setRoleId(2);
                familyTreeUserRepo.save(familyTreeUser);
                result = ApiResult.create(HttpStatus.OK, "Thêm quyền thành công!", familyTreeUser);
                return ResponseEntity.ok(result);
            }
            case "downRole" -> {
                // Chỉ có Role = 3 mới được thêm hoặc xoá quyền
                if (familyTreeUserByToken.getRoleId() != 3) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "User phải có roleId = 3 mới được thêm hoặc huỷ quyền!", null);
                    return ResponseEntity.ok(result);
                }
                if (familyTreeUser.getRoleId() != 2) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Không thể huỷ quyền với người có RoleId = 1 và 3!", null);
                    return ResponseEntity.ok(result);
                }
                // service
                familyTreeUser.setRoleId(1);
                familyTreeUserRepo.save(familyTreeUser);
                result = ApiResult.create(HttpStatus.OK, "Huỷ quyền thành công!", familyTreeUser);
                return ResponseEntity.ok(result);
            }
            case "kick" -> {
                // Chỉ có Role = 2 và 3 mới được kick
                if (familyTreeUserByToken.getRoleId() == 1) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bạn là chỉ là thành viên không có quyền kick!", null);
                    return ResponseEntity.ok(result);
                }
                if (familyTreeUserByToken.getRoleId() <= familyTreeUser.getRoleId()){
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bạn không thể kick User có Role bằng hoặc lớn hơn mình!", null);
                    return ResponseEntity.ok(result);
                }

                familyTreeUserRepo.delete(familyTreeUser);
                result = ApiResult.create(HttpStatus.OK, "Xoá thành công User ra khỏi cây!", familyTreeUser);
                return ResponseEntity.ok(result);
            }
            case "accept" -> {
                // Chỉ có Role = 2 và 3 mới được accept
                if (familyTreeUserByToken.getRoleId() == 1) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bạn là chỉ là thành viên không có quyền accept!", null);
                    return ResponseEntity.ok(result);
                }
                familyTreeUser.setUserTreeStatus(true);
                familyTreeUserRepo.save(familyTreeUser);
                result = ApiResult.create(HttpStatus.OK, "Cho phép user tham gia sơ đồ thành công!", familyTreeUser);
                return ResponseEntity.ok(result);
            }
            default -> {
                result = ApiResult.create(HttpStatus.BAD_REQUEST, "Action không hợp lệ!", null);
                return ResponseEntity.ok(result);
            }
        }
    }

    @PostMapping(path = "/copy")
    public ResponseEntity<ApiResult<?>> copy(@RequestParam String newName, @RequestParam int personId, @RequestParam int side, HttpServletRequest request){
        ApiResult<?> result;
        // Ktra person
        PersonEntity personById = personRepo.findFirstByPersonId(personId);
        if (personById == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
            return ResponseEntity.ok(result);
        }
        int familyTreeId = personById.getFamilyTreeId();
        // ktra người dùng có trong cây k
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(familyTreeId, userByEmail.getUserId(), true)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), personId), null);
            return ResponseEntity.ok(result);
        }
        // Side phải là 1, 2, 3
        if (side != 1 && side != 2 && side !=3) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Side phải thuộc tập giá trị {1, 2, 3}!!!", null);
            return ResponseEntity.ok(result);
        }
        // Ktra cây trống không

        //service
        result = ApiResult.create(HttpStatus.OK, "Sao chép thành công!", familyTreeService.copy(newName, personById, side, userByEmail));
        return ResponseEntity.ok(result);
    }


    @GetMapping(path = "/getPersonSimplified")
    ResponseEntity<ApiResult<?>> getPersonSimplified (@RequestParam int pid){
        ApiResult<?> result;
        // Ktra person
        PersonEntity personById = personRepo.findFirstByPersonId(pid);
        if (personById == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, pid), null);
            return ResponseEntity.ok(result);
        }

        result = ApiResult.create(HttpStatus.OK, "Success", familyTreeService.getPersonSimplified(pid));
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/getDataV2")
    ResponseEntity<ApiResult<?>> getDataV2 (@RequestParam int fid, @RequestParam(required = false, defaultValue = "0") int pid, HttpServletRequest request){
        ApiResult<?> result;
        // Người dùng có trong cây không
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);
        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(fid, userByEmail.getUserId(), true)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), pid), null);
            return ResponseEntity.ok(result);
        }

        // Ktra cây
        FamilyTreeEntity familyTree = familyTreeRepo.findFirstByFamilyTreeId(fid);
        if (familyTree == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_FAMILY_TREE, fid), null);
            return ResponseEntity.ok(result);
        }

        if (pid != 0) {
            // Ktra person
            PersonEntity personById = personRepo.findFirstByPersonId(pid);
            if (personById == null){
                result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, pid), null);
                return ResponseEntity.ok(result);
            }

            // Dữ liệu k đồng bộ
            if (fid != personById.getFamilyTreeId() ) {
                result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.PERSON_DOES_NOT_EXITS_IN_TREE_ID, fid, pid) , null);
                return ResponseEntity.ok(result);
            }
        }

        result = ApiResult.create(HttpStatus.OK, "Lấy danh sách hiển thị Person thành công!", familyTreeService.getDataV2(fid, pid, userByEmail.getUserId()));
        return ResponseEntity.ok(result);
    }
    @GetMapping(path = "/getFamilyIdByCode")
        Map<String, Integer> getFamilyIdByCode(@RequestParam(defaultValue = "") String code,  HttpServletRequest request){
            LinkSharingEntity linkSharingEntity = linkSharingRepo.findFirstByLink(code);
            Map<String, Integer> res = new HashMap<>();
            if(linkSharingEntity != null && !linkSharingService.isTimeOutRequired(linkSharingEntity, Constants.LINK_SHARING_DURATION)){
                res.put("fid", linkSharingEntity.getFamilyTreeId());
                res.put("pid", linkSharingEntity.getPersonId());
                int fid = linkSharingEntity.getFamilyTreeId();
                int uid = linkSharingEntity.getUserId();
                // ktra người dùng có trong cây k
                String username = BearerTokenUtil.getUserName(request);
                UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

                FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(fid, userByEmail.getUserId());
                if(familyTreeUser == null) res.put("UserStatus", -1);
                else if(familyTreeUser.getUserTreeStatus()) res.put("UserStatus", 1);
                else res.put("UserStatus", 0);
            }
            return res;
        }
}
