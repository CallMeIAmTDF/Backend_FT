package com.example.familytree.controllers;


import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.repositories.NotificationRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.utils.BearerTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/notification")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepo notificationRepo;
    private final UserAccountRepo userAccountRepo;

    @GetMapping("/list")
    public ResponseEntity<ApiResult<?>> list(HttpServletRequest request) {
        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(email);

        ApiResult<?> result = ApiResult.create(HttpStatus.OK, "Lấy thành công danh sách thông báo của người dùng!", notificationRepo.findByReceiveId(userByEmail.getUserId()));
        return ResponseEntity.ok(result);
    }
}
