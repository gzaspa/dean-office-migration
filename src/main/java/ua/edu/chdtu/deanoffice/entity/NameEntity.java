package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.*;

/**
 * Created by user on 18.07.2017.
 */
@MappedSuperclass
public class NameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, unique = true, length = 11)
    private int id;

    @Column(name="name", nullable = false, unique = true, length = 40)
    private String name;

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
}
