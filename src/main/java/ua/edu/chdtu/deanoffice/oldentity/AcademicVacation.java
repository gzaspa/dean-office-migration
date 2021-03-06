package ua.edu.chdtu.deanoffice.oldentity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "VIDP_STUDENTS")
public class AcademicVacation {

    @EmbeddedId
    private AcademicVacationPrimaryKey id;

    @ManyToOne
    @JoinColumn(name = "STUD_ID", insertable = false, updatable = false)
    private Student student;

    @Column(name = "FIRE_DATE", insertable = false, updatable = false)
    private Date vacationStart;

    @Column(name = "DATE_NAKAZU")
    private Date orderDate;

    @Column(name = "N_NAKAZU")
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "REASON_ID")
    private OrderReason orderReason;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @Column(name = "ZAYAVA_DATE")
    private Date applicationDate;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Date getVacationStart() {
        return vacationStart;
    }

    public void setVacationStart(Date vacationStart) {
        this.vacationStart = vacationStart;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderReason getOrderReason() {
        return orderReason;
    }

    public void setOrderReason(OrderReason orderReason) {
        this.orderReason = orderReason;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public AcademicVacationPrimaryKey getId() {
        return id;
    }

    public void setId(AcademicVacationPrimaryKey id) {
        this.id = id;
    }
}
