package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Faculty extends NameWithEngAndActiveEntity {
    @Column(name = "abbr", nullable = false, unique = true, length = 20)
    private String abbr;
    @Column(name = "dean", length = 70)
    private String dean;
}
