package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Getter
@Setter
@Table(name = "order_reason", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class OrderReason extends NameWithActiveEntity {
    @Column(name = "kind", nullable = false, length = 25)
    private String kind;
}
