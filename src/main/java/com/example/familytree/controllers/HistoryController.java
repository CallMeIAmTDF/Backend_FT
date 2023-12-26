package com.example.familytree.controllers;


import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.repositories.FamilyTreeUserRepo;
import com.example.familytree.repositories.HistoryRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.BearerTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
@RequestMapping(path = "/history")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryRepo historyRepo;
    private final UserAccountRepo userAccountRepo;
    private final FamilyTreeRepo familyTreeRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;

    @GetMapping("/list")
    public ResponseEntity<ApiResult<?>> list(int familyTreeId, HttpServletRequest request) {
        ApiResult<?> result;
        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(email);

        // Cây có tồn tại
        if (!familyTreeRepo.existsByFamilyTreeId(familyTreeId)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_FOUND_FAMILY_TREE, familyTreeId), null);
            return ResponseEntity.ok(result);
        }

        // userBytoken có tồn tại trong cây không
        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(familyTreeId, userByEmail.getUserId(), true)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userByEmail.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        result = ApiResult.create(HttpStatus.OK, "Lấy thành công danh sách thông báo của người dùng!", historyRepo.findAllByUserIdAndFamilyTreeId(userByEmail.getUserId(), familyTreeId));
        return ResponseEntity.ok(result);
    }
}
