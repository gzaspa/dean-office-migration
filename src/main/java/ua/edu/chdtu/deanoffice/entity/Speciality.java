package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Speciality extends NameWithEngAndActiveEntity {
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "field_of_study_code", length = 20)
    private String fieldOfStudyCode;
    @Column(name = "field_of_study", length = 150)
    private String fieldOfStudy;
    @Column(name = "field_of_study_eng", length = 150)
    private String fieldOfStudyEng;
}
