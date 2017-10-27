package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class NameWithActiveEntity extends NameEntity {
    @Column(name="active", nullable = false)
    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}