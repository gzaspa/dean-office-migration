package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="knowledge_control")
public class KnowledgeControl extends NameWithEngEntity {
    @Column(name="has_grade", nullable = false)
    private boolean hasGrade;

    public boolean isHasGrade() {
        return hasGrade;
    }
}
