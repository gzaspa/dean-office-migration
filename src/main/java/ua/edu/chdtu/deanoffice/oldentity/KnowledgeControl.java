package ua.edu.chdtu.deanoffice.oldentity;

import javax.persistence.*;


@Entity
@Table(name="KNOWLEDGE_CONTROL")
public class KnowledgeControl {

    @Transient
    public static final int EXAM = 1;
    @Transient
    public static final int TEST = 2;
    @Transient
    public static final int COURSEWORK = 3;
    @Transient
    public static final int COURSE_PROJECT = 4;
    @Transient
    public static final int DIFFERENTIATED_TEST = 5;
    @Transient
    public static final int STATE_EXAM = 6;
    @Transient
    public static final int DIPLOMA_PROJECT = 7;
    @Transient
    public static final int PRACTICE = 8;
    @Transient
    public static final int FINAL_EXAMINATION = 7;

    @Id
    @Column(name = "ID" )
    private int id;

    @Column(name = "NAME" )
    private String name;

    @Column(name = "GRADE" )
    private boolean grade;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getGrade() {
        return grade;
    }

    public void setGrade(boolean grade) {
        this.grade = grade;
    }
}
