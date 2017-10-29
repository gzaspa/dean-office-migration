package ua.edu.chdtu.deanoffice.oldentity;

import java.io.Serializable;


public class GradePrimaryKey implements Serializable {
    protected int studentId;
    protected int subjectId;

    public GradePrimaryKey(){

    }

    public GradePrimaryKey(int student, int subject){
        this.studentId =student;
        this.subjectId =subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GradePrimaryKey that = (GradePrimaryKey) o;

        return studentId == that.studentId && subjectId == that.subjectId;

    }

    @Override
    public int hashCode() {
        int result = studentId;
        result = 31 * result + subjectId;
        return result;
    }
}
