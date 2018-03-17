package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "credits", nullable = false, precision = 4, scale = 1)
    private BigDecimal credits;


    @OneToMany(targetEntity = CourseForGroup.class, mappedBy = "course", fetch = FetchType.LAZY)
    private Set<CourseForGroup> coursesForGroups = new HashSet<>();


    public boolean equals(Course other) {
        return this.courseName.equals(other.getCourseName())
                && this.semester.equals(other.getSemester())
                && this.knowledgeControl.equals(other.getKnowledgeControl())
                && this.hours.equals(other.getHours());
    }
}
