package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Specialization extends  NameWithEngEntity {
    @ManyToOne
    private Speciality speciality;
    @ManyToOne
    private Degree degree;
    @ManyToOne
    private Faculty faculty;
    @ManyToOne
    private Department department;
    @Column(name="study_semesters", nullable = false)
    private int studySemesters;
    @Column(name="study_years", nullable = false)
    private BigDecimal studyYears;
    @Column(name="qualification", nullable = false, unique = false, length = 100)
    private String qualification;
    @Column(name="qualification_eng", nullable = false, unique = false, length = 100)
    private String qualificationEng;
    @Column(name="payment_fulltime", nullable = true, precision=15, scale=2)
    private BigDecimal paymentFulltime;
    @Column(name="payment_extramural", nullable = true, precision=15, scale=2)
    private BigDecimal paymentExtramural;
    @Column(name="active", nullable = false)
    private boolean active = true;

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getQualificationEng() {
        return qualificationEng;
    }

    public void setQualificationEng(String qualificationEng) {
        this.qualificationEng = qualificationEng;
    }

    public BigDecimal getPaymentFulltime() {
        return paymentFulltime;
    }

    public void setPaymentFulltime(BigDecimal paymentFulltime) {
        this.paymentFulltime = paymentFulltime;
    }

    public BigDecimal getPaymentExtramural() {
        return paymentExtramural;
    }

    public void setPaymentExtramural(BigDecimal paymentExtramural) {
        this.paymentExtramural = paymentExtramural;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
