package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "NAKAZ_REASONS")
public class OrderReason {

    @Column(name = "NAME")
    String name;
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "ACTIVE1")
    @Type(type = "true_false")
    private boolean active;

    @Column(name = "KIND")
    private String kind;

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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
