package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;


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

    @ManyToOne
    @JoinColumn(name = "TEACHER_ID")
    private Teacher teacher;

    @Column(name = "EXAM_DATE")
    private Date examDate;

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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }
}
