package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "courses_for_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "student_group_id"})
})
public class CourseForGroup extends BaseEntity {
    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;
    @ManyToOne
    @JoinColumn(nullable = false, name = "student_group_id")
    private StudentGroup studentGroup;
    @ManyToOne
    private Teacher teacher;
    @Column(name = "exam_date")
    @Temporal(TemporalType.DATE)
    private Date examDate;
}
