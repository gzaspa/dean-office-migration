package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.*;

@MappedSuperclass
public class NameWithEngEntity extends NameEntity {
    @Column(name="name_eng", nullable = false, length = 100)
    private String nameEng;

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }
}
