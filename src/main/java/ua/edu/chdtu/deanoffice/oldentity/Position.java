package ua.edu.chdtu.deanoffice.oldentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "POSADI")
public class Position {

    @Id
    @Column(name= "ID")
    private int id;

    @Column(name = "NAZVA")
    String name;

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
