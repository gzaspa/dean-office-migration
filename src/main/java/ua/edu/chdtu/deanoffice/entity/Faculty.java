package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Faculty extends NameWithEngEntity {
    @Column(name="abbr", nullable = false, unique = true, length = 20)
    private String abbr;
    @Column(name="active", nullable = false)
    private boolean active = true;
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
