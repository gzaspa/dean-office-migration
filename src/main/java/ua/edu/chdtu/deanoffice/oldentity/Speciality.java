package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "SPECIALITIES")
public class Speciality {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "NAME_BACH")
    private String bachelorName;

    @Column(name = "NAME_MASTER")
    private String masterName;

    @ManyToOne
    @JoinColumn(name = "KAFEDRA_ID")
    private Cathedra cathedra;

    @OneToMany(mappedBy = "speciality")
    private Set<Subject> subjects;

    @ManyToOne
    @JoinColumn(name = "FAKULTET_ID")
    private Department department;

    @Column (name = "ACTIVE1" )
    @Type(type="true_false")
    private boolean active;

    @Column (name = "SHIFR" )
    private String code;

    @Column (name = "SHIFR_BACH" )
    private String bachelorCode;

    @Column (name = "SHIFR_MASTER" )
    private String masterCode;

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

    public String getBachelorName() {
        return bachelorName;
    }

    public void setBachelorName(String bachelorName) {
        this.bachelorName = bachelorName;
    }

    public Cathedra getCathedra() {
        return cathedra;
    }

    public void setCathedra(Cathedra cathedra) {
        this.cathedra = cathedra;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getBachelorCode() {
        return bachelorCode;
    }

    public void setBachelorCode(String bachelorCode) {
        this.bachelorCode = bachelorCode;
    }

    public String getMasterCode() {
        return masterCode;
    }

    public void setMasterCode(String masterCode) {
        this.masterCode = masterCode;
    }
}