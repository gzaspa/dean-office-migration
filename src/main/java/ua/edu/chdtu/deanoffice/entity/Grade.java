package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="grade", uniqueConstraints={
        @UniqueConstraint(columnNames = {"course_id", "student_id"})
})
public class Grade extends BaseEntity {
    private Course course;
//    private Student student;
}
