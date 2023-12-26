package com.example.familytree.controllers;

import com.example.familytree.entities.*;
import com.example.familytree.models.ApiResult;
import com.example.familytree.repositories.*;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.LinkSharingService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.BearerTokenUtil;
import com.example.familytree.utils.SearchPersonByName;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class DisplayController {
    private final PersonRepo personRepo;
    private final FamilyTreeService familyTreeService;
    private final LinkSharingRepo linkSharingRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;
    private final LinkSharingService linkSharingService;
    private final UserAccountRepo userAccountRepo;


    @GetMapping(path = "/personSearch") //Tìm kiếm
    public List<PersonEntity> searchPerson(@RequestParam int familyTreeId, @RequestParam(defaultValue = "") String keyword){
        ArrayList<PersonEntity> list =  new ArrayList<>(personRepo.findByFamilyTreeId(familyTreeId));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for(PersonEntity p : list){
            if(!p.getPersonIsDeleted()){ //chua xoa
                listPerson.add(p);
            }
        }
        return SearchPersonByName.searchPerson(listPerson, keyword);
    }
    @GetMapping(path = "/getFamilyIdByCode")
    Map<String, Integer> getFamilyIdByCode(@RequestParam(defaultValue = "") String code, HttpServletRequest request){
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
            if(userByEmail == null){
                res.put("UserStatus", -1);
            }else{
                FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(fid, userByEmail.getUserId());
                if(familyTreeUser == null) res.put("UserStatus", -1);
                else if(familyTreeUser.getUserTreeStatus()) res.put("UserStatus", 1);
                else res.put("UserStatus", 0);
            }
        }
        return res;
    }
    @GetMapping(path = "/checkStatusUser")
    Map<String, Integer> checkStatusUser(@RequestParam Integer fid, HttpServletRequest request){
        Map<String, Integer> res = new HashMap<>();
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);
        if(userByEmail == null){
            res.put("UserStatus", -1);
        }else{
            FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(fid, userByEmail.getUserId());
            if(familyTreeUser == null) res.put("UserStatus", -1);
            else if(familyTreeUser.getUserTreeStatus()) res.put("UserStatus", 1);
            else res.put("UserStatus", 0);
        }
        return res;
    }

    @GetMapping(path = "/getDataV2ByCode")
    ResponseEntity<ApiResult<?>> getDataV2(@RequestParam(defaultValue = "") String code){
        ApiResult<?> result;
        LinkSharingEntity linkSharingEntity = linkSharingRepo.findFirstByLink(code);
        if (linkSharingEntity == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, "Code không hợp lệ", null);
            return ResponseEntity.ok(result);
        }
        else{

            result = ApiResult.create(HttpStatus.OK, "Lấy danh sách hiển thị Person thành công!", familyTreeService.getDataV2(linkSharingEntity.getFamilyTreeId(), linkSharingEntity.getPersonId(), 199203));
            return ResponseEntity.ok(result);
        }
    }
}
