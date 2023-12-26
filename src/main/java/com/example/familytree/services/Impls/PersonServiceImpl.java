package com.example.familytree.services.Impls;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.enums.HistoryTypeEnum;
import com.example.familytree.enums.NotiTypeEnum;
import com.example.familytree.models.dto.PersonDto;
import com.example.familytree.models.dto.UpdatePersonDto;
import com.example.familytree.models.response.InfoAddPersonResponse;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.services.HistoryService;
import com.example.familytree.services.NotificationService;
import com.example.familytree.services.PersonService;
import com.example.familytree.shareds.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
    private final JdbcTemplate jdbcTemplate;
    private final NotificationService notificationService;
    private final HistoryService historyService;

    @Override
    public InfoAddPersonResponse getInfoPerson(int personId, String option) {
        PersonEntity personById = personRepo.findFirstByPersonId(personId);
        List<Integer> wife = new ArrayList<>();
        List<Integer> husband = new ArrayList<>();
        List<Integer> sibling = new ArrayList<>();
        List<Integer> children = new ArrayList<>();

        // Nữ 0 Nam 1
        if (personById.getPersonGender()) {
            List<SpouseEntity> listSpouseByHusbandId = spouseRepo.findByHusbandIdAndSpouseStatus(personId, 1).stream().filter(x -> x.getWifeId() != null).toList();
            for (SpouseEntity spouseEntity : listSpouseByHusbandId){
                PersonEntity personByWife = personRepo.findFirstByPersonId(spouseEntity.getWifeId());
                if (personByWife != null && !personByWife.getPersonIsDeleted())
                    wife.add(spouseEntity.getWifeId());
            }

        } else {
            List<SpouseEntity> listSpouseByWifeId = spouseRepo.findByWifeIdAndSpouseStatus(personId, 1).stream().filter(x -> x.getHusbandId() != null).toList();
            for (SpouseEntity spouseEntity : listSpouseByWifeId) {
                PersonEntity personByHusband = personRepo.findFirstByPersonId(spouseEntity.getHusbandId());
                if (personByHusband != null && !personByHusband.getPersonIsDeleted())
                    husband.add(spouseEntity.getHusbandId());
            }
        }
        // Tìm anh chị em cùng parentID trong bảng Person
        if (personById.getParentsId() != null) {
            List<PersonEntity> listPersonByParentsId = personRepo.findByParentsId(personById.getParentsId());
            for (PersonEntity personEntity : listPersonByParentsId)
                if (!personEntity.getPersonIsDeleted())
                    sibling.add(personEntity.getPersonId());
            // Xoá chính người có personId trong sibling
            sibling.removeIf(child -> child == personId);
        }
        // Tìm các con của mình
        // Phải tìm tất cả các spouseId của mình với mọi status
        // Tìm trong bảng Person với điều kiện parentsId bằng với listSpouseId tìm được bên trên
        List<SpouseEntity> listSpouseByHusbandIdOrWifeId;
        if (personById.getPersonGender()) {
            listSpouseByHusbandIdOrWifeId = spouseRepo.findByHusbandId(personId);
        } else {
            listSpouseByHusbandIdOrWifeId = spouseRepo.findByWifeId(personId);
        }
        for (SpouseEntity spouseEntity : listSpouseByHusbandIdOrWifeId) {
            List<PersonEntity> listPersonByParentID = personRepo.findByParentsId(spouseEntity.getSpouseId());
            for (PersonEntity personEntity : listPersonByParentID) {
                if (!personEntity.getPersonIsDeleted())
                    children.add(personEntity.getPersonId());
            }
        }
        // Hiển thị thông tin cá nhân theo option
        InfoAddPersonResponse infoAddPersonResponse = new InfoAddPersonResponse();
        infoAddPersonResponse.setPersonName(personById.getPersonName());
        infoAddPersonResponse.setParentsId(personById.getParentsId());
        infoAddPersonResponse.setFatherId(personById.getFatherId());
        infoAddPersonResponse.setMotherId(personById.getMotherId());
        infoAddPersonResponse.setPersonImage(personById.getPersonImage());
        infoAddPersonResponse.setPersonGender(personById.getPersonGender() ? "Nam" : "Nữ");
        infoAddPersonResponse.setWife(wife);
        infoAddPersonResponse.setHusband(husband);
        infoAddPersonResponse.setSibling(sibling);
        infoAddPersonResponse.setChildren(children);
        if (Objects.equals(option, "full")) {
            BeanUtils.copyProperties(personById, infoAddPersonResponse);
        }


        return infoAddPersonResponse;
    }

    @Override
    public List<PersonEntity> getListChild(int fatherId, int motherId) {

        if (fatherId != 0 && motherId == 0) {
            return personRepo.findByFatherId(fatherId);
        }
        if (fatherId == 0 && motherId != 0) {
            return personRepo.findByMotherId(motherId);
        }
        List<PersonEntity> listByFatherId = personRepo.findByFatherId(fatherId);
        List<PersonEntity> listByMotherId = personRepo.findByMotherId(motherId);
        List<PersonEntity> listByFatherIdAndMotherId = personRepo.findByFatherIdAndMotherId(fatherId, motherId);

        // Thêm các phần tử vào listByFatherId, listByMotherId, và listByFatherIdAndMotherId
        Set<PersonEntity> resultSet = new HashSet<>(listByFatherId);
        resultSet.addAll(listByMotherId);
        resultSet.addAll(listByFatherIdAndMotherId);

        return new ArrayList<>(resultSet);
    }

    @Override
    @Transactional
    public PersonEntity createPersonVirtual(int fid, boolean gender) {
        PersonEntity personVirtual = PersonEntity.create(
                0,
                "PersonName",
                gender,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                fid,
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        personRepo.save(personVirtual);
        return personVirtual;
    }

    @Override
    @Transactional
    public void createPersonCopyVirtual(int fid, int pid, boolean gender) {
        String jqlON = "SET IDENTITY_INSERT Person ON";
        jdbcTemplate.execute(jqlON);
        String sql = "INSERT INTO person(person_id, person_name, person_gender, family_tree_id, person_status) " +
                "values(" + pid + ", 'PersonName', 1," + fid + ", 1)";
        jdbcTemplate.execute(sql);
        String jqlOFF = "SET IDENTITY_INSERT Person OFF";
        jdbcTemplate.execute(jqlOFF);
    }

    @Override
    @Transactional
    public PersonEntity createPersonCopy(PersonEntity person, int fid, int range, UserAccountEntity userByEmail) {
        String jqlON = "SET IDENTITY_INSERT Person ON";
        jdbcTemplate.execute(jqlON);
        int personId = person.getPersonId() + range;
        int groupChild = person.getGroupChildId() + range;
        int gender = person.getPersonGender() ? 1 : 0;

        String sql = "INSERT INTO person(person_id, person_name, person_gender, person_DOB, person_job, person_religion, person_ethnic, person_DOD, person_address, family_tree_id, person_status, person_rank, person_description, person_story, person_is_deleted, person_image, sibling_num, group_child_id) " +
                "VALUES(" + personId + ",N'" + person.getPersonName() + "'," + gender + "," +
                (person.getPersonDob() == null ? "NULL" : ("'" + person.getPersonDob() + "'")) + "," +
                (person.getPersonJob() == null ? "NULL" : ("N'" + person.getPersonJob() + "'")) + "," +
                (person.getPersonReligion() == null ? "NULL" : ("N'" + person.getPersonReligion() + "'")) + "," +
                (person.getPersonEthnic() == null ? "NULL" : ("N'" + person.getPersonEthnic() + "'")) + "," +
                (person.getPersonDod() == null ? "NULL" : ("'" + person.getPersonDod() + "'")) + "," +
                (person.getPersonAddress() == null ? "NULL" : ("N'" + person.getPersonAddress() + "'")) + "," +
                fid + "," + 1 + "," + person.getPersonRank() + "," +
                (person.getPersonDescription() == null ? "NULL" : ("N'" + person.getPersonDescription() + "'")) + "," +
                (person.getPersonStory() == null ? "NULL" : ("N'" + person.getPersonStory() + "'")) + "," + 0 + "," +
                (person.getPersonImage() == null ? "NULL" : ("'" + person.getPersonImage() + "'")) + "," + person.getSiblingNum() + "," + groupChild + ")";

        jdbcTemplate.execute(sql);
        String jqlOFF = "SET IDENTITY_INSERT Person OFF";
        jdbcTemplate.execute(jqlOFF);

        PersonEntity personResult = personRepo.findFirstByPersonId(personId);

        notificationService.HandleInsertNotification(personResult, userByEmail, NotiTypeEnum.CREATE_PERSON);
        historyService.HandleInsertHistory(userByEmail, personResult, HistoryTypeEnum.CREATED, null);
        return personResult;
    }

    @Override
    @Transactional
    public PersonEntity updatePerson(UpdatePersonDto newInfo) {
        PersonEntity currentPerson = personRepo.findFirstByPersonId(newInfo.getPersonId());

        currentPerson.setPersonName(newInfo.getPersonName() == null ? currentPerson.getPersonName() : newInfo.getPersonName());
        currentPerson.setPersonDob(newInfo.getPersonDob() == null ? currentPerson.getPersonDob() : newInfo.getPersonDob());
        currentPerson.setPersonJob(newInfo.getPersonJob() == null ? currentPerson.getPersonJob() : newInfo.getPersonJob());
        currentPerson.setPersonReligion(newInfo.getPersonReligion() == null ? currentPerson.getPersonReligion() : newInfo.getPersonReligion());
        currentPerson.setPersonEthnic(newInfo.getPersonEthnic() == null ? currentPerson.getPersonEthnic() : newInfo.getPersonEthnic());
        currentPerson.setPersonDod(newInfo.getPersonDod() == null ? currentPerson.getPersonDod() : newInfo.getPersonDod());
        currentPerson.setPersonAddress(newInfo.getPersonAddress() == null ? currentPerson.getPersonAddress() : newInfo.getPersonAddress());
        currentPerson.setPersonStatus(newInfo.getPersonStatus() == null ? currentPerson.getPersonStatus() : newInfo.getPersonStatus());
        currentPerson.setPersonDescription(newInfo.getPersonDescription() == null ? currentPerson.getPersonDescription() : newInfo.getPersonDescription());
        currentPerson.setPersonStory(newInfo.getPersonStory() == null ? currentPerson.getPersonStory() : newInfo.getPersonStory());
        currentPerson.setPersonImage(newInfo.getPersonImage() == null ? currentPerson.getPersonImage() : newInfo.getPersonImage());

        // cập nhật parentid
        if (newInfo.getMotherId() != null) {
            SpouseEntity spouseByHusbandAndWife = spouseRepo.findFirstByHusbandIdAndWifeId(currentPerson.getFatherId(), newInfo.getMotherId());
            currentPerson.setMotherId(newInfo.getMotherId());
            currentPerson.setParentsId(spouseByHusbandAndWife.getSpouseId());
        }
        // else if vì tránh set lại 2 lần
        else if (newInfo.getFatherId() != null) {
            SpouseEntity spouseByHusbandAndWife = spouseRepo.findFirstByHusbandIdAndWifeId(newInfo.getFatherId(), currentPerson.getMotherId());
            currentPerson.setFatherId(newInfo.getFatherId());
            currentPerson.setParentsId(spouseByHusbandAndWife.getSpouseId());
        }
        // thứ tự ace
        if (newInfo.getSiblingId() != null) {
            PersonEntity siblingBySiblingId = personRepo.findFirstByPersonId(newInfo.getSiblingId());
            /* Cập nhật SiblingNum */
            List<PersonEntity> listSibling = personRepo.findByGroupChildId(siblingBySiblingId.getGroupChildId());
            for (PersonEntity sibling : listSibling) {
                if (sibling.getSiblingNum() > newInfo.getSiblingNum()) {
                    sibling.setSiblingNum(sibling.getSiblingNum() + 1);
                    personRepo.save(sibling);
                }
            }
        }
        currentPerson.setSiblingNum(newInfo.getSiblingNum() + 0.5);
        personRepo.save(currentPerson);
        return currentPerson;
    }

    @Override
    public PersonEntity createFirstPerson(PersonDto personDto) {
        PersonEntity person = PersonEntity.create(
                0,
                personDto.getPersonName(),
                personDto.getPersonGender(),
                personDto.getPersonDob(),
                personDto.getPersonJob(),
                personDto.getPersonReligion(),
                personDto.getPersonEthnic(),
                personDto.getPersonDod(),
                personDto.getPersonAddress(),
                null,
                personDto.getFamilyTreeId(),
                personDto.getPersonStatus(),
                0,
                personDto.getPersonDescription(),
                personDto.getPersonStory(),
                null,
                null,
                false,
                Constants.getCurrentDay(),
                null,
                null,
                personDto.getPersonImage(),
                1.0,
                null
        );
        personRepo.save(person);
        person.setGroupChildId(person.getPersonId());
        personRepo.save(person);

        return person;
    }

    @Override
    @Transactional
    public PersonEntity createParents(PersonDto parentDto, int personID) {
        PersonEntity person = personRepo.findFirstByPersonId(personID);
        // Thêm vào bảng Person
        PersonEntity parentEntity = createFirstPerson(parentDto);
        // Set lại đời rank
        parentEntity.setPersonRank(person.getPersonRank() + 1);
        personRepo.save(parentEntity);

        /* Trường hợp chưa có ai thì tạo Spouse mới bình thường */
        if (person.getFatherId() == null && person.getMotherId() == null && person.getParentsId() == null) {
            SpouseEntity spouseByParent = SpouseEntity.create(
                    0,
                    parentDto.getPersonGender() ? parentEntity.getPersonId() : null,
                    parentDto.getPersonGender() ? null : parentEntity.getPersonId(),
                    0
            );
            spouseRepo.save(spouseByParent);
            /* Update dữ liệu của Con trong bảng Person*/
            person.setParentsId(spouseByParent.getSpouseId());
            if (parentDto.getPersonGender()) {
                person.setFatherId(parentEntity.getPersonId());
            } else {
                person.setMotherId(parentEntity.getPersonId());
            }
            personRepo.save(person);
        }

        /* Trường hợp muốn thêm bố và nhưng đã có mẹ */
        if (parentDto.getPersonGender() && person.getMotherId() != null) {
            // Tạo Spouse mới
            SpouseEntity newSpouse = SpouseEntity.create(
                    0,
                    parentEntity.getPersonId(),
                    person.getMotherId(),
                    1
            );
            spouseRepo.save(newSpouse);
            // Update dữ liệu của Con trong bảng Person
            person.setFatherId(parentEntity.getPersonId());
            personRepo.save(person);
        }
        /* Trường hợp muốn thêm mẹ và nhưng đã có bố tương tự bên trên */
        if (!parentDto.getPersonGender() && person.getFatherId() != null) {
            // Tạo Spouse mới
            SpouseEntity newSpouse = SpouseEntity.create(
                    0,
                    person.getFatherId(),
                    parentEntity.getPersonId(),
                    1
            );
            spouseRepo.save(newSpouse);
            // Update dữ liệu của Con trong bảng Person
            person.setMotherId(parentEntity.getPersonId());
            personRepo.save(person);
        }
        return parentEntity;
    }

    @Override
    @Transactional
    public PersonEntity createChildren(PersonDto childrenDto, int siblingId) {
        Integer parentId = null;
        Integer rank = null;
        Integer fatherId = childrenDto.getFatherId();
        Integer motherId = childrenDto.getMotherId();
        double siblingNum = 1.0;
        Integer groupChildId = null;

        if (siblingId != 0){
            /* Cập nhật GroupSiblingId c*/
            PersonEntity siblingBySiblingId = personRepo.findFirstByPersonId(siblingId);
//            if (siblingBySiblingId != null)
            groupChildId = siblingBySiblingId.getGroupChildId();

            /* Cập nhật SiblingNum */
            List<PersonEntity> listSibling = personRepo.findByGroupChildId(siblingBySiblingId.getGroupChildId());
            if (!listSibling.isEmpty()) {
                for (PersonEntity sibling : listSibling) {
                    if (sibling.getSiblingNum() > childrenDto.getSiblingNum()) {
                        sibling.setSiblingNum(sibling.getSiblingNum() + 1);
                        personRepo.save(sibling);
                    }
                }
                siblingNum = childrenDto.getSiblingNum() + 0.5;
            }
        }




        /* Trường hợp có cả FatherId và MotherId */
        if (fatherId != null && motherId != null) {
            SpouseEntity spouseByFatherAndMother = spouseRepo.findFirstByHusbandIdAndWifeId(fatherId, motherId);
            // Thêm dữ liệu vào trường parentId của Children
            parentId = spouseByFatherAndMother.getSpouseId();
            rank = personRepo.findFirstByPersonId(fatherId).getPersonRank() - 1;
        }
        /* Trươờng hợp chỉ có FatherId */
        if (fatherId != null && motherId == null) {
            SpouseEntity spouseByFatherAndMother = spouseRepo.findFirstByHusbandIdAndWifeId(fatherId, null);
            // Thêm dữ liệu vào trường parentId của Children
            parentId = spouseByFatherAndMother.getSpouseId();
            rank = personRepo.findFirstByPersonId(fatherId).getPersonRank() - 1;
        }
        /* Trươờng hợp chỉ có MotherId */
        if (fatherId == null && motherId != null) {
            SpouseEntity spouseByFatherAndMother = spouseRepo.findFirstByHusbandIdAndWifeId(null, motherId);
            // Thêm dữ liệu vào trường parentId của Children
            parentId = spouseByFatherAndMother.getSpouseId();
            rank = personRepo.findFirstByPersonId(motherId).getPersonRank() - 1;
        }

        // Thêm vào bảng Person
        PersonEntity newChildren = PersonEntity.create(
                0,
                childrenDto.getPersonName(),
                childrenDto.getPersonGender(),
                childrenDto.getPersonDob(),
                childrenDto.getPersonJob(),
                childrenDto.getPersonReligion(),
                childrenDto.getPersonEthnic(),
                childrenDto.getPersonDod(),
                childrenDto.getPersonAddress(),
                parentId,
                childrenDto.getFamilyTreeId(),
                childrenDto.getPersonStatus(),
                rank,
                childrenDto.getPersonDescription(),
                childrenDto.getPersonStory(),
                fatherId,
                motherId,
                false,
                Constants.getCurrentDay(),
                null,
                null,
                childrenDto.getPersonImage(),
                siblingNum,
                groupChildId
        );

        personRepo.save(newChildren);

        /* Cập nhật lại groupChildId nếu nó là người con đầu tiên */
        if (siblingId == 0) {
            newChildren.setGroupChildId(newChildren.getPersonId());
            personRepo.save(newChildren);
        }

        /* Tạo mới bảng Spouse */
        SpouseEntity newSpouse = SpouseEntity.create(
                0,
                childrenDto.getPersonGender() ? newChildren.getPersonId() : null,
                childrenDto.getPersonGender() ? null : newChildren.getPersonId(),
                0
        );
        spouseRepo.save(newSpouse);

        return newChildren;
    }

    @Override
    @Transactional
    public PersonEntity createSpouse(PersonDto personDto, int personID) {
        PersonEntity person = personRepo.findFirstByPersonId(personID);
        // Thêm vào bảng Person
        PersonEntity newPerson = createFirstPerson(personDto);
        // Set lại đời rank
        newPerson.setPersonRank(person.getPersonRank());

        // Tạo Spouse mới
        SpouseEntity newSpouse = SpouseEntity.create(
                0,
                (person.getPersonGender() ? personID : newPerson.getPersonId()),
                (person.getPersonGender() ? newPerson.getPersonId() : personID),
                1
        );

        // Tạo Spouse rỗng
        if (person.getPersonGender()){
            SpouseEntity spouse = spouseRepo.findFirstByHusbandIdAndWifeId(null, newPerson.getPersonId());
            if (spouse == null){
                SpouseEntity newSpouse1 = SpouseEntity.create(
                        0,
                        null,
                        newPerson.getPersonId(),
                        1
                );
                spouseRepo.save(newSpouse1);
            }
        } else {
            SpouseEntity spouse = spouseRepo.findFirstByHusbandIdAndWifeId(newPerson.getPersonId(), null);
            if (spouse == null){
                SpouseEntity newSpouse1 = SpouseEntity.create(
                        0,
                        newPerson.getPersonId(),
                        null,
                        1
                );
                spouseRepo.save(newSpouse1);
            }
        }

        spouseRepo.save(newSpouse);
        // Tạo Spouse rỗng
        if ((newPerson.getPersonGender() && spouseRepo.findByHusbandId(newPerson.getPersonId()).stream().filter(x -> x.getWifeId() == null).toList().isEmpty()) || (!newPerson.getPersonGender() && spouseRepo.findByWifeId(newPerson.getPersonId()).stream().filter(x -> x.getHusbandId() == null).toList().isEmpty())){
            SpouseEntity newSpouse1 = SpouseEntity.create(
                    0,
                    (newPerson.getPersonGender() ? newPerson.getPersonId() : null),
                    (newPerson.getPersonGender() ? null : newPerson.getPersonId()),
                    1
            );
            spouseRepo.save(newSpouse1);
        }
        personRepo.save(newPerson);
        return newPerson;
    }

}