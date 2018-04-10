package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Teacher extends Person {
    @ManyToOne
    @JoinColumn(nullable = false)
    private Department department;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Position position;
    @Column(name = "active", nullable = false)
    private boolean active = true;
    @Column(name = "scientific_degree")
    private String scientificDegree;
}
