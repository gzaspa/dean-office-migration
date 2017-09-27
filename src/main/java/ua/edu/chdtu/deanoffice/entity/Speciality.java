package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Speciality extends NameWithEngEntity {
    @Column(name="code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name="valid", nullable = false)
    private boolean valid = true;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
