package com.example.familytree.services.Impls;

import com.example.familytree.entities.*;
import com.example.familytree.models.dto.UserInfo;
import com.example.familytree.models.response.ListTreeResponse;
import com.example.familytree.models.response.InfoUser;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.*;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.PersonService;
import com.example.familytree.utils.GetPersonByCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class FamilyTreeServiceImpl implements FamilyTreeService {
    private final FamilyTreeRepo familyTreeRepo;
    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;
    private final UserAccountRepo userAccountRepo;
    private final PersonService personService;
    private final JdbcTemplate jdbcTemplate;


    @Override
    public ListTreeResponse getListTreeByUserId(int userId) {
        UserAccountEntity user = userAccountRepo.findFirstByUserId(userId);
        UserInfo userInfo = UserInfo.create(
                user.getUserId(),
                user.getUserEmail(),
                user.getUserFullname()
        );
        List<FamilyTreeEntity> owner = familyTreeRepo.findByUserId(user.getUserId());

        List<FamilyTreeEntity> joined = new ArrayList<>();
        List<FamilyTreeUserEntity> listFamilyTreeJoin = familyTreeUserRepo.findByUserIdAndUserTreeStatus(user.getUserId(), true);
        for (FamilyTreeUserEntity familyTreeUser : listFamilyTreeJoin) {
            joined.add(familyTreeRepo.findFirstByFamilyTreeId(familyTreeUser.getFamilyTreeId()));
        }

        List<FamilyTreeUserEntity> listFamilyTreeRequest = familyTreeUserRepo.findByUserIdAndUserTreeStatus(user.getUserId(), false);
        List<FamilyTreeEntity> requestJoin = new ArrayList<>();
        for (FamilyTreeUserEntity familyTreeUser : listFamilyTreeRequest) {
            requestJoin.add(familyTreeRepo.findFirstByFamilyTreeId(familyTreeUser.getFamilyTreeId()));
        }

        return ListTreeResponse.create(
                userInfo,
                owner,
                joined,
                requestJoin
        );
    }

    @Override
    public List<InfoUser> getListUserByFamilyTreeId(int familyTreeId, int userId) {
        List<InfoUser> result = new ArrayList<>();
        List<FamilyTreeUserEntity> listFamilyTreeUserEntity = familyTreeUserRepo.findByFamilyTreeId(familyTreeId);
        for (FamilyTreeUserEntity familyTreeUser : listFamilyTreeUserEntity) {
            UserAccountEntity user = userAccountRepo.findFirstByUserId(familyTreeUser.getUserId());
            InfoUser infoUser = InfoUser.create(
                    user.getUserId(),
                    user.getUserEmail(),
                    user.getUserId() == userId ? "Tôi" : user.getUserFullname(),
                    familyTreeUser.getRoleId(),
                    familyTreeUser.getUserTreeStatus(),
                    familyTreeUser.getPersonId() == null ? 0 : familyTreeUser.getPersonId(),
                    familyTreeUser.getFamilyTreeId()
            );
            result.add(infoUser);
        }

        return result;
    }

    @Override
    @Transactional
    public FamilyTreeEntity create(FamilyTreeEntity familyTree) {
        // Lưu cây mới tạo vào db
        FamilyTreeEntity newFamilyTree = FamilyTreeEntity.create(
                0,
                familyTree.getUserId(),
                familyTree.getFamilyTreeName(),
                null
        );
        familyTreeRepo.save(newFamilyTree);
        // Thêm người vừa tạo cây vào bảng FamilyTreeUser với vai trò là chủ sở hữu cây
        FamilyTreeUserEntity newFamilyTreeUser = FamilyTreeUserEntity.create(
                0,
                newFamilyTree.getFamilyTreeId(),
                familyTree.getUserId(),
                true,
                3,
                null
        );
        familyTreeUserRepo.save(newFamilyTreeUser);
        return newFamilyTree;
    }

    @Override
    @Transactional
    public FamilyTreeEntity copy(String name, PersonEntity person, int side, UserAccountEntity user) {
        // Lưu cây mới tạo vào db
        FamilyTreeEntity newFamilyTree = FamilyTreeEntity.create(
                0,
                user.getUserId(),
                name,
                null
        );
        familyTreeRepo.save(newFamilyTree);
        // Thêm người vừa tạo cây vào bảng FamilyTreeUser với vai trò là chủ sở hữu cây
        FamilyTreeUserEntity newFamilyTreeUser = FamilyTreeUserEntity.create(
                0,
                newFamilyTree.getFamilyTreeId(),
                user.getUserId(),
                true,
                3,
                null
        );
        familyTreeUserRepo.save(newFamilyTreeUser);



        // Tìm personId lớn nhất trong bảng Person
        PersonEntity personVirtual = personService.createPersonVirtual(newFamilyTree.getFamilyTreeId(), false);
        personRepo.save(personVirtual);
        int maxPersonId = personVirtual.getPersonId() + 1;
        personRepo.delete(personVirtual);
        // Tìm spouse lớn nhất trong bảng Spouse
        SpouseEntity spouseVirtual = SpouseEntity.create(
                0,
                null,
                null,
                0
        );
        spouseRepo.save(spouseVirtual);
        int maxSpouseId = spouseVirtual.getSpouseId() + 1;
        spouseRepo.delete(spouseVirtual);

        List<PersonEntity> listPerson = getListSharingPerson(person.getFamilyTreeId(), person.getPersonId(), side);
        List<SpouseEntity> listSpouse = getListSharingSpouse(listPerson);

        // Tìm personID nhỏ nhất
        int minPersonId = listPerson.get(0).getPersonId();
        for (PersonEntity personEntity : listPerson){
            if (personEntity.getPersonId() < minPersonId)
                minPersonId = personEntity.getPersonId();
        }
        // Tìm spouse nhỏ nhất
        int minSpouseId = listSpouse.get(0).getSpouseId();
        for (SpouseEntity spouseEntity : listSpouse){
            if (spouseEntity.getSpouseId() < minSpouseId)
                minSpouseId = spouseEntity.getSpouseId();
        }

        int rangePersonId = maxPersonId - minPersonId + 1;
        int rangeSpouseId = maxSpouseId - minSpouseId + 1;

        /*Copy Person*/
        for (PersonEntity personEntity : listPerson) {
            PersonEntity personCurrent =  personService.createPersonCopy(personEntity, newFamilyTree.getFamilyTreeId(), rangePersonId, user);
            // Set lại Motherid và fatherID
            if (personEntity.getFatherId() != null) {
                personCurrent.setFatherId(personEntity.getFatherId() + rangePersonId);
                personRepo.save(personCurrent);
            }
            if (personEntity.getMotherId() != null) {
                personCurrent.setMotherId(personEntity.getMotherId() + rangePersonId);
                personRepo.save(personCurrent);
            }

        }


        /*Copy Spouse*/
        for (SpouseEntity spouseEntity : listSpouse) {
            String jqlON = "SET IDENTITY_INSERT Spouse ON";
            jdbcTemplate.execute(jqlON);
            int spouseId = spouseEntity.getSpouseId() + rangeSpouseId;

            String sql = "INSERT INTO Spouse(spouse_id, spouse_status)" +
                    "VALUES(" + spouseId + "," + spouseEntity.getSpouseStatus() + ")";
            jdbcTemplate.execute(sql);
            String jqlOFF = "SET IDENTITY_INSERT Spouse OFF";
            jdbcTemplate.execute(jqlOFF);

            SpouseEntity spouseCurrent = spouseRepo.findFirstBySpouseId(spouseId);
            if (spouseEntity.getHusbandId() != null) {
                if (!personRepo.existsByPersonId(spouseEntity.getHusbandId() + rangePersonId)){
                    personService.createPersonCopyVirtual(newFamilyTree.getFamilyTreeId(), spouseEntity.getHusbandId() + rangePersonId, true);
                }
                spouseCurrent.setHusbandId(spouseEntity.getHusbandId() + rangePersonId);
            }
            if (spouseEntity.getWifeId() != null) {
                if (!personRepo.existsByPersonId(spouseEntity.getWifeId() + rangePersonId)){
                    personService.createPersonCopyVirtual(newFamilyTree.getFamilyTreeId(), spouseEntity.getWifeId() + rangePersonId, false);
                }
                spouseCurrent.setWifeId(spouseEntity.getWifeId() + rangePersonId);
            }
        }

        /* Cập nhật lại parentId trong mỗi Person*/
        for (PersonEntity personEntity : listPerson) {
            if (personEntity.getParentsId() != null) {
                PersonEntity personCopy = personRepo.findFirstByPersonId(personEntity.getPersonId() + rangePersonId);
                if(spouseRepo.existsById(personEntity.getParentsId() + rangeSpouseId)){
                    personCopy.setParentsId(personEntity.getParentsId() + rangeSpouseId);
                    personRepo.save(personCopy);
                }
            }
        }



        // cập nhật lại personId ở cây
        newFamilyTree.setPersonId(person.getPersonId() + rangePersonId);
        familyTreeRepo.save(newFamilyTree);
        newFamilyTreeUser.setPersonId(person.getPersonId() + rangePersonId);
        familyTreeUserRepo.save(newFamilyTreeUser);
        return newFamilyTree;
    }

    @Override
    public List<PersonEntity> getListSharingPerson(int ft, int pid, int side) {
        ArrayList<PersonEntity> list = new ArrayList<>(personRepo.findByFamilyTreeId(ft));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for (PersonEntity p : list) {
            if (!p.getPersonIsDeleted()) { //chua xoa
                listPerson.add(p);
            }
        }
        ArrayList<SpouseEntity> listSpouse = new ArrayList<>();
        for (PersonEntity p : listPerson) {
            int personId = p.getPersonId();
            listSpouse.addAll(spouseRepo.findByHusbandId(personId));
            listSpouse.addAll(spouseRepo.findByWifeId(personId));
        }
        Set<SpouseEntity> set = new LinkedHashSet<>(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
        return GetPersonByCenter.sharingListPerson(ft, pid, listSpouse, listPerson, side); //side = 3: ALL, side = 1: Lấy bên Ngoại, side = 2: Lấy bên nội
    }

    @Override
    public List<SpouseEntity> getListSharingSpouse(List<PersonEntity> listPerson) {
        List<SpouseEntity> listByHusband = new ArrayList<>();
        List<SpouseEntity> listByWife = new ArrayList<>();

        for (PersonEntity person : listPerson) {
            if (person.getPersonGender()) {
                listByHusband.addAll(spouseRepo.findByHusbandId(person.getPersonId()));
            } else {
                listByWife.addAll(spouseRepo.findByWifeId(person.getPersonId()));
            }
        }

        Set<SpouseEntity> resultSet = new HashSet<>(listByHusband);
        resultSet.addAll(listByWife);
        return new ArrayList<>(resultSet);
    }

    @Override
    public List<PersonInfoSimplifiedInfoDis> getPersonSimplified(int pid) {
        //pid: personid
        PersonEntity personById = personRepo.findFirstByPersonId(pid);
        int familyTreeId = personById.getFamilyTreeId();

        ArrayList<PersonEntity> list = new ArrayList<>(personRepo.findByFamilyTreeId(familyTreeId));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for (PersonEntity p : list) {
            if (!p.getPersonIsDeleted()) { //chua xoa
                listPerson.add(p);
            }
        }
        ArrayList<SpouseEntity> listSpouse = new ArrayList<>();
        for (PersonEntity p : listPerson) {
            int personId = p.getPersonId();
            listSpouse.addAll(spouseRepo.findByHusbandId(personId));
            listSpouse.addAll(spouseRepo.findByWifeId(personId));
        }
        Set<SpouseEntity> set = new LinkedHashSet<>(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
//        GetPersonByCenter.GetPersonByCenterDis(ft, pid, listSpouse, listPerson); //Đầy đủ thông tin ArrayList<PersonInfoDisplay>
        return GetPersonByCenter.getPersonSimplified(familyTreeId, pid, listSpouse, listPerson); //Giản lược ArrayList<PersonInfoSimplifiedInfoDis>
    }

    @Override
    public Map<Integer, PersonDataV2> getDataV2(int fid, int pid, int userId) {
        int personId = pid;
        // Trường hợp lấy pid mặc định của cây hoặc user
        if (pid == 0){
            FamilyTreeEntity familyTree = familyTreeRepo.findFirstByFamilyTreeId(fid);
            if (familyTree.getPersonId() == null){
                return Collections.emptyMap();
            }
            personId = familyTree.getPersonId();
            // TH nếu user có person mặc định
            FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(fid, userId);
            if (familyTreeUser.getPersonId() != null){
                personId = familyTreeUser.getPersonId();
            }
        }


        ArrayList<PersonEntity> list =  new ArrayList<>(personRepo.findByFamilyTreeId(fid));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for(PersonEntity p : list){
            if(!p.getPersonIsDeleted()){ //chua xoa
                listPerson.add(p);
            }
        }
        ArrayList<SpouseEntity> listSpouse = new ArrayList<>();
        for (PersonEntity p: listPerson) {
            int personId2 = p.getPersonId();
            listSpouse.addAll(spouseRepo.findByHusbandId(personId2));
            listSpouse.addAll(spouseRepo.findByWifeId(personId2));
        }
        Set<SpouseEntity> set = new LinkedHashSet<>(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
        return GetPersonByCenter.getDataV2(fid, personId, listSpouse, listPerson);
    }
}
