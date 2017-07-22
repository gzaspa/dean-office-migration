package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Speciality extends NameEntity {
    @Column(name="name_eng", nullable = false, unique = true, length = 100)
    private String nameEng;
    @ManyToOne
    private Degree degree;
    @Column(name="code", nullable = false, unique = true, length = 20)
    private String code;
    @ManyToOne
    Faculty faculty;
    @ManyToOne
    Department department;
    @Column(name="qualification", nullable = false, unique = false, length = 100)
    private String qualification;
    @Column(name="qualification_eng", nullable = false, unique = false, length = 100)
    private String qualificationEng;
    @Column(name="payment_fulltime", nullable = true, unique = false)
    private double paymentFulltime;
    @Column(name="payment_extramural", nullable = true, unique = false)
    private double paymentExtramural;
    @Column(name="active", nullable = false)
    private boolean active;

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
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

    public double getPaymentFulltime() {
        return paymentFulltime;
    }

    public void setPaymentFulltime(double paymentFulltime) {
        this.paymentFulltime = paymentFulltime;
    }

    public double getPaymentExtramural() {
        return paymentExtramural;
    }

    public void setPaymentExtramural(double paymentExtramural) {
        this.paymentExtramural = paymentExtramural;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
