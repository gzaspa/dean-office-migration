package ua.edu.chdtu.deanoffice.oldentity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "STUDENTS")
public class Student {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PATRONIMIC")
    private String patronimic;

    @Column(name = "SURNAME_ENG")
    private String surnameEnglish;

    @Column(name = "NAME_ENG")
    private String nameEnglish;

    @Column(name = "PATRONIMIC_ENG")
    private String patronimicEnglish;

    @Column(name = "BIRTH_DATE")
    private Date birthDate;

    @Column(name = "ACTIVE1")
    @Type(type = "true_false")
    private boolean inActive;

    @ManyToOne(targetEntity = Group.class)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @OneToMany(targetEntity = Grade.class)
    @JoinColumn(name = "STUD_ID")
    private Set<Grade> grades;

    @Column(name = "BACHALOR_DIPLOMA_WORK")
    private String bachelorWorkThesis;

    @Column(name = "SPECIALIST_DIPLOMA_WORK")
    private String specialistWorkThesis;

    @Column(name = "MASTER_DIPLOMA_WORK")
    private String masterWorkThesis;

    @Column(name = "BACHALOR_DIPLOMA_NUMBER")
    private String bachelorDiplomaNumber;

    @Column(name = "SPECIALIST_DIPLOMA_NUMBER")
    private String specialistDiplomaNumber;

    @Column(name = "MASTER_DIPLOMA_NUMBER")
    private String masterDiplomaNumber;

    @Column(name = "BACHALOR_DIPLOMA_DATE")
    private Date bachelorDiplomaDate;

    @Column(name = "SPECIALIST_DIPLOMA_DATE")
    private Date specialistDiplomaDate;

    @Column(name = "MASTER_DIPLOMA_DATE")
    private Date masterDiplomaDate;

    @Column(name = "SEX")
    private char sex;

    @Column(name = "MOTHER_NAME")
    private String motherName;

    @Column(name = "MOTHER_INFO")
    private String motherInfo;

    @Column(name = "MOTHER_PHONE")
    private String motherPhone;

    @Column(name = "FATHER_NAME")
    private String fatherName;

    @Column(name = "FATHER_INFO")
    private String fatherInfo;

    @Column(name = "FATHER_PHONE")
    private String fatherPhone;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "ZAMITKI")
    private String notes;

    @ManyToOne(targetEntity = Privilege.class)
    @JoinColumn(name = "PRIVILEGE_ID")
    private Privilege privilege;

    @Column(name = "ACTUAL_ADDRESS")
    private String actualAddress;

    @Column(name = "SCHOOL")
    private String school;

    @Column(name = "N_ZALIKOVKI")
    private String recordBookNumber;

    @Column(name = "MASTER_DIPLOMA_WORK_ENG")
    private String masterDiplomaWordEngName;

    @Column(name = "BACHALOR_SUCCEEDED")
    private boolean bachalorSucceeded;

    @Column(name = "SPECIALIST_SUCCEEDED")
    private boolean specialistSucceeded;

    @Column(name = "MASTER_SUCCEEDED")
    private boolean masterSucceeded;

    public String getMasterDiplomaWorkEngName() {
        return masterDiplomaWordEngName;
    }

    public void setMasterDiplomaWordEngName(String masterDiplomaWordEngName) {
        this.masterDiplomaWordEngName = masterDiplomaWordEngName;
    }

    @Override
    public String toString() {
        return getInitials();
    }

    public String getInitials() {
        return surname + " " + name.substring(0, 1) + "." + patronimic.substring(0, 1) + ".";
    }

    public String generateDocumentName() {
        return getInitials()
                .replace('.', ' ')
                .trim()
                + ".docx";
    }
}

