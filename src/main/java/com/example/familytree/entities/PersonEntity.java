package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Where(clause = "person_is_deleted = false")
@Table(name = "Person")
public class PersonEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "person_id", nullable = false)
    private int personId;
    @Basic
    @Column(name = "person_name", nullable = false, length = 50)
    private String personName;
    @Basic
    @Column(name = "person_gender", nullable = false)
    private Boolean personGender;
    @Basic
    @Column(name = "person_DOB", nullable = true)
    private Date personDob;
    @Basic
    @Column(name = "person_job", nullable = true, length = 50)
    private String personJob;
    @Basic
    @Column(name = "person_religion", nullable = true, length = 30)
    private String personReligion;
    @Basic
    @Column(name = "person_ethnic", nullable = true, length = 30)
    private String personEthnic;
    @Basic
    @Column(name = "person_DOD", nullable = true)
    private Date personDod;
    @Basic
    @Column(name = "person_address", nullable = true, length = 50)
    private String personAddress;
    @Basic
    @Column(name = "parents_id", nullable = true)
    private Integer parentsId;
    @Basic
    @Column(name = "family_tree_id", nullable = false)
    private Integer familyTreeId;
    @Basic
    @Column(name = "person_status", nullable = false)
    private Boolean personStatus;
    @Basic
    @Column(name = "person_rank", nullable = true)
    private Integer personRank;
    @Basic
    @Column(name = "person_description", nullable = true, length = 200)
    private String personDescription;
    @Basic
    @Column(name = "person_story", nullable = true, length = 200)
    private String personStory;
    @Basic
    @Column(name = "father_id", nullable = true)
    private Integer fatherId;
    @Basic
    @Column(name = "mother_id", nullable = true)
    private Integer motherId;
    @Basic
    @Column(name = "person_is_deleted", nullable = true)
    private Boolean personIsDeleted;
    @Basic
    @Column(name = "person_created_at", nullable = true)
    private Date personCreatedAt;
    @Basic
    @Column(name = "person_updated_at", nullable = true)
    private Date personUpdatedAt;
    @Basic
    @Column(name = "person_deleted_at", nullable = true)
    private Date personDeletedAt;
    @Basic
    @Column(name = "person_image", nullable = true, length = 2147483647)
    private String personImage;
    @Basic
    @Column(name = "sibling_num", nullable = true, precision = 0)
    private Double siblingNum;
    @Basic
    @Column(name = "group_child_id", nullable = true)
    private Integer groupChildId;

}
