package ua.edu.chdtu.deanoffice.entity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="knowledge_control")
public class KnowledgeControl extends NameEntity {
    @Column(name="has_grade", nullable = false, columnDefinition="default 1")
    private boolean hasGrade;

    public boolean isHasGrade() {
        return hasGrade;
    }

    public void setHasGrade(boolean hasGrade) {
        this.hasGrade = hasGrade;
    }
}
