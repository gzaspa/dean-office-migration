package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "SUBJECTS_FOR_GROUPS")
public class GroupSubject implements Serializable {

    @EmbeddedId
    private GroupSubjectPrimaryKey primaryKey;

    @ManyToOne
    @JoinColumn(name = "SUBJECT_ID", insertable = false, updatable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID", insertable = false, updatable = false)
    private Group group;

    @Column(name = "IN_DODATOK")
    @Type(type = "true_false")
    private boolean inDiplomaAddition;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isInDiplomaAddition() {
        return inDiplomaAddition;
    }

    public void setInDiplomaAddition(boolean inDiplomaAddition) {
        this.inDiplomaAddition = inDiplomaAddition;
    }

    public GroupSubjectPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(GroupSubjectPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }
}
