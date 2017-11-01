package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


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
    private String bachelorWorkTheme;

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


    @Override
    public String toString() {
        return getInitials();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronimic() {
        return patronimic;
    }

    public void setPatronimic(String patronimic) {
        this.patronimic = patronimic;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isInActive() {
        return inActive;
    }

    public void setInActive(boolean inActive) {
        this.inActive = inActive;
    }

    public String getStudentFullName() {
        return surname + " " + name + " " + patronimic;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public String getInitials() {
        return surname + " " + name.substring(0, 1) + "." + patronimic.substring(0, 1) + ".";
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSurnameEnglish() {
        return surnameEnglish;
    }

    public void setSurnameEnglish(String surnameEnglish) {
        this.surnameEnglish = surnameEnglish;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getPatronimicEnglish() {
        return patronimicEnglish;
    }

    public void setPatronimicEnglish(String patronimicEnglish) {
        this.patronimicEnglish = patronimicEnglish;
    }

    public String getBachelorWorkTheme() {
        return bachelorWorkTheme;
    }

    public void setBachelorWorkTheme(String bachelorWorkTheme) {
        this.bachelorWorkTheme = bachelorWorkTheme;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherInfo() {
        return motherInfo;
    }

    public void setMotherInfo(String motherInfo) {
        this.motherInfo = motherInfo;
    }

    public String getMotherPhone() {
        return motherPhone;
    }

    public void setMotherPhone(String motherPhone) {
        this.motherPhone = motherPhone;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherInfo() {
        return fatherInfo;
    }

    public void setFatherInfo(String fatherInfo) {
        this.fatherInfo = fatherInfo;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String fatherPhone) {
        this.fatherPhone = fatherPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String generateDocumentName() {
        return getInitials()
                .replace('.', ' ')
                .trim()
                + ".docx";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public String getActualAddress() {
        return actualAddress;
    }

    public void setActualAddress(String actualAddress) {
        this.actualAddress = actualAddress;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRecordBookNumber() {
        return recordBookNumber;
    }

    public void setRecordBookNumber(String recordBookNumber) {
        this.recordBookNumber = recordBookNumber;
    }
}

