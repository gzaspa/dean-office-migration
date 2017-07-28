package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class StudentGroup extends NameEntity {
    @ManyToOne
    private Specialization specialization;
    @Column(name="creation_year", nullable = false)
    private int creationYear;
    @Column(name="tuition_form", nullable = false)
    private char tuitionForm = 'f';
    @Column(name="tuition_term", nullable = false)
    private char tuitionTerm = 'f';
    @Column(name="study_semesters", nullable = false)
    private int studySemesters;
    @Column(name="study_years", nullable = false)
    private BigDecimal studyYears;
    @Column(name="active", nullable = false)
    private boolean active = true;
    //CURATOR

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public int getCreationYear() {
        return creationYear;
    }

    public void setCreationYear(int creationYear) {
        this.creationYear = creationYear;
    }

    public char getTuitionForm() {
        return tuitionForm;
    }

    public void setTuitionForm(char tuitionForm) {
        this.tuitionForm = tuitionForm;
    }

    public char getTuitionTerm() {
        return tuitionTerm;
    }

    public void setTuitionTerm(char tuitionTerm) {
        this.tuitionTerm = tuitionTerm;
    }

    public int getStudySemesters() {
        return studySemesters;
    }

    public void setStudySemesters(int studySemesters) {
        this.studySemesters = studySemesters;
    }

    public BigDecimal getStudyYears() {
        return studyYears;
    }

    public void setStudyYears(BigDecimal studyYears) {
        this.studyYears = studyYears;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
