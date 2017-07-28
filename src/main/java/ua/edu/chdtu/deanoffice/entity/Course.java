package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Course extends BaseEntity {
    @ManyToOne
    private CourseName courseName;
    @ManyToOne
    private Specialization specialization;
    @Column(name="semester", nullable = false)
    private Integer semester;
    @ManyToOne
    private KnowledgeControl knowledgeControl;
    @Column(name="hours", nullable = false)
    private Integer hours;

    public CourseName getCourseName() {
        return courseName;
    }

    public void setCourseName(CourseName courseName) {
        this.courseName = courseName;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public KnowledgeControl getKnowledgeControl() {
        return knowledgeControl;
    }

    public void setKnowledgeControl(KnowledgeControl knowledgeControl) {
        this.knowledgeControl = knowledgeControl;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}