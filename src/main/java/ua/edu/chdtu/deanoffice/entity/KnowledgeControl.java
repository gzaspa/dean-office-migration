package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Getter
@Setter
@Table(name = "knowledge_control", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class KnowledgeControl extends NameWithEngEntity {
    @Column(name = "graded", nullable = false)
    private boolean graded;
}
