package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Getter
@Setter
@Entity
@Table(name = "acquired_competencies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"specialization_id", "year"})
})
public class AcquiredCompetencies extends BaseEntity {

    @Column(name = "competencies", nullable = false, columnDefinition = "character varying(8500)", length = 8500)
    private String competencies;
    @Column(name = "competencies_eng", nullable = false, columnDefinition = "character varying(8500)", length = 8500)
    private String competenciesEng;
    @JoinColumn(nullable = false, name = "specialization_id")
    @ManyToOne
    private Specialization specialization;
    @Column(nullable = false, name = "year")
    private Integer year;
}
