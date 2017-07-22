package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Faculty extends NameEntity {
    @Column(name="abbr", nullable = false, unique = true, length = 20)
    private String abbr;
    @Column(name="active", nullable = false, columnDefinition="default 1")
    private boolean active;
    //DEAN

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
