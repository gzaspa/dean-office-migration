package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="current_year")
public class CurrentYear extends BaseEntity {
    @Column(name="curr_year")
    private int currYear;

    public int getCurrYear() {
        return currYear;
    }

    public void setCurrYear(int currYear) {
        this.currYear = currYear;
    }
}
