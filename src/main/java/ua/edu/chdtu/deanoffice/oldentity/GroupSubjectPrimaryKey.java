package ua.edu.chdtu.deanoffice.oldentity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class GroupSubjectPrimaryKey implements Serializable {

    @Column(name = "Subject_ID", insertable = false, updatable = false)
    protected int subjectId;

    @Column(name = "GROUP_ID", insertable = false, updatable = false)
    protected int groupId;


    public GroupSubjectPrimaryKey() {

    }

    public GroupSubjectPrimaryKey(int subjectId, int groupId) {
        this.subjectId = subjectId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupSubjectPrimaryKey that = (GroupSubjectPrimaryKey) o;

        return subjectId == that.subjectId && groupId == that.groupId;

    }

    @Override
    public int hashCode() {
        int result = subjectId;
        result = 31 * result + groupId;
        return result;
    }
}
