package com.example.familytree.utils;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.models.dto.PersonDisplayDto;
import com.example.familytree.models.dto.PersonSimplifiedInfo;
import com.example.familytree.models.dto.SideDto;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoDisplay;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetPersonByCenter {

    public static int getParentIdByPersonId(int personId, ArrayList<PersonEntity> listPerson) {
        for (PersonEntity person : listPerson) {
            if (person.getPersonId() == personId) {
                if (person.getParentsId() == null) return 0;
                return person.getParentsId();
            }
        }
        return 0;
    }
    public static PersonEntity findByPersonId(Integer personId, ArrayList<PersonEntity> personEntities){
        if(personId == null || personId == 0){
            return null;
        }
        for(PersonEntity p : personEntities){
            if(p.getPersonId() == personId){
                return p;
            }
        }
        return null;
    }
    public static int getPersonIdInTreeBySpouse(ArrayList<Integer> personWithCenter, ArrayList<SpouseEntity> listSpouse, int personId, ArrayList<PersonEntity> personEntities){
        ArrayList<Integer> listPBS = getPersonIdBySpouseId(listSpouse, personId, personEntities);
        for(int i : listPBS ){
            if (personWithCenter.contains(i)){
                return i;
            }
        }
        return 0;
    }
    public static int getFatherIdByPersonId(int personId, ArrayList<PersonEntity> listPerson) {
        for (int i = 0; i < listPerson.size(); i++) {
            if (listPerson.get(i).getPersonId() == personId) {
                if (listPerson.get(i).getFatherId() == null) return 0;
                int fid = listPerson.get(i).getFatherId();
                PersonEntity father = listPerson.stream().filter(p -> p.getPersonId() == fid).findFirst().orElse(null);
                if (father == null) {
                    return 0;
                }
                return listPerson.get(i).getFatherId();
            }
        }
        return 0;
    }

    public static int getMotherIdByPersonId(int personId, ArrayList<PersonEntity> listPerson) {
        for (int i = 0; i < listPerson.size(); i++) {
            if (listPerson.get(i).getPersonId() == personId) {
                if (listPerson.get(i).getMotherId() == null) return 0;
                int mid = listPerson.get(i).getMotherId();
                PersonEntity mother = listPerson.stream().filter(p -> p.getPersonId() == mid).findFirst().orElse(null);
                if (mother == null) {
                    return 0;
                }
                return listPerson.get(i).getMotherId();
            }
        }
        return 0;
    }

    public static String getGenderByPersonId(int personId, ArrayList<PersonEntity> listPerson) {
        for (PersonEntity person : listPerson) {
            if (person.getPersonId() == personId) {
                if (person.getPersonGender()) {
                    return "Male";
                } else {
                    return "Female";
                }
            }
        }
        return null;
    }

    public static void getTheMainTree(ArrayList<Integer> personIdInTheMainTree,
                                      int personId, ArrayList<PersonEntity> listPerson,
                                      ArrayList<SideDto> personWithSide,
                                      String side,
                                      int isFatherSide,
                                      Map<Integer, Integer> fatherSide,
                                      Map<Integer, Boolean> intestine) {
        fatherSide.put(personId, isFatherSide);
        int motherId = getMotherIdByPersonId(personId, listPerson);
        int fatherId = getFatherIdByPersonId(personId, listPerson);
        intestine.putIfAbsent(personId, Boolean.TRUE);
        personIdInTheMainTree.add(personId);
        personWithSide.add(SideDto.create(side, personId));
        if (motherId != 0) {
            String mo = side + "1";
            if (isFatherSide == 0) {
                getTheMainTree(personIdInTheMainTree, motherId, listPerson, personWithSide, mo, 2, fatherSide, intestine);
            } else {
                getTheMainTree(personIdInTheMainTree, motherId, listPerson, personWithSide, mo, isFatherSide, fatherSide, intestine);
            }
        }
        if (fatherId != 0) {
            String fa = side + "0";
            if (isFatherSide == 0) {
                getTheMainTree(personIdInTheMainTree, fatherId, listPerson, personWithSide, fa, 1, fatherSide, intestine);
            } else {
                getTheMainTree(personIdInTheMainTree, fatherId, listPerson, personWithSide, fa, isFatherSide, fatherSide, intestine);
            }
        }
    }

    public static int isLarger(PersonEntity personInCenter, PersonEntity person2, ArrayList<Integer> personWithCenter, ArrayList<PersonEntity> personEntities, ArrayList<SpouseEntity> spouseEntities, Map<Integer, Integer> fatherSide, Map<Integer, Boolean> intestine){

        if(person2 == null) return 0;
        if(personInCenter == null) return 0;
        int rank1 = personInCenter.getPersonRank();
        int rank2 = person2.getPersonRank();
        if(rank2 < rank1){ // person2 đi lên
            if(intestine.get(person2.getPersonId()) == Boolean.FALSE){
                return isLarger(personInCenter, findByPersonId(getPersonIdInTreeBySpouse(personWithCenter,
                                        spouseEntities,
                                        person2.getPersonId(),
                                        personEntities),
                                personEntities),
                        personWithCenter,
                        personEntities,
                        spouseEntities,
                        fatherSide,
                        intestine);
            }
            if(person2.getFatherId()!=null && intestine.get(person2.getFatherId()) != null && intestine.get(person2.getFatherId()) == Boolean.TRUE){
                return isLarger(personInCenter, findByPersonId(person2.getFatherId(), personEntities), personWithCenter, personEntities, spouseEntities, fatherSide, intestine);
            }
            if(person2.getMotherId()!=null && intestine.get(person2.getMotherId()) != null && intestine.get(person2.getMotherId()) == Boolean.TRUE){
                return isLarger(personInCenter, findByPersonId(person2.getMotherId(), personEntities), personWithCenter, personEntities, spouseEntities, fatherSide, intestine);
            }
        }
        else if(rank2 > rank1){ //person 1 đi lên
            int fatherId = getFatherIdByPersonId(personInCenter.getPersonId(), personEntities);
            int motherId = getMotherIdByPersonId(personInCenter.getPersonId(), personEntities);
            if(fatherSide.get(person2.getPersonId()) == 1){
                return isLarger(findByPersonId(fatherId, personEntities), person2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine);
            }
            else if(fatherSide.get(person2.getPersonId()) == 2){
                return isLarger(findByPersonId(motherId, personEntities), person2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine);
            }
            return 0;
        }
        else{ //cả 2 cùng đi
            if(personInCenter == person2){ //TRÙNG NHAU
                return 0;
            }
            else if (personInCenter.getGroupChildId().equals(person2.getGroupChildId())){ //Cùng GROUP
                if(personInCenter.getSiblingNum() > person2.getSiblingNum()){ // P1 LỚN HƠN P2
                    return 1;
                }
                else{ //P1 NHỎ HƠN P2
                    return -1;
                }
            }
            else{ //Khác GR
                if(intestine.get(person2.getPersonId()) == Boolean.FALSE) { //RỂ/DÂU
                    int wifId = getPersonIdInTreeBySpouse(personWithCenter, spouseEntities, person2.getPersonId(), personEntities);
                    PersonEntity Wife = findByPersonId(wifId, personEntities);
                    return isLarger(personInCenter, Wife, personWithCenter, personEntities, spouseEntities, fatherSide, intestine);
                }
                else{
                    PersonEntity f1 = findByPersonId(personInCenter.getFatherId(), personEntities);
                    PersonEntity m1 = findByPersonId(personInCenter.getMotherId(), personEntities);
                    PersonEntity f2 = findByPersonId(person2.getFatherId(), personEntities);
                    PersonEntity m2 = findByPersonId(person2.getMotherId(), personEntities);
                    return isLarger(f1, f2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine)
                            + isLarger(f1, m2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine)
                            + isLarger(m1, f2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine)
                            + isLarger(m1, m2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine);
                }
            }
        }
        return 0;
    }
    public static String getVocative(PersonEntity personInCenter, PersonEntity person2, ArrayList<Integer> personWithCenter, ArrayList<PersonEntity> personEntities, ArrayList<SpouseEntity> spouseEntities, Map<Integer, Integer> fatherSide, Map<Integer, Boolean> intestine){
        if(personInCenter == null || person2 == null) return "";
        int rank1 = personInCenter.getPersonRank();
        int rank2 = person2.getPersonRank();
        int x = rank1 - rank2;
        switch (x){
            case -7:
                return "Tiên tổ";
            case -6:
                return "Tiên tổ";
            case -5:
                return "Tiên tổ";
            case -4:
                return "Kỵ";
            case -3:
                return "Cố";
            case -2:
                if(person2.getPersonGender()){
                    return "Ông";
                }
                return "Bà";
            case -1:
                if(personInCenter.getFatherId() != null && personInCenter.getFatherId() == person2.getPersonId()) return "Bố";
                else if (personInCenter.getMotherId() != null && personInCenter.getMotherId() == person2.getPersonId()) return "Mẹ";
                else{
                    if(isLarger(personInCenter, person2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine) > 0) return "Bác";
                    else if(isLarger(personInCenter, person2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine) < 0){
                        if(fatherSide.get(person2.getPersonId()) == 2){
                            if(!personWithCenter.contains(person2.getFatherId()) && !personWithCenter.contains(person2.getMotherId())){
                                if(person2.getPersonGender()) return "Chú";
                                else return "Mợ";
                            }
                            else{
                                if(!person2.getPersonGender()) return "Dì";
                                else return "Cậu";
                            }
                        }
                        if(fatherSide.get(person2.getPersonId()) == 1){
                            if(!personWithCenter.contains(person2.getFatherId()) && !personWithCenter.contains(person2.getMotherId())){
                                if(person2.getPersonGender()) return "Chú";
                                else return "Thím";
                            }
                            else{
                                if(!person2.getPersonGender()) return "Cô";
                                else return "Chú";
                            }
                        }
                    }
                    return "";//bố, mẹ, bác, cô, dì, chú
                }
            case 0:
                if(personInCenter.getGroupChildId().equals(person2.getGroupChildId())){
                    if(personInCenter.getSiblingNum() < person2.getSiblingNum()){
                        return "Em";
                    }
                    else if (personInCenter.getSiblingNum() > person2.getSiblingNum()){
                        if(!person2.getPersonGender()){
                            return "Chị";
                        }
                        else{
                            return "Anh";
                        }
                    }
                    else{
                        return "Tôi";
                    }
                }
                else{
                    if(getPersonIdBySpouseId(spouseEntities, personInCenter.getPersonId(), personEntities).contains(person2.getPersonId())){
                        if(person2.getPersonGender()){
                            return "Chồng";
                        }
                        else{
                            return "Vợ";
                        }
                    }
                    if(isLarger(personInCenter, person2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine) > 0){
                        if(!person2.getPersonGender()){
                            return "Chị";
                        }
                        else{
                            return "Anh";
                        }
                    }
                    else if(isLarger(personInCenter, person2, personWithCenter, personEntities, spouseEntities, fatherSide, intestine) < 0){
                        return "Em";
                    }
                    else{
                        return "";
                    }
                }
            case 1:
                if((person2.getFatherId() != null && person2.getFatherId() == personInCenter.getPersonId()) || (person2.getMotherId() != null && person2.getMotherId() == personInCenter.getPersonId())){
                    return "Con";
                }
                PersonEntity p2 = findByPersonId(getPersonIdInTreeBySpouse(personWithCenter, spouseEntities, person2.getPersonId(), personEntities), personEntities);
                if(p2 == null) return "Cháu";
                if((p2.getFatherId() != null && p2.getFatherId() == personInCenter.getPersonId()) || (p2.getMotherId() != null && p2.getMotherId() == personInCenter.getPersonId())){
                    if(person2.getPersonGender()){
                        return "Con Rể";
                    }
                    else {
                        return "Con dâu";
                    }
                }
                return "Cháu"; //Con, cháu
            case 2:
                return "Cháu"; //cháu
            case 3:
                ArrayList<PersonEntity> parents = new ArrayList<>();
                if(person2.getFatherId() != null){
                    parents.add(findByPersonId(person2.getFatherId(), personEntities));
                }
                if(person2.getMotherId() != null){
                    parents.add(findByPersonId(person2.getMotherId(), personEntities));
                }
                if(parents.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> p = new ArrayList<>();
                for(PersonEntity pa : parents){
                    if(pa.getFatherId() != null){
                        p.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        p.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(p.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> pp = new ArrayList<>();
                for(PersonEntity pa : p){
                    if(pa.getFatherId() != null){
                        pp.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        pp.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(pp.isEmpty()) return "Cháu";
                if(pp.contains(personInCenter)){
                    return "Chắt";
                }
                else{
                    return "Cháu";
                }
            case 4:
                ArrayList<PersonEntity> parents4 = new ArrayList<>();
                if(person2.getFatherId() != null){
                    parents4.add(findByPersonId(person2.getFatherId(), personEntities));
                }
                if(person2.getMotherId() != null){
                    parents4.add(findByPersonId(person2.getMotherId(), personEntities));
                }
                if(parents4.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> p4 = new ArrayList<>();
                for(PersonEntity pa : parents4){
                    if(pa.getFatherId() != null){
                        p4.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        p4.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(p4.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> pp4 = new ArrayList<>();
                for(PersonEntity pa : p4){
                    if(pa.getFatherId() != null){
                        pp4.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        pp4.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(pp4.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> ppp4 = new ArrayList<>();
                for(PersonEntity pa : pp4){
                    if(pa.getFatherId() != null){
                        ppp4.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        ppp4.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(ppp4.isEmpty()) return "Cháu";
                if(ppp4.contains(personInCenter)){
                    return "Chút/Chít";
                }
                else{
                    return "Cháu";
                }
            case 5:
                ArrayList<PersonEntity> parents5 = new ArrayList<>();
                if(person2.getFatherId() != null){
                    parents5.add(findByPersonId(person2.getFatherId(), personEntities));
                }
                if(person2.getMotherId() != null){
                    parents5.add(findByPersonId(person2.getMotherId(), personEntities));
                }
                if(parents5.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> p5 = new ArrayList<>();
                for(PersonEntity pa : parents5){
                    if(pa.getFatherId() != null){
                        p5.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        p5.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(p5.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> pp5 = new ArrayList<>();
                for(PersonEntity pa : p5){
                    if(pa.getFatherId() != null){
                        pp5.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        pp5.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(pp5.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> ppp5 = new ArrayList<>();
                for(PersonEntity pa : pp5){
                    if(pa.getFatherId() != null){
                        ppp5.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        ppp5.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(ppp5.isEmpty()) return "Cháu";
                ArrayList<PersonEntity> pppp = new ArrayList<>();
                for(PersonEntity pa : ppp5){
                    if(pa.getFatherId() != null){
                        pppp.add(findByPersonId(pa.getFatherId(), personEntities));
                    }
                    if(pa.getMotherId() != null){
                        pppp.add(findByPersonId(pa.getMotherId(), personEntities));
                    }
                }
                if(pppp.isEmpty()) return "Cháu";
                if(pppp.contains(personInCenter)){
                    return "Chụt/Chuỵt";
                }
                else{
                    return "Cháu";
                }
            default:
                return "";
        }
    }
    public static ArrayList<Integer> getPersonIdBySpouseId(ArrayList<SpouseEntity> listSpouse, int personId, ArrayList<PersonEntity> personEntities){
        ArrayList<Integer> res = new ArrayList<>();
        for (SpouseEntity spouseEntity : listSpouse) {
            if (spouseEntity.getHusbandId() != null && spouseEntity.getHusbandId().intValue() == personId && spouseEntity.getWifeId() != null) {
                int wid = spouseEntity.getWifeId().intValue();
                personEntities.stream().filter(pe -> pe.getPersonId() == wid).findFirst().ifPresent(p -> res.add(wid));
            }
            if (spouseEntity.getWifeId() != null && spouseEntity.getWifeId().intValue() == personId && spouseEntity.getHusbandId() != null) {
                int hid = spouseEntity.getHusbandId().intValue();
                personEntities.stream().filter(pe -> pe.getPersonId() == hid).findFirst().ifPresent(p -> res.add(hid));
            }
        }
        return res;
    }
    public static ArrayList<Integer> getSpouseIds(ArrayList<SpouseEntity> listSpouse, int personId){
        ArrayList<Integer> spouseIds = new ArrayList<>();
        for (SpouseEntity spouseEntity : listSpouse) {
            if ((spouseEntity.getHusbandId() != null && personId == spouseEntity.getHusbandId().intValue()) || (spouseEntity.getWifeId() != null && personId == spouseEntity.getWifeId().intValue())) {
                spouseIds.add(spouseEntity.getSpouseId());
            }
        }
        return spouseIds;
    }
    public static void getPerson(ArrayList<Integer> personsWithCenter,
                                 ArrayList<Integer> personIdInTheMainTree,
                                 ArrayList<PersonEntity> persons,
                                 ArrayList<SpouseEntity> spouses,
                                 Map<Integer, Integer> fatherSide,
                                 Map<Integer, Boolean> intestine,
                                 Integer centerId, ArrayList<Integer> personIdInTheMainTree2) {
        Set<Integer> personTemp = new HashSet<>();
        for (int i = 0; i < personIdInTheMainTree.size(); i++) {
            if(personIdInTheMainTree.get(i).equals(centerId)){
                ArrayList<Integer> spousePidList2 = getPersonIdBySpouseId(spouses, centerId, persons);
                int isFatherSideBySpouse = fatherSide.get(centerId);
                personsWithCenter.addAll(spousePidList2);
                for (int x : spousePidList2) {
                    fatherSide.putIfAbsent(x, isFatherSideBySpouse);
                    intestine.putIfAbsent(x, Boolean.FALSE);
                }
            }
            int personId = personIdInTheMainTree.get(i);
            int isFatherSide = fatherSide.get(personId);
            int parentId = getParentIdByPersonId(personId, persons);
            if (parentId == 0) {
                boolean isChildOfCenter = false;
                for (int x : personsWithCenter) {
                    if (getParentIdByPersonId(x, persons) == personId) {
                        isChildOfCenter = true;
                        break;
                    }
                }
                if (!isChildOfCenter) {
                    if(!personIdInTheMainTree2.contains(personId)){
                        personsWithCenter.addAll(getPersonIdBySpouseId(spouses, personId, persons));
                    }
                    ArrayList<Integer> spousePidList = getPersonIdBySpouseId(spouses, personId, persons);
                    for(int x: spousePidList){
                        fatherSide.putIfAbsent(x, isFatherSide);
                        intestine.putIfAbsent(x, Boolean.TRUE);
                    }
                }
            }
            String gender = getGenderByPersonId(personId, persons);
            for (PersonEntity person : persons) {
                if ((Objects.equals(gender, "Male") && person.getFatherId() != null && person.getFatherId().intValue() == personId) || (Objects.equals(gender, "Female") && person.getMotherId() != null && person.getMotherId().intValue() == personId)) {

                    int childPersonId = person.getPersonId();
                    if(childPersonId == centerId || !personIdInTheMainTree2.contains(childPersonId)) {
                        personsWithCenter.add(childPersonId);
                        intestine.putIfAbsent(childPersonId, Boolean.TRUE);
                        fatherSide.putIfAbsent(childPersonId, isFatherSide);
                        personTemp.add(childPersonId);
                        ArrayList<Integer> spousePidList2 = getPersonIdBySpouseId(spouses, childPersonId, persons);
                        int isFatherSideBySpouse = fatherSide.get(childPersonId);
                        personsWithCenter.addAll(spousePidList2);
                        for (int x : spousePidList2) {
                            fatherSide.putIfAbsent(x, isFatherSideBySpouse);
                            intestine.putIfAbsent(x, Boolean.FALSE);
                        }
                    }
                }
            }
        }
        if (!personTemp.isEmpty()) {
            getPerson(personsWithCenter, new ArrayList<>(personTemp), persons, spouses, fatherSide, intestine, centerId, personIdInTheMainTree2);
        }
    }
    public static PersonInfoDisplay getInfor(ArrayList<Integer> personsWithCenter,
                                             int personId,
                                             ArrayList<PersonEntity> persons,
                                             ArrayList<SpouseEntity> spouses,
                                             ArrayList<PersonInfoDisplay> apiDisplay,
                                             ArrayList<SideDto> personWithSides,
                                             int personCenterId,
                                             Map<Integer, Integer> fatherSide,
                                             Map<Integer, Boolean> intestine){
        PersonEntity person1 = persons.stream().filter(person -> person.getPersonId() == personId).findFirst().orElse(null);
        SideDto personSide = personWithSides.stream().filter(s -> s.getPersonId() == personId).findFirst().orElse(null);
        String side = "";
        if(personSide != null)
            side = personSide.getSide();
        PersonInfoDisplay api;
        ArrayList<Integer> spouseIds = getSpouseIds(spouses, personId);
        ArrayList<Integer> personBySpouse = getPersonIdBySpouseId(spouses, personId, persons);
        int grId1 = personId;
        int grId2 = personId;
        int count = 0;
        for (int person2 : personBySpouse) {
            PersonInfoDisplay apiCheck = apiDisplay.stream().filter(apid -> apid.getGroupId() == personId).findFirst().orElse(null);
            if (apiCheck == null) {
                for (Integer integer : personsWithCenter) {
                    if (person2 == integer) {
                        grId1 = person2;
                        grId2 = personId;
                        count++;
                        break;
                    }
                }
            } else {
                grId2 = apiCheck.getGroupId();
                grId1 = apiCheck.getGroupId();
                count = 199203;
            }
        }
        PersonEntity personCenter = persons.stream().filter(person -> person.getPersonId() == personCenterId).findFirst().orElse(null);

        String vocative = getVocative(personCenter, person1, personsWithCenter, persons, spouses, fatherSide, intestine);
        assert person1 != null;
        PersonDisplayDto p = PersonDisplayDto.create(person1.getPersonName(), person1.getPersonGender()?"Male":"Female", person1.getPersonDob(), person1.getPersonDod(), person1.getParentsId(), person1.getFamilyTreeId(), person1.getPersonStatus(), person1.getPersonRank(), person1.getFatherId(), person1.getMotherId(), person1.getPersonImage(), person1.getSiblingNum(),person1.getGroupChildId());
        int isFatherSide = fatherSide.get(personId);
        if(count > 1){
            api =  PersonInfoDisplay.create(personId, person1.getParentsId(),p, spouseIds, grId2, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        else{
            api =  PersonInfoDisplay.create(personId, person1.getParentsId(),p, spouseIds, grId1, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        return api;
    }
    public static ArrayList<PersonInfoDisplay> GetPersonByCenterDis(int familyTreeId,
                                                                    int personCenterId,
                                                                    ArrayList<SpouseEntity> listSpouse,
                                                                    ArrayList<PersonEntity> listPerson){

        ArrayList<Integer> personIdInTheMainTree = new ArrayList<>();
        ArrayList<SideDto> personWithSide = new ArrayList<>();
        Map<Integer, Integer> fatherSide = new HashMap<>();
        Map<Integer, Boolean> intestine = new HashMap<>();
        getTheMainTree(personIdInTheMainTree, personCenterId, listPerson, personWithSide, "", 0, fatherSide, intestine);

        ArrayList<Integer> personsWithCenter = new ArrayList<>(personIdInTheMainTree);
        getPerson(personsWithCenter, personIdInTheMainTree, listPerson, listSpouse, fatherSide, intestine, personCenterId, personIdInTheMainTree);

        Set<Integer> sett = new LinkedHashSet<>(personsWithCenter);
        personsWithCenter.clear();
        personsWithCenter.addAll(sett);

        ArrayList<PersonInfoDisplay> apiDisplays = new ArrayList<>();
        for(int i = 0; i < personsWithCenter.size(); i++){
            apiDisplays.add(getInfor(personsWithCenter, personsWithCenter.get(i), listPerson, listSpouse, apiDisplays, personWithSide, personCenterId, fatherSide, intestine));
        }
        apiDisplays.sort(Comparator.comparingInt(PersonInfoDisplay::getId));
        return apiDisplays;
    }
    public static PersonInfoSimplifiedInfoDis getInforSimplified(ArrayList<Integer> personsWithCenter,
                                                                 int personId, ArrayList<PersonEntity> persons,
                                                                 ArrayList<SpouseEntity> spouses,
                                                                 ArrayList<PersonInfoSimplifiedInfoDis> apiDisplay,
                                                                 ArrayList<SideDto> personWithSides,
                                                                 int personCenterId,
                                                                 Map<Integer, Integer> fatherSide,
                                                                 Map<Integer, Boolean> intestine){
        PersonEntity person1 = persons.stream().filter(person -> person.getPersonId() == personId).findFirst().orElse(null);
        SideDto personSide = personWithSides.stream().filter(s -> s.getPersonId() == personId).findFirst().orElse(null);
        String side = "";
        if(personSide != null)
            side = personSide.getSide();
        PersonInfoSimplifiedInfoDis api;
        ArrayList<Integer> spouseIds = getSpouseIds(spouses, personId);
        ArrayList<Integer> personBySpouse = getPersonIdBySpouseId(spouses, personId, persons);
        int grId1 = personId;
        int grId2 = personId;
        int count = 0;
        for (int person2 : personBySpouse) {
            PersonInfoSimplifiedInfoDis apiCheck = apiDisplay.stream().filter(apid -> apid.getGroupId() == personId).findFirst().orElse(null);
            if (apiCheck == null) {
                for (Integer integer : personsWithCenter) {
                    if (person2 == integer) {
                        grId1 = person2;
                        grId2 = personId;
                        count++;
                        break;
                    }
                }
            } else {
                grId2 = apiCheck.getGroupId();
                grId1 = apiCheck.getGroupId();
                count = 199203;
            }
        }
        PersonEntity personCenter = persons.stream().filter(person -> person.getPersonId() == personCenterId).findFirst().orElse(null);
        //String vocative = getVocative(personCenter, person1, personsWithCenter);
        String vocative = getVocative(personCenter, person1, personsWithCenter, persons, spouses, fatherSide, intestine);

        int isFatherSide = fatherSide.get(personId);
        PersonSimplifiedInfo p = PersonSimplifiedInfo.create(person1.getPersonName(),
                person1.getPersonGender()?"Male":"Female",
                person1.getPersonDob(),
                person1.getPersonDod(),
                person1.getPersonStatus());
        if(count > 1){
            api =  PersonInfoSimplifiedInfoDis.create(personId, person1.getParentsId(), p, spouseIds, grId2, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        else{
            api =  PersonInfoSimplifiedInfoDis.create(personId, person1.getParentsId(), p, spouseIds, grId1, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        return api;
    }
    public static ArrayList<PersonInfoSimplifiedInfoDis> getPersonSimplified(int familyTreeId, int personCenterId, ArrayList<SpouseEntity> listSpouse, ArrayList<PersonEntity> listPerson){
        ArrayList<Integer> personIdInTheMainTree = new ArrayList<>();
        ArrayList<SideDto> personWithSide = new ArrayList<>();
        Map<Integer, Integer> fatherSide = new HashMap<>();
        Map<Integer, Boolean> intestine = new HashMap<>();
        getTheMainTree(personIdInTheMainTree, personCenterId, listPerson, personWithSide, "", 0, fatherSide, intestine);

        ArrayList<Integer> personsWithCenter = new ArrayList<>(personIdInTheMainTree);
        getPerson(personsWithCenter, personIdInTheMainTree, listPerson, listSpouse, fatherSide, intestine, personCenterId, personIdInTheMainTree);

        Set<Integer> sett = new LinkedHashSet<>(personsWithCenter);
        personsWithCenter.clear();
        personsWithCenter.addAll(sett);

        ArrayList<PersonInfoSimplifiedInfoDis> apiDisplays = new ArrayList<>();
        for(int i = 0; i < personsWithCenter.size(); i++){
            apiDisplays.add(getInforSimplified(personsWithCenter, personsWithCenter.get(i), listPerson, listSpouse, apiDisplays, personWithSide, personCenterId, fatherSide, intestine));
        }
        apiDisplays.sort(Comparator.comparingInt(PersonInfoSimplifiedInfoDis::getId));
        return apiDisplays;
    }
    public static boolean checkSpouseInList(ArrayList<PersonInfoDisplay> listPersonByCenter, Integer personId){
        for(PersonInfoDisplay per : listPersonByCenter){
            if(per.getId() == personId){
                return true;
            }
        }
        return false;
    }
    public static Map<Integer, PersonDataV2> getDataV2(int familyTreeId, int personCenterId, ArrayList<SpouseEntity> listSpouse, ArrayList<PersonEntity> listPerson){
        Map<Integer, PersonDataV2> apiDislays = new HashMap<>();
        ArrayList<PersonInfoDisplay> listPersonByCenter = GetPersonByCenterDis(familyTreeId, personCenterId, listSpouse, listPerson);
        for(PersonInfoDisplay p : listPersonByCenter){
            int personId = p.getId();
            Integer parentId = p.getParentId();
            Integer fatherId = p.getInfo().getFatherId();
            Integer motherId = p.getInfo().getMotherId();
            ArrayList<Integer> spousePersonIds = getPersonIdBySpouseId(listSpouse, personId, listPerson).stream().filter(sp -> checkSpouseInList(listPersonByCenter, sp)).collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Integer> childrenIds = new ArrayList<>();
            for (PersonInfoDisplay p2 : listPersonByCenter) {
                if ((p2.getInfo().getFatherId() != null && p2.getInfo().getFatherId() == personId) || (p2.getInfo().getMotherId() != null && p2.getInfo().getMotherId() == personId))
                    childrenIds.add(p2.getId());
            }
            PersonDataV2 personDataV2 = PersonDataV2.create(p, fatherId, motherId, spousePersonIds, childrenIds);
            apiDislays.put(personId, personDataV2);
        }
        return apiDislays;
    }
    public static ArrayList<PersonEntity> sharingListPerson(int familyTreeId, int personCenterId, ArrayList<SpouseEntity> listSpouse, ArrayList<PersonEntity> listPerson, int side /*3: ALL, 2: Bố, 1: Mẹ*/){
        ArrayList<Integer> personIdInTheMainTree = new ArrayList<>();
        ArrayList<SideDto> personWithSide = new ArrayList<>();
        Map<Integer, Integer> fatherSide = new HashMap<>();
        Map<Integer, Boolean> intestine = new HashMap<>();
        getTheMainTree(personIdInTheMainTree, personCenterId, listPerson, personWithSide, "", 0, fatherSide, intestine);

        ArrayList<Integer> personsWithCenter = new ArrayList<>(personIdInTheMainTree);
        getPerson(personsWithCenter, personIdInTheMainTree, listPerson, listSpouse, fatherSide, intestine, personCenterId, personIdInTheMainTree);

        Set<Integer> sett = new LinkedHashSet<>(personsWithCenter);
        personsWithCenter.clear();
        personsWithCenter.addAll(sett);

        PersonEntity personCenter = findByPersonId(personCenterId, listPerson);
        ArrayList<PersonEntity> res = new ArrayList<>();

        if(findByPersonId(personCenter.getMotherId(), listPerson) != null){
            res.add(findByPersonId(personCenter.getMotherId(), listPerson));
        }
        if(findByPersonId(personCenter.getFatherId(), listPerson) != null){
            res.add(findByPersonId(personCenter.getFatherId(), listPerson));
        }

        for(int x : personsWithCenter){
            if(fatherSide.get(x) != side){
                PersonEntity p = findByPersonId(x, listPerson);
                if(!res.contains(p)){
                    res.add(p);
                }
            }
        }
        res.sort(Comparator.comparingInt(PersonEntity::getPersonId));
        return res;
    }
    public static PersonInfoDisplay getInfor2(ArrayList<Integer> personsWithCenter,
                                              int personId,
                                              ArrayList<PersonEntity> persons,
                                              ArrayList<SpouseEntity> spouses,
                                              ArrayList<PersonInfoDisplay> apiDisplay,
                                              ArrayList<SideDto> personWithSides,
                                              int personCenterId,
                                              Map<Integer, Integer> fatherSide,
                                              Map<Integer, Boolean> intestine){
        PersonEntity person1 = persons.stream().filter(person -> person.getPersonId() == personId).findFirst().orElse(null);
        SideDto personSide = personWithSides.stream().filter(s -> s.getPersonId() == personId).findFirst().orElse(null);
        String side = "";
        if(personSide != null)
            side = personSide.getSide();
        PersonInfoDisplay api;
        ArrayList<Integer> spouseIds = getSpouseIds(spouses, personId);
        ArrayList<Integer> personBySpouse = getPersonIdBySpouseId(spouses, personId, persons);
        int grId1 = personId;
        int grId2 = personId;
        int count = 0;
        for (int person2 : personBySpouse) {
            PersonInfoDisplay apiCheck = apiDisplay.stream().filter(apid -> apid.getGroupId() == personId).findFirst().orElse(null);
            if (apiCheck == null) {
                for (Integer integer : personsWithCenter) {
                    if (person2 == integer) {
                        grId1 = person2;
                        grId2 = personId;
                        count++;
                        break;
                    }
                }
            } else {
                grId2 = apiCheck.getGroupId();
                grId1 = apiCheck.getGroupId();
                count = 199203;
            }
        }
        PersonEntity personCenter = persons.stream().filter(person -> person.getPersonId() == personCenterId).findFirst().orElse(null);

        //  System.out.println(personCenter.getPersonId() + "    " + (personCenter.getPersonRank() - person1.getPersonRank()) + "   " + person1.getPersonId());

        String vocative = getVocative(personCenter, person1, personsWithCenter, persons, spouses, fatherSide, intestine);
        assert person1 != null;
        PersonDisplayDto p = PersonDisplayDto.create(person1.getPersonName(), person1.getPersonGender()?"Male":"Female", person1.getPersonDob(), person1.getPersonDod(), person1.getParentsId(), person1.getFamilyTreeId(), person1.getPersonStatus(), person1.getPersonRank(), person1.getFatherId(), person1.getMotherId(), person1.getPersonImage(), person1.getSiblingNum(),person1.getGroupChildId());
        int isFatherSide = fatherSide.get(personId);
        if(count > 1){
            api =  PersonInfoDisplay.create(personId, person1.getParentsId(),p, spouseIds, grId2, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        else{
            api =  PersonInfoDisplay.create(personId, person1.getParentsId(),p, spouseIds, grId1, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        return api;
    }
    public static ArrayList<PersonInfoDisplay> getPersonSimplified2(int familyTreeId, int personCenterId, ArrayList<SpouseEntity> listSpouse, ArrayList<PersonEntity> listPerson){
        ArrayList<Integer> personIdInTheMainTree = new ArrayList<>();
        ArrayList<SideDto> personWithSide = new ArrayList<>();
        Map<Integer, Integer> fatherSide = new HashMap<>();
        Map<Integer, Boolean> intestine = new HashMap<>();
        getTheMainTree(personIdInTheMainTree, personCenterId, listPerson, personWithSide, "", 0, fatherSide, intestine);

        ArrayList<Integer> personsWithCenter = new ArrayList<>(personIdInTheMainTree);
        getPerson(personsWithCenter, personIdInTheMainTree, listPerson, listSpouse, fatherSide, intestine, personCenterId, personIdInTheMainTree);

        Set<Integer> sett = new LinkedHashSet<>(personsWithCenter);
        personsWithCenter.clear();
        personsWithCenter.addAll(sett);

        ArrayList<PersonInfoDisplay> apiDisplays = new ArrayList<>();
        for(int i = 0; i < personsWithCenter.size(); i++){
            apiDisplays.add(getInfor2(personsWithCenter, personsWithCenter.get(i), listPerson, listSpouse, apiDisplays, personWithSide, personCenterId, fatherSide, intestine));
        }
        apiDisplays.sort(Comparator.comparingInt(PersonInfoDisplay::getId));
        Set<Integer> settintestine = intestine.keySet();
        for (Integer key : settintestine) {
            System.out.println(key + " " + intestine.get(key));
        }
        return apiDisplays;
    }
}
