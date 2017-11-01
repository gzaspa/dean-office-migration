package ua.edu.chdtu.deanoffice.oldentity;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "SUBJECTS")
public class Subject {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "NAME_ENG")
    private String nameEnglish;

    @ManyToOne
    @JoinColumn(name = "SPECIALITY_ID")
    private Speciality speciality;

    @ManyToOne
    @JoinColumn(name = "KC_ID")
    private KnowledgeControl knowledgeControl;

    @OneToMany(mappedBy="subject",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<GroupSubject> groupSubjects;

    @Column(name = "SEMESTER")
    private int semester;

    @Column(name = "GODIN")
    private int hours;

    @Column(name = "ACTIVE1")
    private boolean active;

    @Column(name = "CREDITS")
    private Double credits;

    @Column(name = "ABBR")
    private String abbreviation;

    @Override
    public String toString() {
        return this.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public KnowledgeControl getKnowledgeControl() {
        return knowledgeControl;
    }

    public void setKnowledgeControl(KnowledgeControl knowledgeControl) {
        this.knowledgeControl = knowledgeControl;
    }

    public void setGroupSubjects(Set<GroupSubject> groupSubjects) {
        this.groupSubjects = groupSubjects;
    }

    public Set<GroupSubject> getGroupSubjects() {
        return groupSubjects;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
