package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="knowledge_control")
public class KnowledgeControl extends NameEntity {
    @Column(name="has_grade", nullable = false)
    private boolean hasGrade;

    public boolean isHasGrade() {
        return hasGrade;
    }
}
