package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "course"/*, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"semester", "kc_id", "hours", "course_name_id"})
}*/)
public class Course extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "course_name_id")
    private CourseName courseName;
    @Column(name = "semester", nullable = false)
    private Integer semester;
    @ManyToOne
    @JoinColumn(name = "kc_id")
    private KnowledgeControl knowledgeControl;
    @Column(name = "hours", nullable = false)
    private Integer hours;
    @Column(name = "hours_per_credit", nullable = false)
    private Integer hoursPerCredit;
    @Column(name = "credits", nullable = false, precision = 4, scale = 1)
    private BigDecimal credits;

    public boolean equals(Course other) {
        if (this.knowledgeControl == null || other.knowledgeControl == null) {
            return false;
        } else {
            return this.courseName.equals(other.getCourseName())
                    && this.semester.equals(other.getSemester())
                    && this.hours.equals(other.getHours())
                    && this.knowledgeControl.equals(other.getKnowledgeControl());
        }
    }
}
