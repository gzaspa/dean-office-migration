package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;

@MappedSuperclass
@Getter
@Setter
public class NameEntity extends BaseEntity {
    @Column(name="name", nullable = false, length = 100)
    private String name;
}
