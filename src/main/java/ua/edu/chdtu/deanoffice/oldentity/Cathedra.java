package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.*;


@Entity
@Table(name = "PIDROZDILI")
public class Cathedra {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ACTIVE1")
    @Type(type = "true_false")
    private boolean active;

    @Column(name = "ABBR_NAME")
    private String abbreviation;

    @ManyToOne(targetEntity = Department.class)
    @JoinColumn(name = "FACULTET")
    private Department department;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
