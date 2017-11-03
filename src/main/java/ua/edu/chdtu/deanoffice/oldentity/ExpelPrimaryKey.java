package ua.edu.chdtu.deanoffice.oldentity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;

@Embeddable
public class ExpelPrimaryKey implements Serializable {

    @Column(name = "STUD_ID", insertable = false, updatable = false)
    protected int subjectId;

    @Column(name = "FIRE_DATE", insertable = false, updatable = false)
    protected Date groupId;

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public Date getGroupId() {
        return groupId;
    }

    public void setGroupId(Date groupId) {
        this.groupId = groupId;
    }
}
