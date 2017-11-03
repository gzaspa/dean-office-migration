package ua.edu.chdtu.deanoffice.entity.superclasses;

import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;

@MappedSuperclass
public class NameEntity extends BaseEntity {
    @Column(name="name", nullable = false, length = 100)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return this.name;
    }
}
