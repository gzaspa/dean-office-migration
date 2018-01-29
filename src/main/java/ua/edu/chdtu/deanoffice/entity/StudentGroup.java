package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "student_group")
public class StudentGroup extends NameWithActiveEntity {
    @ManyToOne
    private Specialization specialization;
    @Column(name="creation_year", nullable = false)
    private int creationYear;
    @Column(name="tuition_form", nullable = false, length = 10, columnDefinition = "varchar(10) default 'FULL_TIME'")
    @Enumerated(value = EnumType.STRING)
    private TuitionForm tuitionForm = TuitionForm.FULL_TIME;
    @Enumerated(value = EnumType.STRING)
    @Column(name="tuition_term", nullable = false, length = 10, columnDefinition = "varchar(10) default 'REGULAR'")
    private TuitionTerm tuitionTerm = TuitionTerm.REGULAR;
    @Column(name="study_semesters", nullable = false)
    private int studySemesters;
    @Column(name="study_years", nullable = false)
    private BigDecimal studyYears;
    @Column(name="begin_years", nullable = false)
    private int beginYears;//курс, з якого починає навчатись група
    //CURATOR
}
