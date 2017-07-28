package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Teacher extends BaseEntity {
    @Column(name="surname", nullable = false, length = 20)
    private String surname;
    @Column(name="name", nullable = false, length = 20)
    private String name;
    @Column(name="patronimic", nullable = false, length = 20)
    private String patronimic;
    @ManyToOne
    private Department department;
    @ManyToOne
    private Position position;
    @Column(name="scientific_degree")
    private String scientificDegree;
    @Column(name="sex", nullable = false)
    char sex = 'm';
    @Column(name="active", nullable = false)
    private boolean active = true;

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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getScientificDegree() {
        return scientificDegree;
    }

    public void setScientificDegree(String scientificDegree) {
        this.scientificDegree = scientificDegree;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
