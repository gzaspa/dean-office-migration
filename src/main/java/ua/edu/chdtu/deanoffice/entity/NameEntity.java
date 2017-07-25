package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.*;

@MappedSuperclass
public class NameEntity extends BaseEntity {
    @Column(name="name", nullable = false, unique = true, length = 100)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
