package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Teacher extends Person {
    @ManyToOne
    private Department department;
    @ManyToOne
    private Position position;
    @Column(name="scientific_degree")
    private String scientificDegree;
    @Column(name="sex", nullable = false)
    char sex = 'm';
    @Column(name="valid", nullable = false)
    private boolean valid = true;

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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
