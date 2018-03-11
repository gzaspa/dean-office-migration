package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.*;


@Entity
@Table(name = "FACULTETI")
public class Department {

    public static final int DEPARTMENT_ID_FITIS = 18;

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ABBR")
    private String abbreviation;

    @Column(name = "IN_ZVIT")
    @Type(type = "true_false")
    private boolean accountable;

    @Column(name = "ACTIVE1")
    @Type(type = "true_false")
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "DEAN_ID")
    private Teacher dean;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Teacher getDean() {
        return dean;
    }

    public void setDean(Teacher dean) {
        this.dean = dean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public boolean isAccountable() {
        return accountable;
    }

    public void setAccountable(boolean accountable) {
        this.accountable = accountable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
