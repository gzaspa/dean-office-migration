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

    public String generateDocumentName() {
        return getInitials()
                .replace('.', ' ')
                .trim()
                + ".docx";
    }
}

