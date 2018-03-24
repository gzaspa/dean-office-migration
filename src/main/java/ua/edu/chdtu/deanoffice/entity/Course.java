package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Course extends BaseEntity {
    @ManyToOne
    private CourseName courseName;
    @Column(name = "semester", nullable = false)
    private Integer semester;
    @ManyToOne
    @JoinColumn(name = "kc_id")
    private KnowledgeControl knowledgeControl;
    @Column(name = "hours", nullable = false)
    private Integer hours;
    @Column(name = "hoursPerCredit", nullable = false)
    private Integer hoursPerCredit;
    @Column(name = "credits", nullable = false, precision = 4, scale = 1)
    private BigDecimal credits;

    public boolean equals(Course other) {
        return this.courseName.equals(other.getCourseName())
                && this.semester.equals(other.getSemester())
                && this.knowledgeControl.equals(other.getKnowledgeControl())
                && this.hours.equals(other.getHours());
    }
}
